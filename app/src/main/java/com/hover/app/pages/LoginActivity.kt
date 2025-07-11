package com.hover.app.pages

import LoginData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.SPUtils
import com.hover.app.utils.AuthService
import com.hover.app.utils.PublicKeyResponse
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException



class LoginActivity : ComponentActivity() {
    private val viewModel by lazy { LoginViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(
                onLoginSuccess = {
                    startActivity(Intent(this@LoginActivity, MapActivity::class.java))
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                    finish()
                }
            )
        }
    }
}

data class SavedUser(
    val username: String,
    val password: String
)

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: LoginData) : LoginState()
    data class Error(val exception: Throwable) : LoginState()
}
sealed class PublicKeyState{
    object Loading : PublicKeyState()
    data class Success(val publicKey: PublicKeyResponse) : PublicKeyState()
    data class Error(val exception: Throwable) : PublicKeyState()
}

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private var _publicKeyState = MutableStateFlow<PublicKeyState>(PublicKeyState.Loading)
    val publicKeyState: StateFlow<PublicKeyState> = _publicKeyState

    var savedUsersList by mutableStateOf(mutableListOf<SavedUser>())

    init {
        Log.d("LoginViewModel", "init")
        savedUsersList = loadSavedUsers().toMutableList()
        viewModelScope.launch {
            // 在协程作用域内调用挂起函数
            val publicKey = getPublicKey()
            Log.d("LoginActivity", "publicKey=$publicKey")
        }
    }

    private fun loadSavedUsers(): List<SavedUser> {
        val sp = SPUtils.getInstance("sp_name")
        val allEntries = sp.all // 获取所有存储的键值对
        println("allEntries=$allEntries")
        return allEntries
            .filterKeys { it.startsWith("loginuser_") } // 过滤出登录用户
            .mapNotNull { (key, value) ->
                val username = key.removePrefix("loginuser_")
                if (username.isNotEmpty() && value is String) {
                    SavedUser(username, value)
                } else {
                    null
                }
            }
    }

    // 执行登录操作
    fun performLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = AuthService.getPermissions()

                result.fold(
                    onSuccess = { response ->
                        Log.d(
                            "Login",
                            "✅ 请求成功! 状态: ${response.code}, 消息: ${response.message}"
                        )
                        Log.d("Login", "🛡️ 权限列表 (${response.data} 项):")
                        response.data.forEachIndexed { index, permission ->
                            Log.d(
                                "Login",
                                "${index + 1}. ${permission.name} - ${permission.description}"
                            )
                        }

                    },
                    onFailure = { error ->
                        Log.e("Login", "❌ 请求失败", error)
                        when (error) {
                            is ClientRequestException ->
                                Log.e("Login", "客户端错误: ${error.response.status}")

                            is ServerResponseException ->
                                Log.e("Login", "服务器错误: ${error.response.status}")

                            is IOException ->
                                Log.e("Login", "网络错误: ${error.message}")

                            else ->
                                Log.e("Login", "未知错误: ${error.message}")
                        }
                    }
                )


                // 可能抛出异常的代码
            } catch (e: Exception) {
                println("Error logging in: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    suspend fun login(username: String, password: String) {
        // 确保公钥已获取
        Log.d("-1--1-1-1-1-", "公钥未获取${publicKeyState.value}")
        val publicKey = when (val state = publicKeyState.value) {

            is PublicKeyState.Success -> {
                Log.d("Login", "公钥: ${state.publicKey}")
                state.publicKey
            }
            else -> {
                Log.d("Login----", "公钥未获取")
                // 如果公钥未获取，先获取公钥
//                getPublicKey()
                Log.d("Login----", "公钥212未获取")

                // 等待公钥获取完成
//                publicKeyState.filter { it is PublicKeyState.Success || it is PublicKeyState.Error }
//                    .first()
//                    .let {
//                        if (it is PublicKeyState.Success) it.publicKey
//                        else throw IllegalStateException("无法获取公钥")
//                    }
            }
        }

        _loginState.value = LoginState.Loading
        Log.d("Login-----", "登录: $username, $password, $publicKey")
        try {
            val result = AuthService.login(username, password)
            result.fold(
                onSuccess = { response ->
                    val token = response.data
                    // 保存token等操作
                    _loginState.value = LoginState.Success(token)
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(error)
                }
            )
        } catch (e: Exception) {
            _loginState.value = LoginState.Error(e)
        }
    }

    // 获取公钥
    suspend fun getPublicKey() {
        _publicKeyState.value = PublicKeyState.Loading

        try {
            val result = AuthService.getKey()
            result.fold(
                onSuccess = { response ->
                    _publicKeyState.value = PublicKeyState.Success(response)
                    Log.d("Login", "公钥: $response")
                },
                onFailure = { error ->
                    _publicKeyState.value = PublicKeyState.Error(error)
                }
            )
        } catch (e: Exception) {
            _publicKeyState.value = PublicKeyState.Error(e)
        }
    }
}



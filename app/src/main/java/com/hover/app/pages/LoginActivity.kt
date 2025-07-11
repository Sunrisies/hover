package com.hover.app.pages

import LoginData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.hover.app.utils.AuthService
import com.hover.app.utils.PublicKeyResponse
import com.hover.app.utils.RsaUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class LoginActivity : ComponentActivity() {
    private val viewModel by lazy { LoginViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            LoginScreen(
                onLoginSuccess = {
                    startActivity(Intent(this@LoginActivity, MapActivity::class.java))
//                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
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
    data class Success(val token: LoginData?) : LoginState()
    data class Error(val exception: Throwable) : LoginState()
}

sealed class PublicKeyState {
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
            getPublicKey()
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

    suspend fun login(username: String, password: String) {
        // 确保公钥已获取
        val publicKey = when (val state = publicKeyState.value) {
            is PublicKeyState.Success -> {
                Log.d("Login", "公钥: ${state.publicKey}")
                state.publicKey.data
            }

            else -> {
                ""
            }

        }
//  进行加密
        val publicKey1 = publicKey.replace("\n", "")
        var encryptedPassword = RsaUtils.encrypt(password, publicKey1)
        _loginState.value = LoginState.Loading
        try {
            val result = AuthService.login(username, encryptedPassword)
            result.fold(
                onSuccess = { response ->
                    val token = response.data
                    Log.d("login", "登录成功: ${response},$response")
                    if (response.data == null) {
                        _loginState.value = LoginState.Error(exception = Exception("登录失败"))
                        // 登录失败进行吐司提示
                        ToastUtils.showLong("登录失败")
                    } else {
                        _loginState.value = LoginState.Success(token)
                        ToastUtils.showLong("登录成功")
                    }
                    // 保存token等操作
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



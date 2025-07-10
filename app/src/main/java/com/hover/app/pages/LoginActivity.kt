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
import com.hover.app.utils.AuthService
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class LoginActivity : ComponentActivity() {
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

// 用于跟踪当前活动的输入字段
enum class Field {
    USERNAME, PASSWORD
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: LoginData) : LoginState()
    data class Error(val exception: Throwable) : LoginState()
}

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    //    var isLogin by mutableStateOf(false)
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    var savedUsersList by mutableStateOf(mutableListOf<SavedUser>())

    init {
        Log.d("LoginViewModel", "init")
        savedUsersList = loadSavedUsers().toMutableList()
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

    // 权限列表

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
        _loginState.value = LoginState.Loading

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
}



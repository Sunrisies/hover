package com.hover.app.pages

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

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLogin by mutableStateOf(false)


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

    fun login(username: String, password: String): Boolean {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. 执行登录
                val loginResult = AuthService.login(username, password)

                loginResult.fold(
                    onSuccess = { loginResponse ->
                        Log.d(
                            "Login",
                            "✅ 登录成功! 状态: ${loginResponse.code}, 消息: ${loginResponse.message}"
                        )

                        // 保存 token
                        val token = loginResponse.data
//                        saveToken(token)
                        Log.d("Login", "🔑 Token: $token")

                        // 2. 使用 token 获取权限
                        AuthService.getPermissions()

//                        permissionsResult.fold(
//                            onSuccess = { permissionsResponse ->
//                                Log.d("Login", "🛡️ 权限列表 (${permissionsResponse.data.size} 项):")
//                                permissionsResponse.data.forEachIndexed { index, permission ->
//                                    Log.d(
//                                        "Login",
//                                        "${index + 1}. ${permission.name} - ${permission.description}"
//                                    )
//                                }
//
//                                // 3. 保存用户信息
////                                saveUserInfo(loginResponse.data.userInfo)
//                            },
//                            onFailure = { error ->
//                                Log.e("Login", "❌ 获取权限失败", error)
////                                handleError(error)
//                            }
//                        )
                        isLogin = true
                    },
                    onFailure = { error ->
                        Log.e("Login", "❌ 登录失败", error)
//                        handleError(error)
                        isLogin = false
                    }
                )
            }

            // 可能抛出异常的代码
            catch (e: Exception) {
                println("Error logging in: ${e.message}")
                e.printStackTrace()
                isLogin = false

            }
        }
        return isLogin
    }

}



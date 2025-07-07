package com.hover.app.pages

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.hover.app.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableEdgeToEdge()
//         创建AuthManager实例
        val authManager = AuthManager(applicationContext)

        setContent {
            // 检查初始登录状态
            val isLoggedIn = runBlocking { authManager.isLoggedIn.first() }
            if (isLoggedIn) {
                LaunchedEffect(Unit) {
                    startActivity(Intent(this@MainActivity, MapActivity::class.java))
                    finish() // 结束当前 Activity
                }
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                // 显示登录界面
//                LoginScreen(
//                    onLoginSuccess = {
//                        // 登录成功后跳转到 MapActivity
//                        startActivity(Intent(this@MainActivity, MapActivity::class.java))
//                        finish() // 结束当前 Activity
//                    }
//                )
            }

        }
    }

}


package com.example.hover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hover.MapScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.example.hover.AuthManager
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE


//         创建AuthManager实例
        val authManager = AuthManager(applicationContext)

        setContent {
            val view = LocalView.current
            // 隐藏状态栏和导航栏
            val windowInsetsController = WindowInsetsControllerCompat(window, view)
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            val navController = rememberNavController()

            // 检查初始登录状态
            val isLoggedIn = runBlocking { authManager.isLoggedIn.first() }

            // 设置起始目的地
            val startDestination = if (!isLoggedIn) "map" else "login"

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                // 登录屏幕
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { navController.navigate("map") }
                    )
                }

                // 地图屏幕
                composable("map") {
                    MapScreen(
                        onLogout = {
                            runBlocking { authManager.logout() }
                            navController.navigate("login") {
                                popUpTo("map") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

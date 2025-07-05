package com.hover.app.pages

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE


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
// 如果已登录，直接跳转到 MapActivity
            if (!isLoggedIn) {
                LaunchedEffect(Unit) {
                    startActivity(Intent(this@MainActivity, MapActivity::class.java))
                    finish() // 结束当前 Activity
                }
            } else {
                // 显示登录界面
                LoginScreen(
                    onLoginSuccess = {
                        // 登录成功后跳转到 MapActivity
                        startActivity(Intent(this@MainActivity, MapActivity::class.java))
                        finish() // 结束当前 Activity
                    }
                )
            }


            // 设置起始目的地
//            val startDestination = if (!isLoggedIn) "map" else "login"
//
//            NavHost(
//                navController = navController,
//                startDestination = startDestination
//            ) {
//                // 登录屏幕
//                composable("login") {
//                    LoginScreen(
//                        onLoginSuccess = { navController.navigate("map") }
//                    )
//                }
//
//                // 地图屏幕
//                composable("map") {
//                    MapScreen(
//                        onLogout = {
//                            runBlocking { authManager.logout() }
//                            navController.navigate("login") {
//                                popUpTo("map") { inclusive = true }
//                            }
//                        }
//                    )
//                }
//            }
        }
    }
}
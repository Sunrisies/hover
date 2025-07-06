package com.hover.app.pages

import android.content.Intent
import android.content.pm.ActivityInfo
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableEdgeToEdge()

//         创建AuthManager实例
        val authManager = AuthManager(applicationContext)
        setFullScreenWithNotchSupport()
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
            if (isLoggedIn) {
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


    private fun setFullScreenWithNotchSupport() {
        // 隐藏系统状态栏和导航栏
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 设置全屏显示
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // 适配刘海屏 (API 28+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }
}

//import android.os.Build
//import android.os.Bundle
//import android.view.WindowInsets
//import android.view.WindowManager
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.platform.LocalView
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsCompat
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // 设置全屏显示并适配刘海屏
////        setFullScreenWithNotchSupport()
//
//        setContent {
//            NotchScreenDemo()
//        }
//    }
//
//}
//
//@Composable
//fun NotchScreenDemo() {
//    val view = LocalView.current
//    val density = LocalDensity.current
//
//    // 使用 WindowInsetsCompat 获取安全区域
//    val insets = remember {
//        WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
//    }
//
//    // 获取刘海屏安全区域
////    val cutoutSafePadding = remember {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            with(density) {
//                println("screenHeight: $density.density * view.height: ${view.height}")
//                println("12312: ${insets.getInsets(WindowInsetsCompat.Type.displayCutout()).left.toDp()}")
////                Insets(
////                    left = insets.getInsets(WindowInsetsCompat.Type.displayCutout()).left.toDp(),
////                    top = insets.getInsets(WindowInsetsCompat.Type.displayCutout()).top.toDp(),
////                    right = insets.getInsets(WindowInsetsCompat.Type.displayCutout()).right.toDp(),
////                    bottom = insets.getInsets(WindowInsetsCompat.Type.displayCutout()).bottom.toDp()
////                )
//            }
//        } else {
////            Insets(0.dp, 0.dp, 0.dp, 0.dp)
//        }
////    }
//
//    // 获取状态栏高度
//    val statusBarHeight = with(density) {
//        insets.getInsets(WindowInsetsCompat.Type.statusBars()).top.toDp()
//    }
//
//    // 获取导航栏高度
//    val navigationBarHeight = with(density) {
//        insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom.toDp()
//    }
//
//    // ... 其余UI代码
//}
//@Composable
//fun DeviceInfoCard() {
//    Card(
//        modifier = Modifier.padding(16.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color(0xFF3949AB)
//        )
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "设备信息",
//                color = Color.White,
//                fontSize = 20.sp,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//
//            Text(
//                text = "品牌: ${android.os.Build.BRAND}",
//                color = Color(0xFFC5CAE9)
//            )
//
//            Text(
//                text = "型号: ${android.os.Build.MODEL}",
//                color = Color(0xFFC5CAE9)
//            )
//
//            Text(
//                text = "Android版本: ${android.os.Build.VERSION.RELEASE}",
//                color = Color(0xFFC5CAE9)
//            )
//
//            Text(
//                text = "SDK版本: ${android.os.Build.VERSION.SDK_INT}",
//                color = Color(0xFFC5CAE9)
//            )
//        }
//    }
//}
//
////@Composable
////fun SafeAreaInfo(insets: Insets) {
////    Card(
////        modifier = Modifier.padding(16.dp),
////        colors = CardDefaults.cardColors(
////            containerColor = Color(0xFF5C6BC0)
////        )
////    ) {
////        Column(
////            modifier = Modifier.padding(16.dp),
////            horizontalAlignment = Alignment.CenterHorizontally
////        ) {
////            Text(
////                text = "安全区域",
////                color = Color.White,
////                fontSize = 20.sp,
////                modifier = Modifier.padding(bottom = 8.dp)
////            )
////
////            Text(
////                text = "左侧: ${insets.left.value}dp",
////                color = Color(0xFFE8EAF6)
////            )
////
////            Text(
////                text = "顶部: ${insets.top.value}dp",
////                color = Color(0xFFE8EAF6)
////            )
////
////            Text(
////                text = "右侧: ${insets.right.value}dp",
////                color = Color(0xFFE8EAF6)
////            )
////
////            Text(
////                text = "底部: ${insets.bottom.value}dp",
////                color = Color(0xFFE8EAF6)
////            )
////        }
////    }
////}
//
////data class Insets(
////    val left: Dp,
////    val top: Dp,
////    val right: Dp,
////    val bottom: Dp
////)
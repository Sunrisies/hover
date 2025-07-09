package com.hover.app.pages

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hover.app.R

class LoginActivity : ComponentActivity() {

    companion object {
        const val TAG = "AndroidMqttClient"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // 隐藏标题栏

        setContent {
            val view = LocalView.current
            // 隐藏状态栏和导航栏
//            val windowInsetsController = WindowInsetsControllerCompat(window, view)
//            windowInsetsController.systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
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


package com.hover.app.pages

import android.content.Intent
import android.os.Build
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat


class LoginActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        window.setDecorFitsSystemWindows(false)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.attributes.apply {



            // 设置视图内容是否显示到异形切口区域
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 如果最低版本支持小于28，需要增加判断，防止在低版本系统运行时找不到系统API崩溃
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }

        }

        setContent {

            LoginScreen(onLoginSuccess = {
                startActivity(Intent(this@LoginActivity, MapActivity::class.java))
                finish() })
        }
    }


}



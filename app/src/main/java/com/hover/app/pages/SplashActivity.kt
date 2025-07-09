package com.hover.app.pages

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hover.app.R


class SplashActivity : ComponentActivity() {

    // 最小显示时间（毫秒）
    private val MIN_SPLASH_DURATION = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen(onLoadingComplete = {
                // 启动主界面
                startActivity(Intent(this, LoginActivity::class.java))
                // 添加过渡动画
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            })
        }

        // 设置全屏显示
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

    }
}

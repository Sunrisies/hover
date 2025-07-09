package com.hover.app.pages


import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                finish() // 结束当前 Activity
            }

        }
    }

}

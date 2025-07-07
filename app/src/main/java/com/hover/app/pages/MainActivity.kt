package com.hover.app.pages

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hover.app.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Tab
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
//
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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

enum class ControlTab {
    Manual, GateFront, GateRear
}
data class MqttMessageData(
    val topic: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterGateControlScreen() {

    val context = LocalContext.current
    var isSwitched by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(ControlTab.Manual) }  // 添加当前标签状态
    LaunchedEffect(Unit) {
        println("连接MQTT服务器1111")
        // 订阅默认主题



    }
    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "圆明园水闸控制系统")
                    }
                }

            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp, 0.dp, 16.dp, 0.dp)
        ) {
            // 状态区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 设备状态
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "设备状态",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "离线",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // 闸门开闭状态
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "闸门开闭状态",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "停止",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // 信息区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 当前水位高度
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "当前水位高度",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "0 m",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // 闸门开启高度
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "闸门开启高度",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "0.195 m",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 控制区域
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 标签选择器
                    TabRow(
                        selectedTabIndex = when (currentTab) {
                            ControlTab.Manual -> 0
                            ControlTab.GateFront -> 1
                            ControlTab.GateRear -> 2
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Tab(
                            selected = currentTab == ControlTab.Manual,
                            onClick = { currentTab = ControlTab.Manual },
                            text = { Text("手动控制") }
                        )
                        Tab(
                            selected = currentTab == ControlTab.GateFront,
                            onClick = { currentTab = ControlTab.GateFront },
                            text = { Text("闸前水位控制") }
                        )
                        Tab(
                            selected = currentTab == ControlTab.GateRear,
                            onClick = { currentTab = ControlTab.GateRear },
                            text = { Text("闸后水位控制") }
                        )
                    }

                    // 动态内容区域
                    when (currentTab) {
                        ControlTab.Manual -> ManualControlContent(isSwitched)
                        ControlTab.GateFront -> GateFrontControlContent()
                        ControlTab.GateRear -> GateRearControlContent()
                    }
                }
            }

            // 历史数据查询
            Text(
                text = "历史数据查询 →",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        // 吐司提示
                        Toast
                            .makeText(context, "历史数据查询被点击", Toast.LENGTH_SHORT)
                            .show()

                        // 处理历史数据查询点击事件
                    }
            )

            // 底部区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                // 公司标志（如果需要）
                // Image(...)

                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = "技术支持",
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        // 电话图标（如果需要）
                        // Icon(...)
                        Text(
                            text = "400-123-4567",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ManualControlContent(isSwitched: Boolean) {
    var switched by remember { mutableStateOf(isSwitched) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 开关控制
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "开关控制",
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = switched,
                onCheckedChange = { switched = it },
                modifier = Modifier.padding(end = 16.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4F9DFF),
                    uncheckedThumbColor = Color.LightGray,
                    checkedTrackColor = Color(0xFFD1E6FF),
                    uncheckedTrackColor = Color(0xFFF0F0F0)
                )
            )
        }

//        Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)

        // 按钮区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ControlButton(
                text = "开闸",
                onClick = { /* 开闸点击事件 */ },
                backgroundColor = Color(0xFF4F9DFF),
//                icon = painterResource(R.drawable.arrow_upward_24px),
                iconDescription = "开闸图标",
                enabled = switched // 开关未开启时禁用按钮
            )
            ControlButton(
                text = "关闸",
                onClick = { /* 关闸点击事件 */ },
                backgroundColor = Color(0xFF8D8D8D),
//                icon = painterResource(R.drawable.arrow_downward_24px),
                iconDescription = "关闸图标",
                enabled = switched
            )
            ControlButton(
                text = "闸门停止",
                onClick = { /* 闸门停止点击事件 */ },
                backgroundColor = Color(0xFFF2D04F),
//                icon = painterResource(R.drawable.pause_24px),
                iconDescription = "关闸图标",
                enabled = switched
            )
        }
    }
}

@Composable
fun  ControlButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
//    icon: Painter,
    iconDescription: String,  // 添加图标描述参数
    iconSize: Dp = 80.dp, // 添加图标大小参数，默认为40.dp
    iconTintColor: Color = Color.White ,// 添加图标颜色参数，默认为白色
    enabled: Boolean = true // 添加 enabled 参数，默认为 true
) {
    println("enabled: $enabled")
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier
                .size(80.dp)
                .background(backgroundColor, CircleShape),
            contentPadding = PaddingValues(1.dp), // 调整内边距以适应图标大小
            enabled=enabled, // 禁用按钮
            onClick = { onClick() }) {
//            Icon(
//                painter = icon,
//                contentDescription = iconDescription,
//                tint = iconTintColor, // 使用 tint 参数设置图标颜色
//                modifier = Modifier.size(iconSize)
//            )
        }
        Text(
            text = text,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
@Composable
fun GateFrontControlContent() {
    Column {
        Text(
            text = "闸前水位控制",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // 在这里添加闸前水位控制的具体内容，比如滑动条控制水位等
    }
}

@Composable
fun GateRearControlContent() {
    Column {
        Text(
            text = "闸后水位控制",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // 在这里添加闸后水位控制的具体内容
    }
}

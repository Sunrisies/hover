package com.hover.app.pages

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hover.app.R


class SelectShipActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SelectShipScreen()
//            SplashScreen(onLoadingComplete = {
//                // 启动主界面
////                startActivity(Intent(this, LoginActivity::class.java))
//                startActivity(Intent(this, MapActivity::class.java))
//                // 添加过渡动画
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                finish()
//            })
        }

//        // 设置全屏显示
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectShipScreen() {

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 右侧：操作按钮
            IconButton(
                modifier = Modifier
                    .size(40.dp),
                onClick = { }
            )
            {
                Icon(
                    painter = painterResource(R.drawable.chevron_left_24px),
                    contentDescription = "设置",
                    tint = Color.Black, // 使用 tint 参数设置图标颜色
                    modifier = Modifier.size(40.dp)
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "选择船只", fontSize = 24.sp)
            }
        }
        var list =remember  {mutableStateListOf("船1", "船2", "船3", "船4", "船5", "船6", "船7", "船8", "船9", "船10")}
        remember { mutableStateListOf<String>() }
        var isRefreshing by remember { mutableStateOf(false) }
        var selectAll = remember { mutableStateOf(false) }
        if (selectAll.value) "取消全选" else "全选"
        var lastRefreshTime by rememberSaveable { mutableStateOf<Long?>(null) }
var state = PullToRefreshState()
        val timeText = lastRefreshTime?.let { diff ->
            when (val seconds = (System.currentTimeMillis() - diff) / 1000) {
                in 0..9 -> "刚刚刷新"
                in 10..59 -> "$seconds 秒前刷新"
                in 60..3599 -> "${seconds / 60} 分钟前刷新"
                else -> "${seconds / 3600} 小时前刷新"
            }
        } ?: "从未刷新"
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                // 触发刷新逻辑
                isRefreshing = true
                lastRefreshTime = System.currentTimeMillis()
                // 模拟异步刷新
                Handler(Looper.getMainLooper()).postDelayed({
                    list.add("New Ship ${System.currentTimeMillis()}")
                    isRefreshing = false
                }, 1500)
            },
            state = state,
            indicator = {
                // 只在下拉/刷新时显示
                PullToRefreshDefaults.Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = state.isRefreshing,
                    state = state
                )
                if (!state.isRefreshing && !state.isPullInProgress) {
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp),
                        color = Color.Gray
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 300.dp)) {
                item {
                    Text(
                        text = "上次刷新：$timeText",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
                items(list) { item ->
                    SelectShipItem(item, isSelected = true, onSelect = {})
                }
            }
        }
    }
}

@Composable
fun SelectShipItem(name: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
            .background(Color.Gray)
            .clickable(onClick = onSelect),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.padding(start = 18.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher__1),
                contentDescription = "图标",
                tint = Color.Unspecified,
                modifier = Modifier.size(60.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp)
//                .background(Color.White)
                .clickable(onClick = onSelect)
        ) {
            Text(text = name, fontSize = 24.sp)
            // 间隔
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "已选择1111111111111111111111111111111111111111111111111111111111111111111",
                fontSize = 16.sp
            )
        }
    }
}

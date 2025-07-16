package com.hover.app.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hover.app.R
import com.king.ultraswiperefresh.NestedScrollMode
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshFooter
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshHeader
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SelectShipActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SelectShipScreen()
        }
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
        var list = remember {
            mutableStateListOf(
                "船1",
                "船2",
                "船3",
                "船4",
                "船5",
                "船6",
                "船7",
                "船8",
                "船9",
                "船10"
            )
        }
        UltraSwipeRefreshSample(list)
    }
}

@Composable
fun UltraSwipeRefreshSample(list: List<String>) {

    val state = rememberUltraSwipeRefreshState()
    var itemCount by remember { mutableIntStateOf(20) }
    val coroutineScope = rememberCoroutineScope()

    UltraSwipeRefresh(
        state = state,
        onRefresh = {
            coroutineScope.launch {
                state.isRefreshing = true
                // TODO 刷新的逻辑处理，此处的延时只是为了演示效果
                delay(2000)
                itemCount = 20
                state.isRefreshing = false
            }
        },
        onLoadMore = {
            coroutineScope.launch {
                state.isLoading = true
                // TODO 加载更多的逻辑处理，此处的延时只是为了演示效果
                delay(2000)
                itemCount += 20
                state.isLoading = false
            }
        },
        modifier = Modifier.background(color = Color(0x7FEEEEEE)),
        headerScrollMode = NestedScrollMode.Translate,
        footerScrollMode = NestedScrollMode.Translate,
        headerIndicator = {
            ClassicRefreshHeader(it)
        },
        footerIndicator = {
            ClassicRefreshFooter(it)
        }
    ) {
        LazyColumn(Modifier.background(color = Color.White)) {

            items(list) { item ->
                SelectShipItem(item, isSelected = true, onSelect = {})
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

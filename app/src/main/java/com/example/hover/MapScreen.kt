package com.example.hover

import android.R.attr.onClick
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.logD
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import kotlin.collections.set



@Composable
fun MapScreen(onLogout: () -> Unit) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    configuration.screenWidthDp
    configuration.screenHeightDp
    val markers = remember { mutableStateListOf<Point>() }
    // 存储每个标记点的状态（用于拖动更新）
    val markerStates = remember { mutableStateMapOf<Int, Point>() }
    // 控制抽屉状态的变量
    var isDrawerOpen by remember { mutableStateOf(false) }
    var drawerState  = rememberDrawerState(initialValue = DrawerValue.Open)
    // 动画效果
    val drawerOffset by animateDpAsState(
        targetValue = if (isDrawerOpen) 0.dp else -300.dp,
        animationSpec = tween(durationMillis = 300),
        label = "drawerAnimation"
    )
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MapboxMapContent(markers,markerStates)
        // 在顶部添加自定义内容
        TopToolbar(
            onLogout = onLogout,
            onClearMarkers = {
                markers.clear()
                markerStates.clear()
            },
            markerCount = markers.size,
            onSettingsClick = { isDrawerOpen = true }
        )
        Box(modifier = Modifier.width(300.dp)
            .offset(x = -drawerOffset) // 从右侧滑入
            .padding(top=40.dp)
            .background(Color.White).align(Alignment.TopEnd),
            // 右边
            contentAlignment = Alignment.CenterEnd,
            ){
            SettingsDrawerContent(
                onClose = { isDrawerOpen = false },

            )

        }

    }
    // 控制抽屉打开/关闭
    LaunchedEffect(isDrawerOpen) {
        println("isDrawerOpen: $isDrawerOpen")
        if (isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

}



@Composable
fun MapboxMapContent(markers: MutableList<Point>,markerStates: MutableMap<Int, Point>) {
    val context = LocalContext.current
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(12.3)
                center(Point.fromLngLat(120.06022, 30.37818))
                pitch(0.0)
                bearing(0.0)
            }
        },
        scaleBar = {
            ScaleBar(
                alignment = Alignment.BottomStart,
            )
        },
        style = { MapStyle(style = "mapbox://styles/mapbox/satellite-streets-v11") },
        onMapClickListener = { clickedPoint ->
            println("onMapClick: $clickedPoint")

            markers.add(clickedPoint)
            // 初始化标记状态
            markerStates[markers.lastIndex] = clickedPoint
            println("添加标记在: $clickedPoint")
            false
        },

        ) {

        // 1. 绘制连接所有点的折线
        if (markers.size >= 2) {
            PolylineAnnotation(
                points = markers,
            ) {
                lineColor = Color(0xffee4e8b)
                lineWidth = 5.0
            }
        }

        markers.forEachIndexed { index, point ->
            key(index) {
                AddMarker(
                    point = markerStates[index] ?: point,
                    onPointUpdated = { newPoint ->
                        // 更新点位置
                        markerStates[index] = newPoint
                        // 更新主列表（触发折线重绘）
                        markers[index] = newPoint
                    },
                    onPointClicked = {
                        // 吐司
                        Toast.makeText(context, "点击了标记点: $point", Toast.LENGTH_LONG)
                            .show()
                    }
                )
            }
        }
    }
}
// 顶部工具栏组件
@Composable
fun TopToolbar(
    onLogout: () -> Unit,
    onClearMarkers: () -> Unit,
    markerCount: Int,
    onSettingsClick: () -> Unit
) {
    Box( modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // 应用模糊效果
                    renderEffect = BlurEffect(8f, 8f, TileMode.Decal)
                }
                // 添加一些背景颜色
                .background(Color.Black.copy(alpha = 0.3f)),
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp), // 添加一些内边距
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左侧：应用标题
            Text(
                text = "地图应用",
                color = Color.White
            )

            // 中间：标记计数
            Text(
                text = "标记点: $markerCount",
                color = Color.White
            )

            // 右侧：操作按钮
                IconButton(
                    modifier = Modifier
                        .size(80.dp),
                    onClick = onSettingsClick)
                {
                    Icon(
                        painter = painterResource(R.drawable.settings_24px),
                        contentDescription = "设置",
                        tint = Color.White, // 使用 tint 参数设置图标颜色
                        modifier = Modifier.size(80.dp)
                    )
                }
        }
    }

}

// 设置抽屉内容
@Composable
fun SettingsDrawerContent(
    onClose: () -> Unit
) {
    // 添加垂直滚动支持
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.surface) // 添加背景色
            .padding(16.dp)
            .clickable(enabled = true) {} // 消费点击事件
        ,
    ) {
        // 标题和关闭按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "设置",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(R.drawable.close_24px),
                    contentDescription = "关闭设置",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 设置选项
        SettingsOption(
//            icon = Icons.Default.Map,
            title = "地图类型",
            description = "选择地图显示样式"
        )

        SettingsOption(
//            icon = Icons.Default.Layers,
            title = "图层控制",
            description = "管理地图图层"
        )

        SettingsOption(
//            icon = Icons.Default.Notifications,
            title = "通知设置",
            description = "管理应用通知"
        )

        SettingsOption(
//            icon = Icons.Default.PrivacyTip,
            title = "隐私设置",
            description = "管理数据隐私选项"
        )

        Spacer(modifier = Modifier.weight(1f))

        // 底部操作按钮
        Button(
            onClick = { /* 保存设置 */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("保存设置")
        }
        Button(
            onClick = { /* 保存设置 */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("保存设置")
        }
        Button(
            onClick = { /* 保存设置 */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("保存设置")
        }
    }
}

// 设置项组件
@Composable
fun SettingsOption(
//    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = null,
//            tint = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.size(24.dp)
//        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))

//        Icon(
//            imageVector = Icons.Default.ArrowForwardIos,
//            contentDescription = "更多",
//            tint = MaterialTheme.colorScheme.onSurfaceVariant,
//            modifier = Modifier.size(16.dp)
//        )
    }
}


@Composable
fun AddMarker(
    point: Point,
    onPointClicked: (PointAnnotation) -> Unit,
    onPointUpdated: (Point) -> Unit
) {
    val marker = rememberIconImage(
        key = R.drawable.ic_blue_marker,
        painter = painterResource(R.drawable.ic_blue_marker)
    )

    PointAnnotation(point = point) {
        iconImage = marker
        interactionsState.onDragged { event ->
            // 更新点位置
            val newPoint = Point.fromLngLat(event.point.longitude(), event.point.latitude())
            onPointUpdated(newPoint)
            true
        }.onClicked { event ->
            onPointClicked(event)
            true
        }
            .also {
                it.isDraggable = true
            }
    }
}
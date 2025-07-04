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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
fun MapScreen(onLogout: () -> Unit,  viewModel: MapViewModel = viewModel()) {
    val configuration = LocalConfiguration.current
    configuration.screenWidthDp
    configuration.screenHeightDp
    val markers = remember { mutableStateListOf<Point>() }
    // 存储每个标记点的状态（用于拖动更新）
    val markerStates = remember { mutableStateMapOf<Int, Point>() }
    // 控制抽屉状态的变量
    // 动画效果
    val drawerOffset by animateDpAsState(
        targetValue = if (viewModel.isDrawerOpen) 0.dp else -300.dp,
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
            onSettingsClick = { viewModel.setIsDrawerOpen(true) }
        )
        Box(modifier = Modifier
            .width(300.dp)
            .offset(x = -drawerOffset) // 从右侧滑入
            .padding(top = 40.dp)
            .align(Alignment.TopEnd),
            // 右边
            contentAlignment = Alignment.CenterEnd,
            ){
            SettingsDrawerContent(
                onClose = { viewModel.setIsDrawerOpen(false)},

            )

        }

    }

}

class MapViewModel : androidx.lifecycle.ViewModel() {
    var username by mutableStateOf("21121")
    var password by mutableStateOf("")
    var isDrawerOpen by mutableStateOf(false)

    fun setIsDrawerOpen(isOpen: Boolean) {
        isDrawerOpen = isOpen
    }

    fun isValidCredentials(): Boolean {
        // 这里添加实际的验证逻辑
        return username.isNotBlank() && password.length >= 6
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
    var selectedSetting by remember { mutableStateOf("船速") }
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
    ){
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(50.dp)
                .verticalScroll(scrollState)
                .background(Color(0x88040408)) // 添加背景色
                .clickable(enabled = true) {} // 消费点击事件
            ,
        ) {
            val menuItems = listOf("船速", "安全", "地图", "通用", "关于")
            val selectedItem = remember { mutableStateOf(4) } // 假设 "关于" 是选中的

            Column {
                menuItems.forEachIndexed { index, item ->
                    SettingCategoryItem(
                        title = item,
                        isSelected = index == selectedItem.value,
                        onClick = { selectedItem.value = index
                            selectedSetting = item
                        }
                    )
                }
            }


        }
        // 分隔线
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        // 右侧：具体设置内容
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // 根据选中的设置项显示不同的内容
            when (selectedSetting) {
                "船速" -> MapTypeSettings()
                "安全" -> LayerControlSettings()
//                "地图" -> NotificationSettings()
//                "通用" -> PrivacySettings()
//                "关于" -> AccountSettings()
//                "关于应用" -> AboutAppSettings()
            }

        }
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

@Composable
fun SettingCategoryItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically, // 显式设置垂直居中
                        horizontalArrangement = Arrangement.Center // 水平居中
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(
                    if (isSelected) Color(0xFF0066CC) else Color.Transparent
                )
                .align(Alignment.CenterVertically) // 确保指示条垂直居中
        )
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp),
                color = if (isSelected) Color(0xFF0066CC) else Color.Black,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )

    }
}

@Composable
fun MapTypeSettings() {
    Column {
        Text(
            text = "选择地图显示样式",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 船速选项
        MapTypeOption("标准地图", true)
        MapTypeOption("卫星地图", false)
        MapTypeOption("地形地图", false)
        MapTypeOption("夜间模式", false)
    }
}

@Composable
fun MapTypeOption(name: String, isSelected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* 选择船速 */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { /* 选择船速 */ }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun LayerControlSettings() {
    Column {
        Text(
            text = "管理地图图层",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 图层选项
        LayerOption("交通状况", true)
        LayerOption("兴趣点", true)
        LayerOption("地形等高线", false)
        LayerOption("3D建筑", false)
    }
}

@Composable
fun LayerOption(name: String, isEnabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Switch(
            checked = isEnabled,
            onCheckedChange = { /* 切换图层状态 */ }
        )
    }
}
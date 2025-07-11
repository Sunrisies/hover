package com.hover.app.pages

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hover.app.R
import com.hover.app.ui.CustomButton
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation

@Composable
fun MapScreen(onLogout: () -> Unit, viewModel: MapViewModel = viewModel()) {
    val configuration = LocalConfiguration.current
    configuration.screenWidthDp
    configuration.screenHeightDp
    val markers = remember { mutableStateListOf<Point>() }
    // 存储每个标记点的状态（用于拖动更新）
    val markerStates = remember { mutableStateMapOf<Int, Point>() }
    // 动画效果
    val drawerOffset by animateDpAsState(
        targetValue = if (viewModel.isDrawerOpen) 0.dp else -300.dp,
        animationSpec = tween(durationMillis = 300),
        label = "drawerAnimation"
    )
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MapboxMapContent(markers, markerStates)
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
        if (viewModel.isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            viewModel.setIsDrawerOpen(false)
                        }
                    }
            )
        }

        Box(
            modifier = Modifier
                .width(300.dp)
                .offset(x = -drawerOffset) // 从右侧滑入
                .padding(top = 40.dp)
                .align(Alignment.TopEnd),
            // 右边
            contentAlignment = Alignment.CenterEnd,
        ) {
            SettingsDrawerContent(onLogout)
        }

    }

}

class MapViewModel : ViewModel() {
    // 控制抽屉状态的变量
    var isDrawerOpen by mutableStateOf(true)

    fun setIsDrawerOpen(isOpen: Boolean) {
        isDrawerOpen = isOpen
    }


}

@Composable
fun MapboxMapContent(
    markers: MutableList<Point>,
    markerStates: MutableMap<Int, Point>,
    viewModel: MapViewModel = viewModel()
) {
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
        style = { MapStyle(style = Style.STANDARD_SATELLITE) },
//        style = { MapStyle(style = "mapbox://styles/mapbox/satellite-streets-v11") },
//        style = { MapStyle(style = "mapbox://styles/mapbox/satellite-streets-v11") },
        onMapClickListener = { clickedPoint ->
            println("onMapClick: $clickedPoint")
            println("isDrawerOpen: ${viewModel.isDrawerOpen}")
            markers.add(clickedPoint)
            markerStates[markers.lastIndex] = clickedPoint
            println("添加标记在: $clickedPoint")


            false
        },

        ) {
//         @OptIn(MapboxDelicateApi::class)
        MapEffect(Unit) {
            ""
//             println("MapEffect: ${it.toString}")
//             val imageSource: ImageSource = it.mapboxMap.getStyle()
//             imageSource.updateImage(bitmap)
        }
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
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
                onClick = onSettingsClick
            )
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
    onLogout: () -> Unit,
) {
    // 添加垂直滚动支持
    val scrollState = rememberScrollState()
    var selectedSetting by remember { mutableStateOf("通用") } // 当前选中的设置项
    val menuItems = listOf("船速", "安全", "地图", "通用", "关于") // 当前左侧数据列表

    Row(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(50.dp)
                .verticalScroll(scrollState)
                .background(Color(0x88040408)) // 添加背景色
                .clickable(enabled = true) {}, // 消费点击事件
        ) {
            Column {
                menuItems.forEachIndexed { index, item ->
                    SettingCategoryItem(
                        title = item,
                        isSelected = item == selectedSetting,
                        onClick = {
                            selectedSetting = item
                        }
                    )
                }
            }


        }

        // 右侧：具体设置内容
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    Color.Black.copy(alpha = 0.80f)
                )
                .verticalScroll(rememberScrollState())
        ) {

            // 根据选中的设置项显示不同的内容
            when (selectedSetting) {
                "船速" -> ShipSpeed(
                    onConfirm = { /* 确认船速 */ }
                )

                "安全" -> SecureSettings(
                    onConfirm = { /* 确认安全设置 */ }
                )

                "地图" -> MapTypeSettings()
                "通用" -> GeneralSettings()
                "关于" -> AboutSettings(onLogout)
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureSettings(onConfirm: () -> Unit) {
    var actionText by remember { mutableStateOf("无动作") }
    var expanded by remember { mutableStateOf(false) }
    val actions = listOf("无动作", "发出警报", "自动返航")

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "低电量提醒",
            color = Color.White,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.width(8.dp))


        CustomTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = "提醒电量",
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )
            },
            modifier = Modifier
                .width(80.dp)
                .height(30.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))


        Text(
            text = "V",
            color = Color.White,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.width(8.dp))

        CustomButton(text = "确定", onClick = { onConfirm() })
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "低电量动作",
            color = Color.White,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 下拉菜单
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                readOnly = true,
                value = actionText,
                onValueChange = { },
                modifier = Modifier
                    .menuAnchor()
                    .width(150.dp),
                trailingIcon = { Text(text = "▼", color = Color.Gray) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                actions.forEach { action ->
                    DropdownMenuItem(
                        text = { Text(text = action) },
                        onClick = {
                            actionText = action
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    var focused by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    {
        if (focused) {
            keyboardController?.hide()
            focused = false
        }
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, RoundedCornerShape(4.dp))
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    focused = true
                    println("点击了")

                    keyboardController?.show()
                }
            },
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (value.isEmpty()) {
                    placeholder()
                }
                innerTextField()
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        )
    )
}

@Composable
fun ShipSpeed(
    onConfirm: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .background(Color(0xFF040404))
            .padding(8.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                // 点击屏幕任何地方时清除焦点并隐藏键盘
                focusManager.clearFocus()
                keyboardController?.hide()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            text = "自动速度",
            color = Color.White,
            fontWeight = FontWeight.Normal
        )

        CustomTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = "自动船速",
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )
            },
            modifier = Modifier
                .width(80.dp)
                .height(30.dp)
        )


        Text(
            text = "m/s",
            color = Color.White,
            fontWeight = FontWeight.Normal
        )
        CustomButton(text = "确定", onClick = { onConfirm() })
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
        iconAnchor = IconAnchor.BOTTOM
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
            color = if (isSelected) Color(0xFF0066CC) else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        )

    }
}

@Composable
fun MapTypeSettings() {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = "选择地图显示样式",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
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
        Checkbox(
            checked = isSelected,
            onCheckedChange = { /* 选择船速 */ },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF0066CC),
                uncheckedColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}


@Composable
fun LayerOption(name: String, isEnabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        Switch(
            checked = isEnabled,
            onCheckedChange = { /* 切换图层状态 */ },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF0066CC),
                uncheckedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0066CC).copy(alpha = 0.3f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun AboutSettings(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CustomButton(text = "退出登录", onClick = { onLogout() })
    }
}

@Composable
fun GeneralSettings() {
    var isHardDecode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "重复次数", color = Color.White)
            CustomTextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        text = "重复次数",
                        color = Color.White,
                        fontWeight = FontWeight.Normal
                    )
                },
                modifier = Modifier
                    .width(80.dp)
                    .height(30.dp)
            )
            CustomButton(text = "确定", onClick = { })
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "视频解码",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Checkbox(
                checked = isHardDecode,
                onCheckedChange = { isHardDecode = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF0066CC),
                    uncheckedColor = Color.Gray
                )
            )


            Text(
                text = "硬解",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Checkbox(
                checked = isHardDecode,
                onCheckedChange = { isHardDecode = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF0066CC),
                    uncheckedColor = Color.Gray
                )
            )


            Text(
                text = "软解",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

        }
        LayerOption("界面常亮", true)

        LayerOption("语音播报", false)

        LayerOption("显示航迹", true)
    }
}
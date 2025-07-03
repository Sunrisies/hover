package com.example.hover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.logD


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContent {
            val view = LocalView.current
            // 隐藏状态栏和导航栏
            val windowInsetsController = WindowInsetsControllerCompat(window, view)
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            MapScreen()
        }
    }
}

@Composable
fun MapScreen(){
    val configuration = LocalConfiguration.current
    configuration.screenWidthDp
    configuration.screenHeightDp
    val markers = remember { mutableStateListOf<Point>() }
    // 存储每个标记点的状态（用于拖动更新）
    val markerStates = remember { mutableStateMapOf<Int, Point>() }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
            style = { MapStyle(style = "mapbox://styles/mapbox/satellite-streets-v11") },
            onMapClickListener = { clickedPoint ->
                println("onMapClick: $clickedPoint")
                logD(
                    this.javaClass.simpleName,
                    "onMapClick: $clickedPoint"
                )
                markers.add(clickedPoint)
                // 初始化标记状态
                markerStates[markers.lastIndex] = clickedPoint
                println("添加标记在: $clickedPoint")
                false
            },
        ){
            markers.forEachIndexed { index, point ->
                key(index) {
                    AddMarker(point = point)
                }
            }
        }
    }
}

@Composable
fun AddMarker(point: Point) {
    val marker = rememberIconImage(
        key = R.drawable.ic_blue_marker,
        painter = painterResource(R.drawable.ic_blue_marker)
    )

    PointAnnotation(point = point) {
        iconImage = marker
//        textField = "标记位置"
        interactionsState.onClicked {
            println("标记被点击: $point")
            true
        }.onDragged {
            println("标记被拖动: $point")
        }.also { it.isDraggable = true }
    }
}
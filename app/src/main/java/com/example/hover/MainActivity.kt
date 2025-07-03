package com.example.hover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import androidx.compose.ui.platform.LocalConfiguration
public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContent {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val screenHeight = configuration.screenHeightDp

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
                    style = { MapStyle(style = "mapbox://styles/mapbox/satellite-streets-v11") }
                )
            }
        }
    }
}
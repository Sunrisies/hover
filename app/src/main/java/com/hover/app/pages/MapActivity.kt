package com.hover.app.pages

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.color.colorTheme
import com.mapbox.maps.extension.style.style
import kotlinx.coroutines.runBlocking


class MapActivity : ComponentActivity() {
    private lateinit var mapboxMap: MapboxMap

    private var atmosphereUseTheme = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // 创建AuthManager实例
        val authManager = AuthManager(applicationContext)
//        MapView(
//            this,
//            MapInitOptions(
//                this, cameraOptions = START_CAMERA_POSITION
//            )
//        ).also { mapView ->
//            mapboxMap = mapView.mapboxMap
//            setContentView(mapView)
//            mapboxMap.loadStyle(
//                style(Style.MAPBOX_STREETS) {
//                    +colorTheme(base64 = BASE64_ENCODED_RED_THEME)
//                    +atmosphere {
//                        color(COLOR_GREEN)
//                    }
//                }
//            )
//        }
        setContent {
            val view = LocalView.current
            // 隐藏状态栏和导航栏
            val windowInsetsController = WindowInsetsControllerCompat(window, view)
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

            // 显示地图界面
            MapScreen(
                onLogout = {
                    runBlocking { authManager.logout() }
                    // 返回登录界面
                    startActivity(Intent(this@MapActivity, MainActivity::class.java))
                    finish() // 结束当前 Activity
                }
            )
        }
    }




    companion object {
        /**
         * Base64 encoded version of a custom LUT (Look-Up Table) image.
         * LUT image can be created by following [this](https://docs.mapbox.com/help/tutorials/create-a-custom-color-theme/) guide.
         *
         * To convert your LUT image to a Base64 string, you can use an online tool or a script.
         * For example, you can use the following command in a terminal:
         *
         * ```sh
         * base64 -i path/to/your/lut-image.png -o output.txt
         * ```
         */
        private const val BASE64_ENCODED_RED_THEME =
            "iVBORw0KGgoAAAANSUhEUgAABAAAAAAgCAYAAACM/gqmAAAAAXNSR0IArs4c6QAABSFJREFUeF7t3cFO40AQAFHnBv//wSAEEgmJPeUDsid5h9VqtcMiZsfdPdXVzmVZlo+3ZVm+fr3//L7257Lm778x+prL1ff0/b//H+z/4/M4OkuP/n70Nc7f+nnb+yzb//sY6vxt5xXPn+dP/aH+GsXJekb25izxR/ypZ6ucUefv9g4z2jPP3/HPHwAAgABAABgACIACkAAsAL1SD4yKWQAUAHUBdAG8buKNYoYL8PEX4FcHQAAAAAAAAAAAAAAAAAAAAAAA8LAeGF1mABAABAABQACQbZP7+hk5AwACAAAAAAAAAAAAAAAAAAAAAAAA4EE9AICMx4QBAAAAAAAANgvJsxGQV1dA/PxmMEtxU9YoABQACoC5CgDxX/wvsb2sEf/Ff/Ff/N96l5n73+/5YAB4CeBqx2VvMqXgUfD2npkzBCAXEBeQcrkoa5x/FxAXEBcQF5A2Wy3/t32qNYr8I//Mln+MABgBMAJgBMAIgBEAIwBGAIwAGAEwAmAE4K4eAGCNQIw+qQ0AmQ+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB/6gEABAB5RgACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN/UAAPKcAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgEFNODICRtDkDO/gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOhvlPUWem+h9xKQ+V4CUt9wO6KZnn/Pv+ff8z/bW5DFP59CUnJbWSP+iX/iX78znqED/urxnwHAAGAAMAAYAAwABgADgAHAAGAAMAAYAAwABgADoNMcHUAdQAQcAUfAe8xEwH0O86t3IPz8OvClu17WqD/UH+oP9cf1Gdia01d/LQsDgAHAAGAAMAAYAAwABgADgAHAAGAAMAAYAAwABkCnSQwABgACj8Aj8D1mItAMAB1wHfDS3S5r5F/5V/6Vf3XAW12h/mIArHY89iZTAAQA2XtmBKAWqOslyf4rgBXACmAFcIur8k/bJ/mnQTr5V/6Vf+fKv0YAjAAYATACYATACIARACMARgCMABgBMAJgBMAIgBEAIwCdZuiA64AjwAgwAtxjpg6cDlztLlLA7/Pr1gueyr56/jx/5ZzUNeof9Y/6R/0zk4HGAGAAMAAYAAwABgADgAHAAGAAMAAYAAwABgADgAHQaQ4DgAGAgCPgCHiPmTqQOpC1u8gAYACMjAf5V/6Vf+XfmTrQ8l97v8Z/5X8GAAOAAcAAYAAwABgADAAGAAOAAcAAYAAwABgADIBO0xgADAAdCB0IHYgeMxkADAAdkGM7IPbf/pfuWlmj/lH/qH/UPzMZGAwABgADgAHAAGAAMAAYAAwABgADgAHAAGAAMAAYAJ3mMAAYAAg4Ao6A95jJAGAA6EDrQJfuclkj/8q/8q/8O1MHWv47Nv8xABgADAAGAAOAAcAAYAAwABgADAAGAAOAAcAAYAB0msYAYADoQOhA6ED0mMkAYADogBzbAbH/9r/YFWWN+kf9o/5R/8xkYDAAGAAMAAYAA4ABwABgADAAGAAMAAYAA4ABwABgAHSawwBgACDgCDgC3mMmA4ABoAOtA126y2WN/Cv/yr/y70wdaPnv2PzHAGAAMAAYAAwABgADgAHAAGAAMAAYAAwABgADgAHQaRoDgAGgA6EDoQPRYyYDgAGgA3JsB8T+2/9iV5Q16h/1j/pH/TOTgcEAYAAwABgADAAGAAOAAcAAYAAwABgADAAGAAPgyQ2AT4NBIB3ew5dkAAAAAElFTkSuQmCC"
        private const val COLOR_USE_THEME_DEFAULT = "default"
        private const val COLOR_USE_THEME_NONE = "none"
        private const val COLOR_GREEN = "#00ff00"
        private const val LATITUDE = 40.72
        private const val LONGITUDE = -73.99
        private val CENTER = Point.fromLngLat(LONGITUDE, LATITUDE)
        private val START_CAMERA_POSITION = cameraOptions {
            center(CENTER)
            zoom(2.0)
            pitch(45.0)
        }
    }
}

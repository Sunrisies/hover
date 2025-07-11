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
import androidx.compose.runtime.Composable
import com.hover.app.R
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Composable
fun SelectShipScreen(){

    Column(){
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp,8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            // 右侧：操作按钮
            IconButton(
                modifier = Modifier
                    .size(40.dp),
                onClick = {  }
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
            ){
                Text(text="选择船只",fontSize = 24.sp)
            }
        }

        Column(){
            SelectShipItem("船1",isSelected = true,onSelect = {})
            SelectShipItem("船2",isSelected = false,onSelect = {})
            SelectShipItem("船3",isSelected = false,onSelect = {})
            SelectShipItem("船3",isSelected = false,onSelect = {})

            SelectShipItem("船3",isSelected = false,onSelect = {})

            SelectShipItem("船3",isSelected = false,onSelect = {})

            SelectShipItem("船3",isSelected = false,onSelect = {})

        }
    }
}

@Composable
fun SelectShipItem(name:String,isSelected:Boolean,onSelect:()->Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp,8.dp)
            .background(Color.Gray)
            .clickable(onClick = onSelect),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
Box( modifier = Modifier.padding(start=18.dp)){
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
                .padding(16.dp,8.dp)
//                .background(Color.White)
                .clickable(onClick = onSelect)
        ){
            Text(text=name,fontSize = 24.sp)
            // 间隔
            Spacer(modifier = Modifier.height(8.dp))
            Text(text="已选择1111111111111111111111111111111111111111111111111111111111111111111",fontSize = 16.sp)
        }
    }
}

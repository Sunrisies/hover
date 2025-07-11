package com.hover.app.pages

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hover.app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onLoadingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 控制动画状态
    var animationState by remember { mutableStateOf(0f) }
    // 添加退出动画状态
    var exitAnimation by remember { mutableStateOf(false) }
    val exitAlpha by animateFloatAsState(
        targetValue = if (exitAnimation) 0f else 1f,
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    )
    // 动画控制器
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // 模拟加载完成
    LaunchedEffect(Unit) {
        // 启动动画
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        ) { value, _ -> animationState = value }
        // 模拟加载过程
        delay(2000)
        exitAnimation = true
        delay(500) // 等待退出动画完成

        // 加载完成后回调
        onLoadingComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .alpha(exitAlpha), // 应用退出透明度,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Logo 图片 - 带缩放动画
            Image(
                painter = painterResource(id = R.drawable.ic_launcher__1),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(0.8f + animationState * 0.2f) // 缩放动画
                    .alpha(animationState) // 淡入效果
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 应用名称 - 带淡入动画
            Text(
                text = stringResource(id = R.string.app_name),
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(animationState),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 加载进度条 - 带脉冲动画
            CircularProgressIndicator(
                color = Color.Black,
                trackColor = Color.Black.copy(alpha = 0.2f),
                modifier = Modifier
                    .size(48.dp)
                    .scale(pulse) // 脉冲动画
            )
        }

        // 版本信息 - 底部对齐
        Text(
            text = stringResource(id = R.string.version_info),
            color = Color.Black.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .alpha(animationState) // 淡入效果
        )
    }
}

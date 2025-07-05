package com.hover.app.ui
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    text: String, // 按钮上显示的文本内容
    onClick: () -> Unit, // 按钮点击事件的回调函数
    modifier: Modifier = Modifier, // 用于自定义按钮的修饰符，默认为 Modifier
    shape: Shape = RoundedCornerShape(6.dp), // 按钮的形状，默认为圆角矩形
    containerColor: Color = Color(0xFF0066CC), // 按钮的背景颜色，默认为蓝色
    contentColor: Color = Color.White, // 按钮的文本颜色，默认为白色
    contentPadding: PaddingValues = PaddingValues(0.dp), // 按钮内容的内边距，默认为0
    innerPadding: PaddingValues = PaddingValues(4.dp) // 文本的内边距，默认为4.dp
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .wrapContentSize()
            .then(modifier),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = contentPadding
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
package com.hover.app.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.SPUtils
import com.hover.app.R
import com.hover.app.ui.CustomButton
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    // 观察登录状态变化
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                // 保存用户名密码等操作
                SPUtils.getInstance("sp_name")
                    .put("loginuser_${viewModel.username}", viewModel.password)
                onLoginSuccess()
            }

            is LoginState.Error -> {
                // 显示错误提示
                val error = (loginState as LoginState.Error).exception
                Log.e("Login", "登录失败", error)
            }

            else -> {}
        }
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    KeyboardActions(
        onNext = { passwordFocusRequester.requestFocus() },
        onDone = {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null  // 禁用涟漪效果
            ) {
                // 点击屏幕任何地方时清除焦点并隐藏键盘
                focusManager.clearFocus()
                keyboardController?.hide()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp
                )
                .background(Color.White)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    // 阻止点击表单区域时关闭键盘
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "地图应用登录",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            var dropdownItems = viewModel.savedUsersList.map { user ->
                user.username
            }
            if (dropdownItems.isEmpty()) {
                dropdownItems = listOf("无保存的用户")
            }
            UsernameTextField(
                viewModel = viewModel,
                usernameFocusRequester = usernameFocusRequester,
                dropdownItems = dropdownItems,
            )


            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(
                viewModel = viewModel,
                passwordFocusRequester = passwordFocusRequester,
            )


            Spacer(modifier = Modifier.height(24.dp))
            val coroutineScope = rememberCoroutineScope()
            CustomButton(text = "登录", onClick = {  // 登录逻辑...
                coroutineScope.launch {
                    viewModel.login(viewModel.username, viewModel.password)
                }

                // 登录后清除焦点
                focusManager.clearFocus()
                keyboardController?.hide()
            }
            )

        }
    }
}

@Composable
fun UsernameTextField(
    viewModel: LoginViewModel, // 替换为你的ViewModel类型
    usernameFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    dropdownItems: List<String>, // 下拉列表的选项
) {

    var expanded by remember { mutableStateOf(false) } // 控制下拉列表的展开状态
    OutlinedTextField(
        value = viewModel.username,
        onValueChange = {
            viewModel.username = it
//            expanded = it.isNotEmpty()
        },
        label = { Text("用户名") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        trailingIcon = {
            // 下拉图标
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Add else Icons.Default.ArrowDropDown,
                    contentDescription = "下拉菜单"
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(usernameFocusRequester)
            .onFocusChanged { focusState ->
            }
    )
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd,
    ) {
        // 下拉列表
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(TextFieldDefaults.MinWidth)
                .background(Color.White)
                .height(100.dp),
        ) {
            dropdownItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {

                        viewModel.username = item // 更新输入框内容
                        viewModel.savedUsersList.forEach { user ->
                            if (user.username == item) {
                                viewModel.password = user.password
                                // 调用登录接口
                                coroutineScope.launch {
                                    viewModel.login(item, user.password)
                                }
                            }
                        }
                        expanded = false // 关闭下拉菜单
                    },
                )
            }
        }
    }

}


@Composable
fun PasswordTextField(
    viewModel: LoginViewModel, // 替换为你的ViewModel类型
    passwordFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    // 控制密码是否可见的状态
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = viewModel.password,
        onValueChange = { viewModel.password = it },
        label = { Text("密码") },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None // 显示明文
        } else {
            PasswordVisualTransformation() // 显示星号
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                // 处理完成操作
            }
        ),
        trailingIcon = {
            // 密码可见性切换按钮
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = if (passwordVisible) {
                        painterResource(R.drawable.visibility_24px)
                    } else {
                        painterResource(R.drawable.visibility_off_24px)
                    },
                    contentDescription = if (passwordVisible) {
                        "隐藏密码"
                    } else {
                        "显示密码"
                    }
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(passwordFocusRequester)

    )
}

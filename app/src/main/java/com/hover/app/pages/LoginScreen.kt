package com.hover.app.pages
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    // 用于跟踪哪个输入框有焦点
    var activeField by remember { mutableStateOf<Field?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                // 点击屏幕任何地方时清除焦点并隐藏键盘
                focusManager.clearFocus()
                keyboardController?.hide()
                activeField = null
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(32.dp)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    // 阻止点击表单区域时关闭键盘
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "地图应用登录",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
                label = { Text("用户名") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(usernameFocusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            activeField = Field.USERNAME
                        } else if (activeField == Field.USERNAME) {
                            activeField = null
                        }
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("密码") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            activeField = Field.PASSWORD
                        } else if (activeField == Field.PASSWORD) {
                            activeField = null
                        }
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // 登录逻辑...
                    if (viewModel.isValidCredentials()) {
                        onLoginSuccess()
                    }
                    // 登录后清除焦点
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("登录")
            }
        }
    }
}

// 用于跟踪当前活动的输入字段
private enum class Field {
    USERNAME, PASSWORD
}
class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    fun isValidCredentials(): Boolean {
        // 这里添加实际的验证逻辑
        return username.isNotBlank() && password.length >= 6
    }
}

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.SPUtils
import com.hover.app.R
import com.hover.app.ui.CustomButton
import com.hover.app.utils.AuthService
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException



@OptIn(ExperimentalMaterial3Api::class)
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
//   var savedUsers = viewModel.getSavedUsers()
//    Log.d("Login", "savedUsers=$savedUsers")
//     处理键盘操作（下一步/完成）
    val keyboardActions = KeyboardActions(
        onNext = { passwordFocusRequester.requestFocus() },
        onDone = {
            focusManager.clearFocus()
            keyboardController?.hide()
            activeField = null
        }
    )
    var expanded by remember { mutableStateOf(false) }
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
                activeField = null
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

//            OutlinedTextField(
//                value = viewModel.username,
//                onValueChange = { viewModel.username = it
//                    expanded = it.isNotEmpty()
//                                },
//                label = { Text("用户名") },
//                singleLine = true,
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Text,
//                    imeAction = ImeAction.Next
//                ),
//                trailingIcon = {
//
//                },
//                keyboardActions = keyboardActions,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .focusRequester(usernameFocusRequester)
//                    .onFocusChanged { focusState ->
//                        if (focusState.isFocused) {
//                            activeField = Field.USERNAME
//                        } else if (activeField == Field.USERNAME) {
//                            activeField = null
//                        }
//                    }
//            )
         var dropdownItems =   viewModel.savedUsersList.map { user ->
                Log.d("Log------in", "user=$user")
             user.username
            }
//            Log.d("--------","asdsadadas:${dropdownItems}")
//            val dropdownItems = listOf("用户1", "用户2", "用户3","用户4","用户5","用户6") // 下拉列表的选项
            UsernameTextField(
                viewModel = viewModel,
                usernameFocusRequester = usernameFocusRequester,
                dropdownItems = dropdownItems
            )


            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(
                viewModel = viewModel,
                passwordFocusRequester = passwordFocusRequester,
            )


            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(text = "登录", onClick = {  // 登录逻辑...
                var isLogin = viewModel.login(viewModel.username, viewModel.password)
                println("isLogin=$isLogin")
                if (isLogin) {
                    SPUtils.getInstance("sp_name").put("loginuser_" + viewModel.username, viewModel.password);
//                    SPUtils.getInstance(Config.SP_NAME).put(Config.MODEL_FLAG, false);
                    onLoginSuccess()
                }
//                // 登录后清除焦点
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
    dropdownItems: List<String> // 下拉列表的选项
) {
    var expanded by remember { mutableStateOf(true) } // 控制下拉列表的展开状态
    var selectedUsername by remember { mutableStateOf("") } // 当前选中的用户名
    Log.d("---------------","1232131${viewModel.savedUsersList}")
    OutlinedTextField(
        value = viewModel.username,
        onValueChange = {
            viewModel.username = it
            expanded = it.isNotEmpty()
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
//                val activeField = null
//                if (focusState.isFocused) {
//                    var activeField = Field.USERNAME
//                } else if (activeField == Field.USERNAME) {
//                    activeField = null
//                }
            }
    )
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd,
        ){
        // 下拉列表
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(TextFieldDefaults.MinWidth).background(Color.White).height(100.dp),
        ) {
            dropdownItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        viewModel.username = item // 更新输入框内容
//                        viewModel.password = item // 更新输入框内容
                        // viewModel.savedUsersList 找到对应的账号吗，提取密码
                        viewModel.savedUsersList.forEach { user ->
                            if (user.username == item) {
                                viewModel.password = user.password
                            }
                        }
                        expanded = false // 关闭下拉菜单
                    },
//                modifier = Modifier.padding(8.dp)
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

data class SavedUser(
    val username: String,
    val password: String
)

// 用于跟踪当前活动的输入字段
enum class Field {
    USERNAME, PASSWORD
}

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLogin by mutableStateOf(false)


    var savedUsersList by mutableStateOf(mutableListOf<SavedUser>())

    init{
        Log.d("LoginViewModel","init")
        savedUsersList = loadSavedUsers().toMutableList()
    }


    fun isValidCredentials(): Boolean {
        // 这里添加实际的验证逻辑
        return username.isNotBlank() && password.length >= 6
    }
    private fun loadSavedUsers(): List<SavedUser> {
        val sp = SPUtils.getInstance("sp_name")
        val allEntries = sp.all // 获取所有存储的键值对
        println("allEntries=$allEntries")
        return allEntries
            .filterKeys { it.startsWith("loginuser_") } // 过滤出登录用户
            .mapNotNull { (key, value) ->
                val username = key.removePrefix("loginuser_")
                if (username.isNotEmpty() && value is String) {
                    SavedUser(username, value)
                } else {
                    null
                }
            }
    }

    // 权限列表

    // 执行登录操作
    fun performLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = AuthService.getPermissions()

                result.fold(
                    onSuccess = { response ->
                        Log.d(
                            "Login",
                            "✅ 请求成功! 状态: ${response.code}, 消息: ${response.message}"
                        )
                        Log.d("Login", "🛡️ 权限列表 (${response.data} 项):")
                        response.data.forEachIndexed { index, permission ->
                            Log.d(
                                "Login",
                                "${index + 1}. ${permission.name} - ${permission.description}"
                            )
                        }
                    },
                    onFailure = { error ->
                        Log.e("Login", "❌ 请求失败", error)
                        when (error) {
                            is ClientRequestException ->
                                Log.e("Login", "客户端错误: ${error.response.status}")

                            is ServerResponseException ->
                                Log.e("Login", "服务器错误: ${error.response.status}")

                            is IOException ->
                                Log.e("Login", "网络错误: ${error.message}")

                            else ->
                                Log.e("Login", "未知错误: ${error.message}")
                        }
                    }
                )


                // 可能抛出异常的代码
            } catch (e: Exception) {
                println("Error logging in: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun login(username: String, password: String): Boolean {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. 执行登录
                val loginResult = AuthService.login(username, password)

                loginResult.fold(
                    onSuccess = { loginResponse ->
                        Log.d(
                            "Login",
                            "✅ 登录成功! 状态: ${loginResponse.code}, 消息: ${loginResponse.message}"
                        )

                        // 保存 token
                        val token = loginResponse.data
//                        saveToken(token)
                        Log.d("Login", "🔑 Token: $token")

                        // 2. 使用 token 获取权限
                        AuthService.getPermissions()

//                        permissionsResult.fold(
//                            onSuccess = { permissionsResponse ->
//                                Log.d("Login", "🛡️ 权限列表 (${permissionsResponse.data.size} 项):")
//                                permissionsResponse.data.forEachIndexed { index, permission ->
//                                    Log.d(
//                                        "Login",
//                                        "${index + 1}. ${permission.name} - ${permission.description}"
//                                    )
//                                }
//
//                                // 3. 保存用户信息
////                                saveUserInfo(loginResponse.data.userInfo)
//                            },
//                            onFailure = { error ->
//                                Log.e("Login", "❌ 获取权限失败", error)
////                                handleError(error)
//                            }
//                        )
                        isLogin = true
                    },
                    onFailure = { error ->
                        Log.e("Login", "❌ 登录失败", error)
//                        handleError(error)
                        isLogin = false
                    }
                )
            }

            // 可能抛出异常的代码
            catch (e: Exception) {
                println("Error logging in: ${e.message}")
                e.printStackTrace()
                isLogin = false

            }
        }
        return isLogin
    }

}

package com.hover.app.utils

import LoginData
import LoginRequest
import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import java.io.IOException
import kotlinx.serialization.Serializable
// 基础响应模型
@Serializable
data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)

// 权限数据模型
@Serializable
data class Permission(
    val description: String,
    val name: String
)

// 安全网络客户端
object SafeNetworkClient {
    // 使用协程作用域管理客户端生命周期
    private var client: HttpClient? = null

    // 获取或创建 HttpClient 实例
    private fun getClient(): HttpClient {
        return client ?: createHttpClient().also {
            client = it
        }
    }

    // 创建 HttpClient
    private fun createHttpClient(): HttpClient {
        return HttpClient(Android) {
            // 使用简单日志而不是 Logback
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("Ktor", message)
                    }
                }
                level = LogLevel.HEADERS
            }

            engine {
                // this: AndroidEngineConfig
                connectTimeout = 100_000
                socketTimeout = 100_000
//                proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("localhost", 8080))
            }

            // 内容协商配置
            install(ContentNegotiation) {
                jackson()
            }

            // 默认请求配置
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                header("Accept-Charset", "UTF-8")
            }

            // 异常处理
            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status.value
//                    when (statusCode) {
//                        in 300..399 -> throw RedirectResponseException(response)
//                        in 400..499 -> throw ClientRequestException(response)
//                        in 500..599 -> throw ServerResponseException(response)
//                    }
                }
            }
        }
    }

    // 安全执行网络请求
    suspend fun <T> safeRequest(
        request: suspend HttpClient.() -> T
    ): Result<T> {
        return try {
            Log.d("Network", "✅ 请求成功!")
            Result.success(getClient().request())
        } catch (e: Exception) {
            Log.e("Network", "请求失败: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 关闭客户端
    fun close() {
        client?.close()
        client = null
    }
}

// 认证服务
object AuthService {
    var BaseUrl = "http://api.chaoyang1024.top:18080/"
    // 使用模拟器专用地址
    private  val PERMISSIONS_URL = BaseUrl + "api/auth/permissions"
    private  val LOGIN_URL = BaseUrl + "api/auth/login"
    suspend fun getPermissions(): Result<BaseResponse<List<Permission>>> {
        println("正在请求权限数据...")
        return SafeNetworkClient.safeRequest {
            get(PERMISSIONS_URL) {
                timeout {
                    requestTimeoutMillis = 15000
                }
            }.body()
        }
    }
    // 登录
    suspend fun login(username: String, password: String): Result<BaseResponse<LoginData>> {
        return SafeNetworkClient.safeRequest {
            post(LOGIN_URL) {
                timeout {
                    requestTimeoutMillis = 15000
                }
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(LoginRequest(username, password))
            }.body()
        }
    }

}


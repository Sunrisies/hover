import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("user_name") val username: String,
    @SerialName("pass_word") val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
    @SerialName("data") val data: LoginData
)

@Serializable
data class LoginData(
    @SerialName("token") val token: String,
    @SerialName("expire") val expire: Long,
    @SerialName("user_info") val userInfo: UserInfo
)

@Serializable
data class UserInfo(
    @SerialName("user_uuid") val userUuid: String,
    @SerialName("user_name") val username: String,
    @SerialName("permissions") val permissions: String
)

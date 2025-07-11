import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("user_name") val userName: String,
    @SerialName("pass_word") val passWord: String
)


//@Serializable
//data class LoginData(
//    val access_token: String,
//    val expires_in: Number,
//    val user: UserInfo
//)

@Serializable
data class UserInfo(
    @SerialName("id") val id: Int,
    @SerialName("uuid") val uuid: String,
    @SerialName("user_name") val user_name: String,
    @SerialName("email") val email: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("role") val role: String? = null,
    @SerialName("permissions") val permissions: String,
    @SerialName("binding") val binding: String? = null,
    @SerialName("created_at") val created_at: String,
    @SerialName("updated_at") val updated_at: String
)

// 定义数据模型类
@Serializable
data class LoginResponse(
    val code: Int,
    val msg: String,
    val data: LoginData?,
    val count: Any?,
    val obj: Any?
)
@Serializable
data class LoginData(
    val userId: String,
    val userName: String,
    val permission: List<Any>,
    val modules: List<Module>,
    val token: String,
    val workspaceId: String,
    val userType: Int,
    val mqttUsername: String,
    val mqttPassword: String,
    val nickName: String,
    val logo: String
)
@Serializable
data class Module(
    val id: String,
    val modulename: String,
    val moduledesc: String,
    val moduleico: String,
    val routeurl: String,
    val removecode: String,
    val page: Any?,
    val limit: Any?,
    val keywords: Any?,
    val createTime: Any?,
    val permissionids: Any?
)



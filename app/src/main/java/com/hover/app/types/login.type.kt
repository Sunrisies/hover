import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("user_name") val user_name: String,
    @SerialName("pass_word") val pass_word: String
)


@Serializable
data class LoginData(
    val access_token: String,
    val expires_in: Number,
    val user: UserInfo
)

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

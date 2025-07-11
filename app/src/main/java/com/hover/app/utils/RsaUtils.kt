package com.hover.app.utils

import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


object RsaUtils {
    //进行Base64转码时的flag设置，默认为Base64.DEFAULT
    private const val TAG = "RsaUtils"

    fun encrypt(plainText: String, publicKeyStr: String): String {
        Log.d(TAG, "开始 RSA 加密")
        Log.d(TAG, "原始文本: $plainText")
        Log.d(TAG, "公钥字符串: ${publicKeyStr.take(30)}...") // 只显示前30个字符

        try {
            val publicKey = getPublicKeyFromString(publicKeyStr)
            Log.d(TAG, "公钥转换成功: ${publicKey.algorithm}")

            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            Log.d(TAG, "加密成功，字节长度: ${encryptedBytes.size}")

            val result = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
            Log.d(TAG, "Base64 结果: ${result.take(30)}...") // 只显示前30个字符

            return result
        } catch (e: Exception) {
            Log.e(TAG, "RSA 加密失败", e)
            throw RuntimeException("RSA 加密失败: ${e.message}", e)
        }
    }

    private fun getPublicKeyFromString(publicKeyStr: String): PublicKey {
        try {
            val sanitizedKey = publicKeyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "")
                .trim()

            Log.d(TAG, "清理后的公钥: ${sanitizedKey.take(30)}...")

            val keyBytes = Base64.decode(sanitizedKey, Base64.DEFAULT)
            Log.d(TAG, "解码后的公钥字节长度: ${keyBytes.size}")

            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")

            return keyFactory.generatePublic(keySpec)
        } catch (e: Exception) {
            Log.e(TAG, "公钥转换失败", e)
            throw RuntimeException("公钥转换失败: ${e.message}", e)
        }
    }

}
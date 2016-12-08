package konrad.test.kotlin

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import org.apache.commons.codec.binary.Base64
import java.io.File
import java.io.InputStream
import java.util.*
import javax.crypto.SecretKey

/**
 * Stworzone przez Konrad Botor dnia 2016-12-08.
 */
fun main(args: Array<String>) {
    val file: File?
    var propertiesStream: InputStream? = null
    val properties = Properties()

    if (args.isNotEmpty()) {
        file = File(args[0])
        propertiesStream = file.inputStream()
    }

    if (propertiesStream == null) {
        propertiesStream = Class.forName("konrad.test.kotlin.ApplicationKt").classLoader.getResourceAsStream("application.properties")
    }

    properties.load(propertiesStream)

    val algorithm = properties.getProperty("algorithm", "")
    val mode = properties.getProperty("mode", "")
    val padding = properties.getProperty("padding", "")
    val encryptionKey = properties.getProperty("encryptionKey", "")
    val unencryptedText = properties.getProperty("unencryptedText", "")

    val secretKeyFactory = SecretKeyFactory.getInstance(algorithm)
    val keySpec = DESKeySpec(encryptionKey.toByteArray())
    val cipher = Cipher.getInstance("$algorithm/$mode/$padding")
    val secretKey = secretKeyFactory.generateSecret(keySpec)

    println("Text to encrypt: $unencryptedText")

    val encryptedString = encrypt(cipher, secretKey, unencryptedText)

    println("Encrypted string: $encryptedString")

    val decryptedText: String = decrypt(cipher, encryptedString, secretKey)

    println("Decrypted string: $decryptedText")
}

private fun decrypt(cipher: Cipher, encryptedString: String, secretKey: SecretKey?): String {
    var decryptedText: String = ""
    try {
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val encryptedText = Base64.decodeBase64(encryptedString)
        val plainText = cipher.doFinal(encryptedText)
        decryptedText = String(plainText)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return decryptedText
}

private fun encrypt(cipher: Cipher, secretKey: SecretKey?, unencryptedText: String): String {
    var encryptedString = ""
    try {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val plainText = unencryptedText.toByteArray()
        val encryptedText = cipher.doFinal(plainText)
        encryptedString = String(Base64.encodeBase64(encryptedText))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return encryptedString
}
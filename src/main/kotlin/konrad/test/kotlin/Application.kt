package konrad.test.kotlin

import org.apache.commons.cli.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import org.apache.commons.codec.binary.Base64
import java.io.File
import java.io.InputStream
import java.util.*
import javax.crypto.SecretKey
import org.apache.commons.cli.HelpFormatter




/**
 * Stworzone przez Konrad Botor dnia 2016-12-08.
 */
fun main(args: Array<String>) {
    val properties = processOptions(args)

    if (properties.keys.contains("print_help")) return

    val algorithm = properties.getProperty("algorithm", "")
    val mode = properties.getProperty("mode", "")
    val padding = properties.getProperty("padding", "")
    val encryptionKey = properties.getProperty("encryptionKey", "")
    var text = properties.getProperty("text", "")

    val secretKeyFactory = SecretKeyFactory.getInstance(algorithm)
    val keySpec = DESKeySpec(encryptionKey.toByteArray())
    val cipher = Cipher.getInstance("$algorithm/$mode/$padding")
    val secretKey = secretKeyFactory.generateSecret(keySpec)

    if (properties.keys.contains("input_file")) {
        val inputFile = File(properties.getProperty("input_file"))
        text = inputFile.readText()
    }

    if (properties.keys.contains("encrypt")) {
        println("Text to encrypt: $text")
        val encryptedString = encrypt(cipher, secretKey, text)
        println("Encrypted string: $encryptedString")
        if (properties.keys.contains("output_file")) {
            writeToFile(properties.getProperty("output_file"), text)
        }
    }

    if (properties.keys.contains("decrypt")) {
        println("Text to decrypt: $text")
        val decryptedText = decrypt(cipher, text, secretKey)
        println("Decrypted string: $decryptedText")
        if (properties.keys.contains("output_file")) {
            writeToFile(properties.getProperty("output_file"), text)
        }
    }
}

fun  writeToFile(fileName: String, text: String) {
    val out = File(fileName)
    out.writeText(text)
}

fun processOptions(args: Array<String>): Properties {
    val file: File?
    val properties = Properties()
    val parser = DefaultParser()
    val commandLine: CommandLine?
    val propertiesStream: InputStream = Class.forName("konrad.test.kotlin.ApplicationKt").classLoader.getResourceAsStream("application.properties")

    properties.load(propertiesStream)

    val configurationFileOption = Option.builder("c").argName("path-to-configuration-file").longOpt("configuration_file").hasArg().required(false).optionalArg(false).desc("Configuration file path").build()
    val textFileOption = Option.builder("f").argName("path-to-input-file").longOpt("input-file").hasArg().required(false).optionalArg(false).desc("Input file path").build()
    val outFileOption = Option.builder("o").argName("path-to-output-file").longOpt("output-file").hasArg().required(false).optionalArg(true).desc("Output file path").build()
    val helpOption = Option.builder("h").longOpt("help").hasArg(false).required(false).desc("Print this help").build()
    val decryptOption = Option.builder("d").longOpt("decrypt").hasArg(false).required(false).desc("Decrypt input").build()
    val encryptOption = Option.builder("e").longOpt("encrypt").hasArg(false).required(false).desc("Encrypt input").build()

    val options = Options()
    options.addOption(configurationFileOption)
    options.addOption(textFileOption)
    options.addOption(outFileOption)
    options.addOption(helpOption)
    options.addOption(decryptOption)
    options.addOption(encryptOption)


    try {
        commandLine = parser.parse(options, args)
    } catch (exp: ParseException) {
        System.err.println("Parsing failed.  Reason: " + exp.message)
        exp.printStackTrace()
        return properties
    }

    if (commandLine.hasOption("h")) {
        properties.setProperty("print_help", "true")
        val formatter = HelpFormatter()
        formatter.printHelp("TextCrypt", options)
        return properties
    }

    if (commandLine.hasOption("c")) {
        val path = commandLine.getOptionValue("c")
        file = File(path)
        properties.load(file.inputStream())
    }

    if (commandLine.hasOption("f")) {
        properties.setProperty("input_file", commandLine.getOptionValue("f"))
    }

    if (commandLine.hasOption("o")) {
        properties.setProperty("output_file", commandLine.getOptionValue("f"))
    }

    if (commandLine.hasOption("e")) {
        properties.setProperty("encrypt", "true")
    } else if (commandLine.hasOption("d")) {
        properties.setProperty("decrypt", "true")
    } else {
        properties.setProperty("encrypt", "true")
    }

    return properties
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
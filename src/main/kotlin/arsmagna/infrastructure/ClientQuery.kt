package arsmagna.infrastructure

import arsmagna.IrbisConnection
import java.io.ByteArrayOutputStream
import java.io.IOException


/**
 * Клиентский запрос.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ClientQuery (connection: IrbisConnection, commandCode: String) {
    private val stream: ByteArrayOutputStream = ByteArrayOutputStream()

    init {
        addAnsi(commandCode)
        addAnsi(Character.valueOf(connection.workstation).toString())
        addAnsi(commandCode)
        add(connection.clientId)
        connection.queryId++
        add(connection.queryId)
        addAnsi(connection.password)
        addAnsi(connection.username)
        addLineFeed()
        addLineFeed()
        addLineFeed()
    }

    //=========================================================================
    @Throws(IOException::class)
    fun add(value: Boolean) {
        val text = if (value) "1" else "0"
        addAnsi(text)
    }

    @Throws(IOException::class)
    fun add(value: Int) {
        val text = value.toString()
        addAnsi(text)
    }

    @Throws(IOException::class)
    fun addAnsi(text: String?) {
        addAnsiNoLF(text)
        addLineFeed()
    }

    @Suppress("UNUSED_PARAMETER")
    @Throws(IOException::class)
    fun addAnsiNoLF(text: String?) {
        TODO()
//        if (!isNullOrEmpty(text)) {
//            val bytes: ByteArray = text.getBytes(IrbisEncoding.ansi())
//            stream!!.write(bytes)
//        }
    }

    @Throws(IOException::class)
    fun addLineFeed() {
        TODO()
        // stream.write(Utility.LF)
    }

    @Suppress("UNUSED_PARAMETER")
    @Throws(IOException::class)
    fun addUtf(text: String?) {
        TODO()
//        if (!isNullOrEmpty(text)) {
//            val bytes: ByteArray = text.getBytes(IrbisEncoding.utf())
//            stream!!.write(bytes)
//        }
//        addLineFeed()
    }

    fun encode(): Array<ByteArray> {
        TODO()
//        val buffer = stream!!.toByteArray()
//        val prefix: ByteArray = IrbisEncoding.ansi().encode(buffer.size.toString() + "\n").array()
//        return arrayOf(prefix, buffer)
    }

}
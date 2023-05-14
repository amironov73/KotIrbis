package arsmagna.infrastructure

import arsmagna.Connection
import arsmagna.utils.LF
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Клиентский запрос.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ClientQuery (connection: Connection, commandCode: String) {
    @Suppress("JoinDeclarationAndAssignment")
    private val stream: ByteArrayOutputStream

    init {
        stream = ByteArrayOutputStream()
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

    /**
     * Добавление логического значения.
     */
    @Throws(IOException::class)
    fun add(value: Boolean) {
        val text = if (value) "1" else "0"
        addAnsi(text)
    }

    /**
     * Добавление целого числа.
     */
    @Throws(IOException::class)
    fun add(value: Int) {
        val text = value.toString()
        addAnsi(text)
    }

    /**
     * Добавление строки в кодировке ANSI с переходом на новую строку.
     */
    @Throws(IOException::class)
    fun addAnsi(text: String?) {
        addAnsiNoLF(text)
        addLineFeed()
    }

    /**
     * Добавление строки в кодировке ANSI без перехода на новую строку.
     */
    @Throws(IOException::class)
    fun addAnsiNoLF(text: String?) {
        if (text != null) {
            val bytes = text.toByteArray(getAnsiEncoding())
            stream.write(bytes)
        }
    }

    /**
     * Переход на новую строку.
     */
    @Throws(IOException::class)
    fun addLineFeed() {
        stream.write(LF)
    }

    /**
     * Добавление строки в кодировке UTF-8 с переходом на новую строку.
     */
    @Throws(IOException::class)
    fun addUtf(text: String?) {
        if (text != null) {
            val bytes = text.toByteArray(getUtfEncoding())
            stream.write(bytes)
        }
        addLineFeed()
    }

    /**
     * Сериализация запроса в последовательность байт.
     */
    fun encode(): Array<ByteArray> {
        val buffer = stream.toByteArray()
        val prefix: ByteArray = getAnsiEncoding().encode(buffer.size.toString() + "\n").array()
        return arrayOf(prefix, buffer)
    }
}
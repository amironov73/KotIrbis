package arsmagna.infrastructure

import arsmagna.IrbisException
import arsmagna.utils.isNullOrEmpty
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.util.*


/**
 * Ответ сервера.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ServerResponse (var socket: Socket): AutoCloseable {

    /**
     * Код команды.
     */
    var command: String? = null

    /**
     * Идентификатор клиента.
     */
    var clientId = 0

    /**
     * Номер запроса.
     */
    var queryId = 0

    /**
     * Код возврата (формируется не всегда!).
     */
    var returnCode = 0

    private var stream: InputStream? = null

    private var savedSymbol = 0

    init {
        stream = BufferedInputStream(socket.getInputStream())
        savedSymbol = -1

        command = readAnsi()
        clientId = readInt32()
        queryId = readInt32()
        for (i in 0..6) {
            readAnsi()
        }
    }

    @Throws(IOException::class)
    override fun close() {
        socket.close()
    }

    /**
     * Проверка кода возврата.
     *
     * @param allowed Разрешенные коды.
     * @throws IrbisException Bad return code.
     */
    @Suppress("UNUSED_PARAMETER")
    @Throws(IrbisException::class)
    fun checkReturnCode(allowed: IntArray?) {
        TODO()
//        if (obtainReturnCode() < 0) {
//            if (Utility.find(allowed, returnCode) < 0) {
//                throw IrbisException(returnCode)
//            }
//        }
    }

    @Throws(IrbisException::class)
    fun checkReturnCode() {
        if (obtainReturnCode() < 0) {
            throw IrbisException(returnCode)
        }
    }

    /**
     * Поиск преамбулы в потоке.
     *
     * @return Поток (сразу за преамбулой) либо null.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun getBinaryFile(): InputStream? {
        val length: Int = binaryFilePreamble.size
        val length1 = length - 1
        val buffer = ByteArray(length)
        if (stream!!.read(buffer) != length) {
            return null
        }
        while (true) {
            if (buffer.contentEquals(binaryFilePreamble)) {
                return stream
            }
            System.arraycopy(buffer, 1, buffer, 0, length1)
            val one = stream!!.read()
            if (one < 0) {
                return null
            }
            buffer[length1] = one.toByte()
        }
    }

    /**
     * Отдача входного потока "на растерзание".
     * Закрыть поток должен будет вызывающий код.
     *
     * @return Входной поток ответа сервера.
     */
    fun getStream(): InputStream {
        return stream!!
    }

    fun getLine(): ByteArray {
        val result = ByteArrayOutputStream()
        try {
            while (true) {
                var one: Int
                if (savedSymbol >= 0) {
                    one = savedSymbol
                    savedSymbol = -1
                } else {
                    one = stream!!.read()
                }
                if (one < 0) {
                    break
                }
                if (one == 0x0D) {
                    one = stream!!.read()
                    savedSymbol = if (one == 0x0A) {
                        break
                    } else {
                        one
                    }
                } else {
                    result.write(one)
                }
            }
        } catch (ex: IOException) {
            return ByteArray(0)
        }
        return result.toByteArray()
    }

    fun obtainReturnCode(): Int {
        returnCode = readInt32()
        return returnCode
    }

    fun nop() {}

    fun readAnsi(): String {
        TODO()
//        val line = getLine()
//        return String(line, IrbisEncoding.ansi())
    }

    /**
     * Считывание не менее указанного количества строк.
     *
     * @param count How many strings to read.
     * @return Read string or null.
     */
    fun readAnsi(count: Int): Array<String>? {
        val result = ArrayList<String>()
        for (i in 0 until count) {
            val line = readAnsi()
            if (isNullOrEmpty(line)) {
                return null
            }
            result.add(line)
        }
        return result.toTypedArray<String>()
    }

    fun readInt32(): Int {
        val text = readAnsi()
        return text.toInt()
    }

    fun readRemainingAnsiLines(): Array<String> {
        val result = ArrayList<String>()
        while (true) {
            val line = readAnsi()
            if (isNullOrEmpty(line)) {
                break
            }
            result.add(line)
        }
        return result.toTypedArray<String>()
    }

    fun readRemainingUtfLines(): Array<String> {
        val result = ArrayList<String>()
        while (true) {
            val line = readUtf()
            if (isNullOrEmpty(line)) {
                break
            }
            result.add(line)
        }
        return result.toTypedArray<String>()
    }

    @Throws(IOException::class)
    fun readRemainingAnsiText(): String {
        TODO()
        //val buffer = ByteArrayOutputStream()
        //Utility.copyTo(stream, buffer)
        //val bytes = buffer.toByteArray()
        //return String(bytes, IrbisEncoding.ansi())
    }

    @Throws(IOException::class)
    fun readRemainingUtfText(): String {
        TODO()
//        val buffer = ByteArrayOutputStream()
//        Utility.copyTo(stream, buffer)
//        val bytes = buffer.toByteArray()
//        return String(bytes, IrbisEncoding.utf())
    }

    fun readUtf(): String {
        TODO()
//        val line = getLine()
//        return String(line, IrbisEncoding.utf())
    }

    /**
     * Считывание не менее указанного количества строк.
     *
     * @param count How many lines to read.
     * @return Read lines or null.
     */
    fun readUtf(count: Int): Array<String>? {
        val result = ArrayList<String>()
        for (i in 0 until count) {
            val line = readUtf()
            if (isNullOrEmpty(line)) {
                return null
            }
            result.add(line)
        }
        return result.toTypedArray<String>()
    }

}
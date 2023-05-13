package arsmagna.infrastructure

import arsmagna.utils.MSDOS_DELIMITER
import java.io.IOException
import java.io.PrintWriter
import java.io.StringReader
import java.util.*


/**
 * Файл меню.
 */
@Suppress("unused")
class MenuFile {
    var fileName: String? = null
    val entries = mutableListOf<MenuEntry>()

    /**
     * Добавление строчек в меню.
     *
     * @return Собственно меню.
     */
    fun add(code: String, comment: String?): MenuFile {
        val entry = MenuEntry(code, comment!!)
        entries.add(entry)
        return this
    }

    /**
     * Отыскивает запись, соответствующую данному коду.
     *
     * @return Запись либо null.
     */
    fun getEntry(code: String): MenuEntry? {
        var c = code
        for (entry in entries) {
            if (c.equals(entry.code, ignoreCase = true)) {
                return entry
            }
        }
        c = c.trim { it <= ' ' }
        for (entry in entries) {
            if (c.equals(entry.code, ignoreCase = true)) {
                return entry
            }
        }
        c = trimCode(code)
        for (entry in entries) {
            if (code.equals(entry.code, ignoreCase = true)) {
                return entry
            }
        }
        return null
    }

    /**
     * Выдает значение, соответствующее коду.
     *
     * @param code         Код.
     * @param defaultValue Значение по умолчанию.
     * @return Найденное значение либо null.
     */
    fun getValue(code: String, defaultValue: String? = null): String? {
        val found = getEntry(code)
        return found?.comment ?: defaultValue
    }

    /**
     * Разбор потока.
     *
     * @param scanner Поток.
     * @return Меню.
     */
    fun parse(scanner: Scanner): MenuFile {
        val result = MenuFile()
        while (scanner.hasNext()) {
            val code = scanner.nextLine()
            if (code == null || !scanner.hasNext()) {
                break
            }
            if (code.startsWith(STOP_MARKER)) {
                break
            }
            val comment: String = scanner.nextLine()
            result.add(code, comment)
        }
        return result
    }

    /**
     * Парсинг ответа сервера.
     *
     * @return Меню.
     */
    @Throws(IOException::class)
    fun parse(response: ServerResponse): MenuFile {
        val text = response.readRemainingAnsiText()
        val reader = StringReader(text)
        val scanner = Scanner(reader)
        return parse(scanner)
    }

    /**
     * Вывод меню в текстовый поток.
     *
     * @param writer Текстовый поток.
     */
    fun write(writer: PrintWriter) {
        for (entry in entries) {
            writer.println(entry.code)
            writer.println(entry.comment)
        }
        writer.print(STOP_MARKER)
    }

    override fun toString(): String {
        val result = StringBuilder()
        for (entry in entries) {
            result.append(entry.code)
            result.append(MSDOS_DELIMITER)
            result.append(entry.comment)
            result.append(MSDOS_DELIMITER)
        }
        result.append(STOP_MARKER)
        return result.toString()
    }
}
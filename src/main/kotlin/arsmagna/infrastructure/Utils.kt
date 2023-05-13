@file:Suppress("unused")

package arsmagna.infrastructure


/**
 * Преамбула двоичного файла.
 */
var binaryFilePreamble = byteArrayOf(
    0x49, 0x52, 0x42, 0x49, 0x53, 0x5F, 0x42, 0x49, 0x4E, 0x41, 0x52,
    0x59, 0x5F, 0x44, 0x41, 0x54, 0x41
)

/**
 * Сравнение двух ключей меню или INI-файла.
 */
fun sameKey(first: String, second: String) = first.equals(second, true)

/**
 * Признак конца меню.
 */
const val STOP_MARKER = "*****"

/**
 * Разделители меню.
 */
const val MENU_SEPARATORS = "[\\s\\-=:]"

/**
 * Отрезание лишних символов в коде меню.
 *
 * @return Очищенный код.
 */
fun trimCode(code: String): String {
    var c = code.trim { it <= ' ' }
    val parts = code.split(MENU_SEPARATORS.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if (parts.size != 0) {
        c = parts[0]
    }
    return c
}

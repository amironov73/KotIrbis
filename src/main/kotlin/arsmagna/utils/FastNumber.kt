@file:Suppress("unused", "DuplicatedCode")

package arsmagna.utils

import org.jetbrains.annotations.Contract

/**
 * Быстрая и тупая конвертация числа в строку.
 * Не учитывает знак.
 *
 * @param number Число, подлежащее конверсии.
 * @return Результирующая строка.
 */
@Contract(pure = true)
fun fastToString(number: Int): String {
    var value = number
    val buffer = CharArray(10)
    var offset = 9
    if (value == 0) {
        buffer[offset] = '0'
        offset--
    } else {
        while (value != 0) {
            val remainder = value % 10
            value = number / 10
            buffer[offset] = Char('0'.code + remainder)
            offset--
        }
    }

    return String(buffer, offset + 1, 9 - offset)
}

/**
 * Быстрая и тупая конвертация числа в строку.
 * Не учитывает знак.
 *
 * @param number Число, подлежащее конверсии.
 * @return Результирующая строка.
 */
@Contract(pure = true)
fun fastToString(number: UInt): String {
    var value = number
    val buffer = CharArray(10)
    var offset = 9
    if (value == 0u) {
        buffer[offset] = '0'
        offset--
    } else {
        while (value != 0u) {
            val remainder = value % 10u
            value = number / 10u
            buffer[offset] = Char(('0'.code.toUInt() + remainder).toInt())
            offset--
        }
    }

    return String(buffer, offset + 1, 9 - offset)
}

/**
 * Быстрая и тупая конвертация числа в строку.
 * Не учитывает знак.
 *
 * @param number Число, подлежащее конверсии.
 * @return Результирующая строка.
 */
@Contract(pure = true)
fun fastToString(number: Long): String {
    var value = number
    val buffer = CharArray(20)
    var offset = 19
    if (value == 0L) {
        buffer[offset] = '0'
        offset--
    } else {
        while (value != 0L) {
            val remainder = value % 10
            value = number / 10
            buffer[offset] = Char(('0'.code.toLong() + remainder).toUShort())
            offset--
        }
    }

    return String(buffer, offset + 1, 19 - offset)
}

/**
 * Быстрая и тупая конвертация числа в строку.
 * Не учитывает знак.
 *
 * @param number Число, подлежащее конверсии.
 * @return Результирующая строка.
 */
@Contract(pure = true)
fun fastToString(number: ULong): String {
    var value = number
    val buffer = CharArray(20)
    var offset = 19
    if (value == 0UL) {
        buffer[offset] = '0'
        offset--
    } else {
        while (value != 0UL) {
            val remainder = value % 10u
            value = number / 10u
            buffer[offset] = Char(('0'.code.toULong() + remainder).toUShort())
            offset--
        }
    }

    return String(buffer, offset + 1, 19 - offset)
}

/**
 * Быстрый и грязный разбор обычного целого без знака.
 *
 * @param text Текст для разбора.
 * @return Разобранное число.
 */
@Contract(pure = true)
fun fastParseInt(text: String): Int {
    var result = 0
    var offset = 0
    var length = text.length
    while (length > 0) {
        result = result * 10 + text[offset].code - '0'.code
        length--
        offset++
    }

    return result
}

/**
 * Быстрый и грязный разбор обычного целого без знака.
 *
 * @param text Текст для разбора.
 * @return Разобранное число.
 */
@Contract(pure = true)
fun fastParseInt(text: String, offset: Int, length: Int): Int {
    var ofs = offset
    var len = length
    var result = 0
    while (len > 0) {
        result = result * 10 + text[ofs].code - '0'.code
        len--
        ofs++
    }

    return result
}

/**
 * Быстрый и грязный разбор обычного целого без знака.
 *
 * @param text Текст для разбора.
 * @return Разобранное число.
 */
@Contract(pure = true)
fun fastParseInt(text: CharArray, offset: Int, length: Int): Int {
    var ofs = offset
    var len = length
    var result = 0
    while (len > 0) {
        result = result * 10 + text[ofs].code - '0'.code
        len--
        ofs++
    }

    return result
}

/**
 * Быстрый и грязный разбор обычного целого без знака.
 *
 * @param text Текст для разбора.
 * @return Разобранное число.
 */
@Contract(pure = true)
fun fastParseInt(text: ByteArray, offset: Int, length: Int): Int {
    var ofs = offset
    var len = length
    var result = 0
    while (len > 0) {
        result = result * 10 + text[ofs] - '0'.code
        len--
        ofs++
    }

    return result
}

/**
 * Быстрый и грязный разбор длинного целого без знака.
 *
 * @param text Текст для разбора.
 * @return Разобранное число.
 */
@Contract(pure = true)
fun fastParseLong(text: String): Long {
    var result: Long = 0
    var offset = 0
    var length = text.length
    while (length > 0) {
        result = result * 10 + text[offset].code.toLong() - '0'.code.toLong()
        length--
        offset++
    }

    return result
}

/**
 * Быстрый и грязный разбор длинного целого без знака.
 *
 * @param text Текст для разбора.
 * @return Разобранное число.
 */
@Contract(pure = true)
fun fastParseLong(text: String, offset: Int, length: Int): Long {
    var ofs = offset
    var len = length
    var result: Long = 0
    while (len > 0) {
        result = result * 10 + text[ofs].code.toLong() - '0'.code.toLong()
        len--
        ofs++
    }

    return result
}

/**
 * Быстрый и грязный разбор длинного целого без знака.
 *
 * @param text Текст для разбора.
 * @return Разобранное число.
 */
@Contract(pure = true)
fun fastParseLong(text: CharArray, offset: Int, length: Int): Long {
    var ofs = offset
    var len = length
    var result: Long = 0
    while (len > 0) {
        result = result * 10 + text[ofs].code.toLong() - '0'.code.toLong()
        len--
        ofs++
    }

    return result
}

/**
 * Быстрый и грязный разбор длинного целого без знака.
 *
 * @param text Текст для разбора.
 * @return Разобранное число.
 */
@Contract(pure = true)
fun fastParseLong(text: ByteArray, offset: Int, length: Int): Long {
    var ofs = offset
    var len = length
    var result: Long = 0
    while (len > 0) {
        result = result * 10 + text[ofs] - '0'.code.toLong()
        len--
        ofs++
    }

    return result
}

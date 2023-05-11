@file:Suppress("unused")

package arsmagna.utils

/**
 * Разделитель строк в ИРБИС.
 */
const val IRBIS_DELIMITER = "\u001F\u001E"

/**
 * Короткая версия разделителя строк.
 */
const val SHORT_DELIMITER = "\u001E"

/**
 * Разделитель строк в MSDOS
 */
const val MSDOS_DELIMITER = "\r\n"

/**
 * Разделитель строк в UNIX.
 */
const val UNIX_DELIMITER = "\n"

val SEARCH_DELIMITERS = charArrayOf('#')

/**
 * Замена разделителей текста с ИРБИС на MS-DOS.
 *
 * @param text Текст с разделителями ИРБИС.
 * @return Преобразованный текст.
 */
fun fromIrbisToDos(text: String?): String? {
    if (text != null) {
        return text.replace(IRBIS_DELIMITER.toRegex(), MSDOS_DELIMITER)
    }
    return null
}

/**
 * Замена разделителей текста с MS-DOS на ИРБИС.
 *
 * @param text Текст с разделителями MS-DOS.
 * @return Преобразованный текст.
 */
fun fromDosToIrbis(text: String?): String? {
    if (text != null) {
        return text.replace(MSDOS_DELIMITER.toRegex(), IRBIS_DELIMITER)
    }
    return null
}

/**
 * Замена разделителей текста с MS-DOS на UNIX.
 *
 * @param text Текст с разделителями MS-DOS.
 * @return Преобразованный текст.
 */
fun fromDosToUnix(text: String?): String? {
    if (text != null) {
        return text.replace(MSDOS_DELIMITER.toRegex(), UNIX_DELIMITER)
    }
    return null
}

/**
 * Разбивка ответа сервера по строкам (полный вариант разделителя).
 *
 * @param text Текст ответа сервера.
 * @return Массив строк.
 */
fun fromFullDelimiter(text: String?): Array<String?> {
    return text?.split(IRBIS_DELIMITER.toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        ?: arrayOfNulls(0)
}

/**
 * Строки, приходящие в ответ на команду WriteRecord.
 *
 * @param text Текст ответа сервера.
 * @return Строки, в которых содержится модифицированная запись.
 */
fun fromShortDelimiter(text: String?): Array<String?> {
    return text?.split(SHORT_DELIMITER.toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        ?: arrayOfNulls(0)
}

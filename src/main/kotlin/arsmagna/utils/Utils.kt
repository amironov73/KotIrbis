@file:Suppress("unused")

package arsmagna.utils

/*
 * Вспомогательные константы и методы, не вошедшие в прочие классы.
 */

import org.jetbrains.annotations.Contract

/**
 * Максимальное количество записей в пакете.
 */
const val MAX_PACKET = 32758

/**
 * Список баз данных для администратора.
 */
const val ADMINISTRATOR_DATABASE_LIST = "dbnam1.mnu"

/**
 * Список баз данных для каталогизатора.
 */
const val CATALOGER_DATABASE_LIST = "dbnam2.mnu"

/**
 * Список баз данных для читателя.
 */
const val READER_DATABASE_LIST = "dbnam3.mnu"

/**
 * Carriage return and line feed symbols.
 */
val CRLF = byteArrayOf(0x0D, 0x0A)

/**
 * Line feed symbol.
 */
val LF = byteArrayOf(0x0A)

/**
 * Return codes that are valid for the ReadRecord command.
 */
val READ_RECORD_CODES = intArrayOf(-201, -600, -602, -603)

/**
 * Return codes that are valid for the ReadTerms command.
 */
val READ_TERMS_CODES = intArrayOf(-202, -203, -204)

// ОБЩИЕ РАЗДЕЛИТЕЛИ

/**
 * Запятая.
 */
val comma = charArrayOf(',')

/**
 * Запятая или точка с запятой.
 */
val commaOrSemicolon = charArrayOf(',', ';')

/**
 * Двоеточие.
 */
val colon = charArrayOf(':')

/**
 * Точка.
 */
val dot = charArrayOf('.')

/**
 * Знак "минус".
 */
val minus = charArrayOf('-')

/**
 * Перевод строки.
 */
val newLine = charArrayOf('\r', '\n')

/**
 * Перевод строки или символ процента.
 */
val newLineOrPercent = charArrayOf('\r', '\n', '%')

/**
 * Решетка.
 */
val numberSign = charArrayOf('#')

/**
 * Точка с запятой.
 */
val semicolon = charArrayOf(';')

/**
 * Слэш.
 */
val slash = charArrayOf('/')

/**
 * Пробел.
 */
val space = charArrayOf(' ')

/**
 * Пробел или символ табуляции.
 */
val spaceOrTab = charArrayOf(' ', '\t')

/**
 * Символ табуляции.
 */
val tab = charArrayOf('\t')

/**
 * Вертикальная черта.
 */
val verticalLine = charArrayOf('|')

// КОДЫ АРМ

/**
 * АРМ "Администратор".
 */
const val Administrator: Byte =  65 // 'A'

/**
 * АРМ "Книговыдача".
 */
const val Bookland: Byte = 66 // 'B'

/**
 * АРМ "Книговыдача". Еще разок :)
 */
const val Circulation: Byte = 66 // 'B'

/**
 * АРМ "Каталогизатор".
 */
const val Cataloger: Byte = 67 // 'C'

/**
 * Апплет Java.
 */
const val JavaAppllet: Byte = 74 // 'J'

/**
 * АРМ "Книгообеспеченность".
 */
const val Provision: Byte = 75 // 'K'

/**
 * АРМ "Комплектатор".
 */
const val Acquisitions: Byte = 77 // 'M'

/**
 * АРМ "Читатель".
 */
const val Reader: Byte = 82 // 'R'

// СТАТУСЫ ЗАПИСЕЙ

/**
 * Нет статуса записи (все нули).
 */
const val NO_STATUS = 0

/**
 * Запись логически удалена.
 */
const val LOGICALLY_DELETED = 1

/**
 * Запись физически удалена.
 */
const val PHYSICALLY_DELETED = 2

/**
 * Запись отсутствует.
 */
const val ABSENT = 4

/**
 * Запись не актуализирована.
 */
const val NON_ACTUALIZED = 8

/**
 * Последняя версия записи.
 */
const val LAST_VERSION = 32

/**
 * Запись заблокирована.
 */
const val LOCKED = 64

/**
 * Ошибка в Autoin.gbl.
 */
const val AutoinError = 128

/**
 * Полный текст не актуализирован.
 */
const val FullTextNotActualized = 256

// ОТДЕЛЬНЫЕ СИМВОЛЫ
/**
 * Is digit from 0 to 9?
 */
@Contract(pure = true)
fun isArabicDigit(c: Char) = c in '0'..'9'

/**
 * Is letter from A to Z or a to z?
 */
@Contract(pure = true)
fun isLatinLetter(c: Char) = (c in 'A'..'Z' || c in 'a'..'z')

/**
 * Is digit from 0 to 9
 * or letter from A to Z or a to z?
 */
@Contract(pure = true)
fun isLatinLetterOrArabicDigit(c: Char) = c in '0'..'9'
        || c in 'A'..'Z' || c in 'a'..'z'

/**
 * Is letter from А to Я or а to я?
 */
@Contract(pure = true)
fun isRussianLetter(c: Char) = c in 'А'..'я' || c == 'Ё' || c == 'ё'

// СТРОКИ

/**
 * Превращаем пустую строку в null.
 *
 * @param text Текст для проверки.
 * @return Тот же текст либо null.
 */
@Contract(value = "null -> null", pure = true)
fun emptyToNull(text: String?): String? {
    return if (text == null || text == "") {
        null
    } else text
}

/**
 * Выбираем строку, не равную null (если такая вообще есть).
 *
 * @param first Первая строка.
 * @param second Вторая строка.
 * @return Та, которая не равна null.
 */
@Contract(value = "!null, _ -> !null", pure = true)
fun iif(first: String?, second: String?): String? {
    return first ?: second
}

/**
 * Выбираем строку, не равную null (если такая вообще есть).
 *
 * @param first Первая строка.
 * @param second Вторая строка.
 * @param third Третья строка.
 * @return Та, которая не равна null.
 */
@Contract(value = "!null, _, _ -> !null; null, !null, _ -> !null", pure = true)
fun iif(first: String?, second: String?, third: String?): String? {
    return first ?: (second ?: third)
}

/**
 * Строка пустая или равна null?
 *
 * @param text Проверяемая строка.
 * @return true, если пустая или равна null.
 */
@Contract(value = "null -> true", pure = true)
fun isNullOrEmpty(text: String?): Boolean {
    return text == null || text.length == 0
}

@Contract(pure = true)
fun sameString(s1: String?, s2: String?): Boolean {
    if (s1 === s2) {
        return true
    }
    return if (s1 == null || s2 == null) {
        false
    } else s1.compareTo(s2, ignoreCase = true) == 0
}

/**
 * Безопасное сравнение строк (любая из них может быть равна null).
 *
 * @param left Первая строка.
 * @param right Вторая строка.
 * @return Результат сравнения.
 */
@Contract(pure = true)
fun safeCompare(left: String?, right: String?): Int {
    if (left == null) {
        return if (right == null) {
            0
        } else -1
    }
    return if (right == null) {
        1
    } else left.compareTo(right)
}

/**
 * Безопасное преобразование строки в целое число.
 *
 * @param text Строка
 * @return Результат преобразования
 */
fun safeParseInt32(text: String?): Int {
    if (isNullOrEmpty(text)) {
        return 0
    }
    val result: Int
    result = try {
        text!!.toInt()
    } catch (ex: Exception) {
        0
    }
    return result
}

@Contract(pure = true)
fun nullToEmpty(value: String?): String {
    return value ?: ""
}

fun nullableToString(value: Any?): String? {
    return value?.toString()
}

fun toVisible(value: Any?): String {
    return value?.toString() ?: "(null)"
}

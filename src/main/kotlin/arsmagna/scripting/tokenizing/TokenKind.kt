@file:Suppress("unused")

package arsmagna.scripting.tokenizing.kinds

/**
 * Комментарий.
 */
const val COMMENT = "comment"

/**
 * Пробел, табуляция, перевод строки.
 */
const val WHITESPACE = "whitespace"

/**
 * Директива.
 */
const val DIRECTIVE = "directive"

/**
 * Терм, например, "{" или "++".
 */
const val TERM = "term"

/**
 * Зарезервированное слово, например, "if".
 */
const val RESERVED = "reserved"

/**
 * Идентификатор.
 */
const val IDENTIFIER = "identifier"

/**
 * Один Unicode-символ в одиночных кавычках.
 */
const val CHAR = "char"

/**
 * Произвольное количество Unicode-символов в двойных кавычках (строка).
 */
const val STRING = "string"

/**
 * Строка без экранирования.
 */
const val RAW = "raw"

/**
 * Интерполируемая строка вида `$"{z} = {x} + {y}"`.
 */
const val INTERPOLATION = "interpolation"

/**
 * Внешний по отношению к Barsik код.
 */
const val EXTERNAL = "external"

/**
 * Альтернативная строка `hello world`
 */
const val ALT = "alt"

/**
 * Целое 32-битное число со знаком без префикса и суффикса.
 */
const val INT32 = "int32"

/**
 * Целое 64-битное число без знака без префикса, суффикс 'L'.
 */
const val INT64 = "int64"

/**
 * Целое 32-битное число без знака без префикса, суффикс 'U'.
 */
const val UINT32 = "uint32"

/**
 * Целое 64-битное число без знака без префикса, суффикс 'UL'.
 */
const val UINT64 = "uint64"

/**
 * Целое 32-битное число без знака c префиксом '0x', без суффикса.
 */
const val HEX32 = "hex32"

/**
 * Целое 64-битное число без знака c префиксом '0x', суффикс 'L'.
 */
const val HEX64 = "hex64"

/**
 * Длинное целое число в десятеричной системе без префикса, суффикс 'B'.
 */
const val BIG = "big"

/**
 * Число с плавающей точкой с одинарной точностью.
 */
const val SINGLE = "single"

/**
 * Число с плавающей точкой с двойной точностью.
 */
const val DOUBLE = "double"

/**
 * Число с фиксированной точкой (денежное).
 */
const val DECIMAL = "decimal"

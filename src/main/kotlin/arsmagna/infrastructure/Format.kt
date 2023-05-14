@file:Suppress("unused")

package arsmagna.infrastructure

import arsmagna.utils.isNullOrEmpty

/**
 * Формат ALL (вывод всех полей записи).
 */
const val ALL = "&uf('+0')"

/**
 * Формат BRIEF (короткое библиографическое описание).
 */
const val BRIEF = "@brief"

/**
 * Формат IBIS.
 */
const val IBIS = "@ibiskw_h"

/**
 * Информационный формат.
 */
const val INFORMATIONAL = "@info_w"

/**
 * Формат, оптимизированный сервером.
 */
const val OPTIMIZED = "@"

/**
 * Удаление комментариев из формата.
 *
 * @param text Формат для обработки.
 * @return Результат обработки.
 */
fun removeComments(text: String): String {
    if (isNullOrEmpty(text)) {
        return text
    }

    if (!text.contains("/*")) {
        return text
    }

    val result = StringBuilder(text.length)
    var state = 0.toChar()
    var index = 0
    val length = text.length
    while (index < length) {
        var c = text[index]
        when (state) {
            '\'', '"', '|' -> {
                if (c == state) {
                    state = 0.toChar()
                }
                result.append(c)
            }

            else -> if (c == '/') {
                if (index + 1 < length && text[index + 1] == '*') {
                    while (index < length) {
                        c = text[index]
                        if (c == '\r' || c == '\n') {
                            result.append(c)
                            break
                        }
                        index++
                    }
                } else {
                    result.append(c)
                }
            } else if (c == '\'' || c == '"' || c == '|') {
                state = c
                result.append(c)
            } else {
                result.append(c)
            }
        }

        index++
    }

    return result.toString()
}

/**
 * Подготовка строки динамического формата для отправки на сервер.
 *
 * @param text Формат для обработки.
 * @return Результат обработки.
 */
fun prepareFormat(text: String): String {
    var phase1 = text
    phase1 = removeComments(phase1)
    val length = phase1.length
    if (length == 0) {
        return phase1
    }

    var flag = false
    for (i in 0 until length) {
        if (phase1[i] < ' ') {
            flag = true
            break
        }
    }

    if (!flag) {
        return phase1
    }

    val result = StringBuilder(length)
    for (i in 0 until length) {
        val c = phase1[i]
        if (c >= ' ') {
            result.append(c)
        }
    }

    return result.toString()
}

package arsmagna.infrastructure

import java.nio.charset.Charset

private var _windows1251 : Charset? = null
private var _utf8: Charset? = null

/**
 * Получение ANSI-кодировки.
 */
fun getAnsiEncoding(): Charset {
    if (_windows1251 == null) {
        _windows1251 = charset("windows-1251")
    }

    return _windows1251!!
}

/**
 * Получение кодировки UTF-8.
 */
fun getUtfEncoding(): Charset {
    if (_utf8 == null) {
        _utf8 = charset("UTF-8")
    }

    return _utf8!!
}

package arsmagna

/**
 * Навигатор по тексту.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class TextNavigator (text: String) {
    private var _column = 1
    private var _length = text.length
    private var _line = 1
    private var _position = 0

    private var _text: String = text

    /**
     * Символ в указанной позиции.
     *
     * @param position Смещение от начала текста в символах.
     * @return Символ или EOF.
     */
    fun charAt(position: Int): Char {
        return if (position in 0 until _length) _text[position] else EOT
    }

    /**
     * Номер текущей колонки (отсчитывается от 1).
     *
     * @return Номер колонки.
     */
    fun column(): Int {
        return _column
    }

    /**
     * Признак конца текста.
     *
     * @return Конец текста?
     */
    fun eot(): Boolean {
        return _position >= _length
    }

    /**
     * Управляющий символ?
     *
     * @return Управляющий символ?
     */
    fun isControl(): Boolean {
        return Character.isISOControl(peekChar())
    }

    /**
     * Цифра?
     *
     * @return Цифра?
     */
    fun isDigit(): Boolean {
        return Character.isDigit(peekChar())
    }

    /**
     * Буква?
     *
     * @return Буква?
     */
    fun isLetter(): Boolean {
        return Character.isLetter(peekChar())
    }

    /**
     * Пробел?
     *
     * @return Пробел?
     */
    fun isWhitespace(): Boolean {
        return Character.isWhitespace(peekChar())
    }

    /**
     * Общая длина текста в символах, включая служебные.
     *
     * @return Длина текста.
     */
    fun length(): Int {
        return _length
    }

    /**
     * Номер текущей строки (отсчет от 1).
     *
     * @return Номер текущей строки.
     */
    fun line(): Int {
        return _line
    }

    /**
     * Подглядывание текущего символа. Не смещает позицию.
     *
     * @return Текущий символ или EOT.
     */
    fun peekChar(): Char {
        return if (_position >= _length) EOT else _text[_position]
    }

    /**
     * Текущая позиция (отсчет от 0, учитываются все символы,
     * в т. ч. служебные). Может быть за пределами текста.
     *
     * @return Текущая позиция.
     */
    fun position(): Int {
        return _position
    }

    /**
     * Чтение текущего символа. Позиция смещается на один символ.
     *
     * @return Текущий символ или EOT.
     */
    fun readChar(): Char {
        if (_position >= _length) {
            return EOT
        }
        val result = _text[_position++]
        if (result == '\n') {
            _line++
            _column = 1
        } else {
            _column++
        }

        return result
    }

    /**
     * Чтение строки.
     *
     * @return Строка или null.
     */
    fun readLine(): String? {
        if (_position >= _length) {
            return null
        }

        val start = _position
        while (_position < _length) {
            val c = _text[_position]
            if (c == '\r' || c == '\n') {
                break
            }
            readChar()
        }

        val result: String = _text.substring(start, _position)
        if (_position < _length) {
            var c = _text[_position]
            if (c == '\r') {
                readChar()
                c = peekChar()
            }
            if (c == '\n') {
                readChar()
            }
        }

        return result
    }

    /**
     * Считывание вплоть до указанного символа (включая его).
     *
     * @param stopChar Стоп-символ.
     * @return Считанная строка или null.
     */
    fun readTo(stopChar: Char): String? {
        if (eot()) {
            return null
        }

        val savePosition = _position
        while (true) {
            val c = readChar()
            if (c == EOT || c == stopChar) {
                break
            }
        }

        return _text.substring(savePosition, _position)
    }

    /**
     * Считывание вплоть до указанного символа
     * (сам символ остается несчитанным).
     *
     * @param stopChar Стоп-символ.
     * @return Считанная строка или null.
     */
    fun readUntil(stopChar: Char): String? {
        if (eot()) {
            return null
        }

        val savePosition = _position
        while (true) {
            val c = peekChar()
            if (c == EOT || c == stopChar) {
                break
            }
            readChar()
        }

        return _text.substring(savePosition, _position)
    }

    /**
     * Отдать остаток текста.
     *
     * @return Остаток текста или null.
     */
    fun remainingText(): String? {
        return if (eot()) {
            null
        } else _text.substring(_position)
    }

    companion object {
        /**
         * Признак конца текста.
         */
        const val EOT = '\u0000'
    }
}

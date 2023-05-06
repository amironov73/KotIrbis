package arsmagna

/**
 * Подполе библиографической записи.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class SubField(var code: Char = '\u0000', var value: String? = null) {
    /**
     * Нет кода подполя, т. е. код пока не задан.
     */
    val NO_CODE = '\u0000'

    /**
     * Разделитель подполей.
     */
    val DELIMITER = '^'

    /**
     * Создание клона подполя.
     * @return Копия подполя.
     */
    fun clone() = SubField(code, value)

    override fun toString(): String = "^$code$value"
}

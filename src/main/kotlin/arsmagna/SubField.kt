package arsmagna

/**
 * Подполе библиографической записи.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class SubField(var code: Char = NO_CODE, var value: String? = null) {
    /**
     * Создание глубокой копии подполя.
     * @return Копия подполя.
     */
    fun clone() = SubField(code, value)

    override fun toString(): String = "^$code$value"
}

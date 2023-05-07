package arsmagna

/**
 * Поле библиографической записи.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Field(var tag: Int = NO_TAG, var value: String? = null) {
    /**
     * Список подполей.
     */
    val subfields = arrayListOf<SubField>()

    /**
     * Создание клона поля.
     * @return Глубокая копия поля.
     */
    fun clone(): Field {
        val result = Field(tag, value)
        for (subfield in subfields) {
            result.subfields.add(subfield.clone())
        }
        return result
    }

    override fun toString(): String {
        return StringBuilder().apply {
            append(tag)
            append('#')
            append(value)
            for (subfield in subfields) {
                append(subfield)
            }
        }.toString()
    }
}
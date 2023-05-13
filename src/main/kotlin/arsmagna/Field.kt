package arsmagna

import arsmagna.utils.compareCodes
import arsmagna.utils.nullableToString
import org.jetbrains.annotations.Contract
import java.util.*


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

    fun add(code: Char, value: Any?): Field {
        val text = nullableToString(value)
        subfields.add(SubField(code, text))
        return this
    }

    fun add(code: Char, condition: Boolean, value: Any?): Field {
        if (condition) {
            val text = nullableToString(value)
            subfields.add(SubField(code, text))
        }
        return this
    }

    fun addNonEmpty(code: Char, value: Any?): Field {
        val text = nullableToString(value)
        if (text != null) {
            subfields.add(SubField(code, text))
        }
        return this
    }

    fun assignFrom(other: Field): Field {
        clear()
        value = other.value
        for (subField in other.subfields) {
            subfields.add(subField.clone())
        }
        return this
    }

    fun assignTo(other: Field): Field {
        other.assignFrom(this)
        return this
    }

    fun clear(): Field {
        value = null
        subfields.clear()
        return this
    }

    @Contract(pure = true)
    fun getFirstSubField(code: Char): SubField? {
        for (subField in subfields) {
            if (compareCodes(code, subField.code) == 0) {
                return subField
            }
        }
        return null
    }

    @Contract(pure = true)
    fun getFirstSubFieldValue(code: Char): String? {
        for (subField in subfields) {
            if (compareCodes(code, subField.code) == 0) {
                return subField.value
            }
        }
        return null
    }

    fun getSubField(code: Char): Array<SubField> {
        val result = LinkedList<SubField>()
        for (subField in subfields) {
            if (compareCodes(code, subField.code) == 0) {
                result.add(subField)
            }
        }
        return result.toTypedArray<SubField>()
    }

    @Contract(pure = true)
    fun getSubField(code: Char, occurrence: Int): SubField? {
        var occ = occurrence
        for (subField in subfields) {
            if (compareCodes(code, subField.code) == 0) {
                if (--occ < 0) {
                    return subField
                }
            }
        }
        return null
    }

    fun getSubFieldValue(code: Char): Array<String?> {
        val result = LinkedList<String?>()
        for (subField in subfields) {
            if (compareCodes(code, subField.code) == 0) {
                val text = subField.value
                if (text != null) {
                    result.add(text)
                }
            }
        }
        return result.toTypedArray<String?>()
    }

    @Contract(pure = true)
    fun getSubFieldValue(code: Char, occurrence: Int): String? {
        var occ = occurrence
        for (subField in subfields) {
            if (compareCodes(code, subField.code) == 0) {
                if (--occ < 0) {
                    return subField.value
                }
            }
        }
        return null
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
package arsmagna

import arsmagna.utils.LOGICALLY_DELETED
import arsmagna.utils.PHYSICALLY_DELETED
import arsmagna.utils.isNullOrEmpty
import arsmagna.utils.nullableToString
import org.jetbrains.annotations.Contract
import java.util.*


/**
 * Библиографическая запись.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Record {
    /**
     * Имя базы данных, которой принадлежит данная запись.
     */
    var database: String? = null

    /**
     * MFN записи.
     */
    var mfn = 0

    /**
     * Версия записи.
     */
    var version = 0

    /**
     * Статус записи.
     */
    var status = 0

    /**
     * Список полей записи.
     */
    val fields = arrayListOf<Field>()

    /**
     * Произвольные пользовательские данные.
     */
    var userData: Any? = null

    fun addField(tag: Int, value: Any?): Record {
        val field = Field(tag, nullableToString(value))
        fields.add(field)
        return this
    }

    fun addField(tag: Int, condition: Boolean, value: Any?): Record {
        if (condition) {
            val field = Field(tag, nullableToString(value))
            fields.add(field)
        }
        return this
    }

    fun addField(tag: Int, vararg subFields: SubField): Record {
        val field = Field(tag)
        for (subField in subFields) {
            field.subfields.add(subField)
        }
        fields.add(field)
        return this
    }

    fun addField(tag: Int, condition: Boolean, vararg subFields: SubField): Record {
        if (condition) {
            val field = Field(tag)
            for (subField in subFields) {
                field.subfields.add(subField)
            }
            fields.add(field)
        }
        return this
    }

    fun addNonEmptyField(tag: Int, value: Any?): Record {
        val text = nullableToString(value)
        if (!isNullOrEmpty(text)) {
            val field = Field(tag, text)
            fields.add(field)
        }
        return this
    }

    /**
     * Создание клона записи.
     * @return Глубокая копия записи.
     */
    fun clone(): Record {
        val result = Record()
        result.mfn = mfn
        result.version = version
        result.status = status
        for (field in fields) {
            result.fields.add(field.clone())
        }

        return result
    }

    @Contract(pure = true)
    fun fm(tag: Int): String? {
        for (field in fields) {
            if (field.tag == tag) {
                return field.value
            }
        }
        return null
    }

    @Contract(pure = true)
    fun fm(tag: Int, code: Char): String? {
        for (field in fields) {
            if (field.tag == tag) {
                return field.getFirstSubFieldValue(code)
            }
        }
        return null
    }

    fun fma(tag: Int): Array<String> {
        val result = LinkedList<String>()
        for (field in fields) {
            if (field.tag == tag) {
                val fieldValue = field.value
                if (fieldValue != null) {
                    result.add(fieldValue)
                }
            }
        }
        return result.toTypedArray<String>()
    }

    fun fma(tag: Int, code: Char): Array<String> {
        val result = LinkedList<String>()
        for (field in fields) {
            if (field.tag == tag) {
                val text = field.getFirstSubFieldValue(code)
                if (text != null) {
                    result.add(text)
                }
            }
        }
        return result.toTypedArray<String>()
    }

    fun getField(tag: Int): Array<Field?> {
        val result: LinkedList<Field> = LinkedList<Field>()
        for (field in fields) {
            if (field.tag == tag) {
                result.add(field)
            }
        }
        return result.toArray(arrayOfNulls<Field>(0))
    }

    @Contract(pure = true)
    fun getField(tag: Int, occurrence: Int): Field? {
        var occ = occurrence
        for (field in fields) {
            if (field.tag == tag && --occ < 0) {
                return field
            }
        }
        return null
    }

    @Contract(pure = true)
    fun getFirstField(tag: Int): Field? {
        for (field in fields) {
            if (field.tag == tag) {
                return field
            }
        }
        return null
    }

    /**
     * Удалена ли запись?
     *
     * @return true, если запись удалена.
     */
    @Contract(pure = true)
    fun isDeleted(): Boolean = status and (LOGICALLY_DELETED or PHYSICALLY_DELETED) != 0

    override fun toString(): String {
        return StringBuilder().apply {
            append(mfn)
            append('#')
            append(status)
            appendLine()
            append(0)
            append('#')
            append(version)
            appendLine()
            for (field in fields) {
                append(field)
                appendLine()
            }
        }.toString()
    }
}
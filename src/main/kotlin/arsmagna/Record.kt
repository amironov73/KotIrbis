package arsmagna

import arsmagna.utils.isNullOrEmpty
import arsmagna.utils.nullableToString

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
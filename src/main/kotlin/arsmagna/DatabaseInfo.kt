package arsmagna

import arsmagna.utils.SHORT_DELIMITER
import arsmagna.utils.fastParseInt
import org.jetbrains.annotations.Contract

/**
 * Информация о базе данных ИРБИС.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class DatabaseInfo {
    /**
     * Имя базы данных.
     */
    var name: String? = null

    /**
     * Описание базы данных.
     */
    var description: String? = null

    /**
     * Максимальный MFN.
     */
    var maxMfn = 0

    /**
     * Логически удаленные записи.
     */
    var logicallyDeletedRecords: IntArray? = null

    /**
     * Физически удаленные записи.
     */
    var physicallyDeletedRecords: IntArray? = null

    /**
     * Не актуализированные записи.
     */
    var nonActualizedRecords: IntArray? = null

    /**
     * Заблокированные записи.
     */
    var lockedRecords: IntArray? = null

    /**
     * Признак блокировки базы данных в целом.
     */
    var databaseLocked = false

    /**
     * База только для чтения.
     */
    var readOnly = false

    companion object {
        fun parse(line: String): IntArray {
            if (line.isEmpty()) {
                return intArrayOf()
            }

            val items = line.split(SHORT_DELIMITER)
            val result = IntArray(items.size)
            for (i in items.indices) {
                result[i] = fastParseInt(items[i])
            }

            return result

        }

    }

    @Contract(pure = true)
    override fun toString(): String {
        return if (description == null) {
            name!!
        } else "$name - $description"
    }
}
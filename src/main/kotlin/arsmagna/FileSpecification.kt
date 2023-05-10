package arsmagna

import org.jetbrains.annotations.Contract
import java.util.*

/**
 * Путь на файл на сервере ИРБИС64
 * в формате `path.database.filename`.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class FileSpecification {
    /**
     * Признак двоичного файла.
     */
    var isBinaryFile = false

    /**
     * Код пути.
     */
    var path: Int

    /**
     * База данных.
     */
    var database: String? = null

    /**
     * Имя файла.
     */
    var fileName: String

    /**
     * Содержимое текстового файла.
     */
    var content: String? = null

    constructor(path: Int, database: String, fileName: String) {
        this.path = path
        this.database = database
        this.fileName = fileName
    }

    constructor(path: Int, fileName: String) {
        this.path = path
        this.fileName = fileName
    }

    override fun hashCode(): Int {
        return Objects.hash(path, database, fileName, content)
    }

    @Contract(value = "null -> false", pure = true)
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other === this) {
            return true
        }

        if (other is FileSpecification) {
            return path == other.path && database == other.database && fileName == other.fileName
        }

        return false
    }

    override fun toString(): String {
        var result = fileName
        if (isBinaryFile) {
            result = "@$fileName"
        } else if (content != null) {
            result = "&$fileName"
        }

        result = when (path) {
            0, 1 -> "$path..$result"
            else -> "$path.$database.$result"
        }

        if (content != null) {
            result = "$result&$content"
        }

        return result
    }
}

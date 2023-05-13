package arsmagna.infrastructure

/**
 * Строка INI-файла.
 */
class IniLine(var key: String, var value: String?) {
    override fun toString() = "$key=$value"
}

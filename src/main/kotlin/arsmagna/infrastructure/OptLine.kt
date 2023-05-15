package arsmagna.infrastructure


/**
 * Строка OPT-файла.
 */
class OptLine {
    /**
     * Паттерн.
     */
    var pattern: String? = null

    /**
     * Соответствующий рабочий лист.
     */
    var worksheet: String? = null

    /**
     * Разбор строки OPT-файла.
     */
    fun parse(text: String) {
        val parts = text.trim { it <= ' ' }
            .split("\\s+".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        pattern = parts[0]
        worksheet = parts[1]
    }

    override fun toString() = "$pattern $worksheet"
}
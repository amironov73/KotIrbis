package arsmagna.infrastructure


/**
 * Секция INI-файла.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class IniSection(var name: String? = null) {
    val lines = mutableListOf<IniLine>()

    /**
     * Find the line by the key.
     *
     * @param key Key to search for.
     * @return Found line or null.
     */
    fun find(key: String): IniLine? {
        for (line in lines) {
            if (sameKey(line.key, key)) {
                return line
            }
        }
        return null
    }

    /**
     * Get the value for the specified key.
     * @param key Key to search for.
     * @param defaultValue Default value to use if no lines found.
     * @return Found value or null.
     */
    fun getValue(key: String, defaultValue: String? = null): String? {
        val found = find(key)
        return if (found == null) defaultValue else found.value
    }

    /**
     * Remove the line with the specified key.
     * @param key Key to remove.
     */
    fun remove(key: String) {
        val found = find(key)
        if (found != null) {
            lines.remove(found)
        }
    }

    /**
     * Set the line value by the key.
     *
     * @param key   Key to search for.
     * @param value Value to set (can be null).
     */
    fun setValue(key: String, value: String?): IniSection {
        var line = find(key)
        if (line == null) {
            line = IniLine(key, value)
            lines.add(line)
        } else {
            line.value = value
        }
        return this
    }
}
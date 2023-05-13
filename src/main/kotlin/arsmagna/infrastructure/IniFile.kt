package arsmagna.infrastructure

import java.io.File
import java.io.IOException
import java.io.StringReader
import java.util.*


/**
 * INI-файл.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class IniFile {
    /**
     * Секции.
     */
    val sections = mutableListOf<IniSection>()

    fun findSection(name: String?): IniSection? {
        for (section in sections) {
            if (name == null) {
                if (section.name == null) {
                    return section
                }
            } else if (section.name != null && sameKey(section.name!!, name)) {
                return section
            }
        }
        return null
    }

    fun getOrCreateSection(name: String?): IniSection {
        var result = findSection(name)
        if (result == null) {
            result = IniSection(name)
            sections.add(result)
        }
        return result
    }

    fun getValue(sectionName: String?, key: String, defaultValue: String? = null): String? {
        return getValue(sectionName, key, defaultValue)
    }

    fun parse(scanner: Scanner): IniFile {
        val result = IniFile()
        var section: IniSection? = null
        while (scanner.hasNext()) {
            var line: String = scanner.nextLine()
            line = line.trim { it <= ' ' }
            if (line == "") {
                continue
            }
            if (line.startsWith("[")) {
                line = line.substring(1, line.length - 1)
                section = IniSection(line)
                result.sections.add(section)
            } else if (section != null) {
                val parts = line.split("=".toRegex(), limit = 2).toTypedArray()
                val key = parts[0]
                val value = if (parts.size == 2) parts[1] else ""
                val item = IniLine(key, value)
                if (section == null) {
                    section = IniSection(null)
                    result.sections.add(section)
                }
                section.lines.add(item)
            }
        }
        return result
    }

    @Throws(IOException::class)
    fun parse(file: File): IniFile? {
        TODO()
//        FileInputStream(file).use { stream ->
//            Scanner(stream, IrbisEncoding.ansi().name()).use { scanner ->
//                val result = parse(scanner)
//                result.fileName = file.absolutePath
//                return result
//            }
//        }
    }

    @Throws(IOException::class)
    fun parse(response: ServerResponse): IniFile {
        val text = response.readRemainingAnsiText()
        val reader = StringReader(text)
        val scanner = Scanner(reader)
        return parse(scanner)
    }

    fun setValue(
        sectionName: String?, key: String,
        value: String?
    ): IniFile {
        val section = getOrCreateSection(sectionName)
        section.setValue(key, value)
        return this
    }

    override fun toString(): String {
        val result = StringBuilder()
        var first = true
        for (section in sections) {
            if (!first) {
                result.append("\n")
            }
            first = false
            result.append(section)
        }
        return result.toString()
    }
}

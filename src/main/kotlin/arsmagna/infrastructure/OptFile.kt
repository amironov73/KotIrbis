package arsmagna.infrastructure

import arsmagna.Connection
import arsmagna.FileSpecification
import arsmagna.IrbisException
import arsmagna.Record
import org.jetbrains.annotations.Contract
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.StringReader
import java.util.*

/**
 * OPT-файл.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class OptFile {
    companion object {
        const val WILDCARD = '+'
    }

    val lines = mutableListOf<OptLine>()

    var worksheetLength = 0

    var worksheetTag = 0

    @Contract(pure = true)
    fun getWorksheet(record: Record): String {
        return record.fm(worksheetTag)!!
    }

    @Throws(IOException::class)
    fun parse(file: File) {
        FileInputStream(file).use { stream ->
            Scanner(stream, getAnsiEncoding()).use {scanner ->
                parse(scanner)
            }
        }
    }

    fun parse(scanner: Scanner) {
        worksheetTag = scanner.nextInt()
        worksheetLength = scanner.nextInt()
        while (scanner.hasNext()) {
            var text = scanner.nextLine()
            text = text.trim { it <= ' ' }
            if (text.startsWith("*")) {
                break
            }
            val line = OptLine()
            line.parse(text)
            lines.add(line)
        }
    }

    @Throws(IOException::class)
    fun parse(response: ServerResponse) {
        val text = response.readRemainingAnsiText()
        val reader = StringReader(text)
        val scanner = Scanner(reader)
        parse(scanner)
    }

    @Throws(IOException::class)
    fun read(
        connection: Connection,
        specification: FileSpecification
    ): OptFile {
        val text = connection.readTextContent(specification)
        val reader = StringReader(text)
        val scanner = Scanner(reader)
        val result = OptFile()
        result.parse(scanner)

        return result
    }

    private fun sameChar(pattern: Char, testable: Char): Boolean {
        return if (pattern == WILDCARD) {
            true
        } else pattern.lowercaseChar() == testable.lowercaseChar()
    }

    private fun sameText(pattern: String, testable: String): Boolean {
        if (pattern.isEmpty()) {
            return false
        }

        if (testable.isEmpty()) {
            return pattern[0] == WILDCARD
        }

        var patternIndex = 0
        var testableIndex = 0
        while (true) {
            var patternChar = pattern[patternIndex]
            val testableChar = testable[testableIndex]
            val patternNext = patternIndex++ < pattern.length
            val testableNext = testableIndex++ < testable.length
            if (patternNext && !testableNext) {
                if (patternChar == WILDCARD) {
                    while (patternIndex < pattern.length) {
                        patternChar = pattern[patternIndex]
                        patternIndex++
                        if (patternChar != WILDCARD) {
                            return false
                        }
                    }
                    return true
                }
            }

            if (patternNext != testableNext) {
                return false
            }
            if (!patternNext) {
                return true
            }
            if (!sameChar(patternChar, testableChar)) {
                return false
            }
        }
    }

    @Throws(IrbisException::class)
    fun resolveWorksheet(tag: String): String {
        for (line in lines) {
            if (sameText(line.pattern!!, tag)) {
                return line.worksheet!!
            }
        }
        throw IrbisException()
    }

}
package arsmagna.scripting.parsing

import arsmagna.scripting.tokenizing.Token

/**
 * Хранит состояние в процессе парсинга.
 */
class ParseState(val tokens: Array<Token>) {
    /**
     * Общее количество обработанных токенов.
     * Проще говоря, текущее абсолютное смещение от начала
     * входного потока.
     */
    var location = 0
}

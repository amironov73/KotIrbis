package arsmagna.scripting.parsing

/**
 * Интерфейс парсера.
 */
@Suppress("unused")
interface IParser<TResult> {
    /**
     * Попытка разбора текущего токена.
     */
    fun parse(state: ParseState):  Result<TResult>
}

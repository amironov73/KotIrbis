package arsmagna.scripting

/**
 * Синтаксическая ошибка в скрипте
 */
@Suppress("unused")
class SyntaxException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
}
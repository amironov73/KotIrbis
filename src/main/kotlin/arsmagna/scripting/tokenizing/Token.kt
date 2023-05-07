package arsmagna.scripting.tokenizing

/**
 * Токен.
 */
@Suppress("unused")
class Token(
    val kind: String, val value: String?,
    val offset: Int = 0, val line: Int = 0,
    val column: Int = 0, val userData: Any? = null
) {
}
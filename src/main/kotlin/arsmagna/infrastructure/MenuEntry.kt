package arsmagna.infrastructure

import org.jetbrains.annotations.Contract

/**
 * Пара строк в ИРБИС-меню.
 */
class MenuEntry(var code: String, var comment: String) {
    @Contract(pure = true)
    override fun toString(): String = "$code - $comment"

}
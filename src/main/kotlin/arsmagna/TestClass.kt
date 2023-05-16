package arsmagna

/**
 * Некий тестовый класс, не несущий полезной нагрузки.
 * Содержит два свойства: [first] и [second].
 *
 * @property first Первое свойство.
 * @property second Второе свойство.
 * @constructor Создание экземпляра.
 */
@Suppress("unused")
class TestClass(val first: String, val second: String) {

    /**
     * Прочее.
     */
    var other: Any? = null

    /**
     * Метод, делающий нечто очень сложное и важное.
     *
     * @param name Имя.
     * @param value Задаваемое значение.
     * @return Вычисленное количество.
     */
    fun someMethod(name: String, value: Any?): Int {
        requireNotNull(name)
        println("value for $name is $value")
        return 1
    }

    override fun equals(other: Any?) = if (other is TestClass)
        first == other.first && second == other.second
    else false

    override fun hashCode() = first.hashCode() * 137 + second.hashCode()
    override fun toString() = "$first - $second"
}
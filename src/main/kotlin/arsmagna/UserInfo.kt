package arsmagna

import arsmagna.infrastructure.ServerResponse
import arsmagna.utils.emptyToNull
import arsmagna.utils.sameString
import org.jetbrains.annotations.Contract

/**
 * Информация о зарегистрированном пользователе системы
 * (по данным client_m.mnu).
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class UserInfo {
    /**
     * Номер по порядку в списке.
     */
    var number: String? = null

    /**
     * Логин.
     */
    var name: String? = null

    /**
     * Пароль.
     */
    var password: String? = null

    /**
     * Доступность АРМ Каталогизатор.
     */
    var cataloger: String? = null

    /**
     * АРМ Читатель.
     */
    var reader: String? = null

    /**
     * АРМ Книговыдача.
     */
    var circulation: String? = null

    /**
     * АРМ Комплектатор.
     */
    var acquisitions: String? = null

    /**
     * АРМ Книгообеспеченность.
     */
    var provision: String? = null

    /**
     * АРМ Администратор.
     */
    var administrator: String? = null

    /**
     * Произвольные пользовательские данные.
     */
    var userData: Any? = null

    //=========================================================================

    //=========================================================================
    private fun formatPair(
        prefix: String,
        value: String?,
        defaultValue: String
    ): String {
        return if (sameString(value, defaultValue)) "" else "$prefix=$value;"
    }

    //=========================================================================

    //=========================================================================
    /**
     * Кодирование информации о пользователе в три строки.
     *
     * @return Закодированная информация.
     */
    fun encode(): String {
        return name + "\r\n" + password + "\r\n" +
        formatPair("C", cataloger,     "irbisc.ini") +
        formatPair("R", reader,        "irbisr.ini") +
        formatPair("B", circulation,   "irbisb.ini") +
        formatPair("M", acquisitions,  "irbism.ini") +
        formatPair("K", provision,     "irbisk.ini") +
        formatPair("A", administrator, "irbisa.ini")
    }

    companion object {

        /**
         * Разбор ответа сервера.
         *
         * @param response Ответ сервера.
         * @return Перечень пользователей.
         */
        fun parse(response: ServerResponse): Array<UserInfo> {
            val result = ArrayList<UserInfo>()
            val userCount = response.readInt32()
            val linesPerUser = response.readInt32()
            if (userCount == 0 || linesPerUser == 0) {
                return arrayOf()
            }
            for (i in 0 until userCount) {
                val lines = response.readAnsi(linesPerUser + 1) ?: break
                if (lines.isEmpty()) {
                    break
                }

                val user = UserInfo()
                user.number = emptyToNull(lines[0])
                if (lines.size > 1) {
                    user.name = emptyToNull(lines[1])
                }
                if (lines.size > 2) {
                    user.password = emptyToNull(lines[2])
                }
                if (lines.size > 3) {
                    user.cataloger = emptyToNull(lines[3])
                }
                if (lines.size > 4) {
                    user.reader = emptyToNull(lines[4])
                }
                if (lines.size > 5) {
                    user.circulation = emptyToNull(lines[5])
                }
                if (lines.size > 6) {
                    user.acquisitions = emptyToNull(lines[6])
                }
                if (lines.size > 7) {
                    user.provision = emptyToNull(lines[7])
                }
                if (lines.size > 8) {
                    user.administrator = emptyToNull(lines[8])
                }

                result.add(user)
            }

            return result.toTypedArray()
        }
    }

    //=========================================================================

    //=========================================================================
    @Contract(pure = true)
    override fun toString(): String {
        return "UserInfo{" + "number='" + number + '\'' +
        ", name='" + name + '\'' + ", password='" + password + '\'' +
        ", cataloger='" + cataloger + '\'' +
        ", reader='" + reader + '\'' +
        ", circulation='" + circulation + '\'' +
        ", acquisitions='" + acquisitions + '\'' +
        ", provision='" + provision + '\'' +
        ", administrator='" + administrator + '\'' + '}'
    }

}
package arsmagna

import arsmagna.infrastructure.ServerResponse
import arsmagna.utils.emptyToNull
import org.jetbrains.annotations.Contract

/**
 * Информация о запущенном на сервере ИРБИС64 процессе.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ProcessInfo {
    /**
     * Просто порядковый номер в списке.
     */
    var number: String? = null

    /**
     * С каким клиентом взаимодействует.
     */
    var ipAddress: String? = null

    /**
     * Логин оператора.
     */
    var name: String? = null

    /**
     * Идентификатор клиента.
     */
    var clientId: String? = null

    /**
     * Тип АРМ.
     */
    var workstation: String? = null

    /**
     * Время запуска.
     */
    var started: String? = null

    /**
     * Последняя выполненная (или выполняемая) команда.
     */
    var lastCommand: String? = null

    /**
     * Порядковый номер последней команды.
     */
    var commandNumber: String? = null

    /**
     * Идентификатор процесса.
     */
    var processId: String? = null

    /**
     * Состояние.
     */
    var state: String? = null

    /**
     * Произвольные пользовательские данные.
     */
    var userData: Any? = null

    companion object {
        /**
         * Разбор ответа сервера.
         *
         * @param response Ответ сервера.
         * @return Список процессов.
         */
        fun parse(response: ServerResponse): Array<ProcessInfo> {
            val result = ArrayList<ProcessInfo>()
            val processCount = response.readInt32()
            val linesPerProcess = response.readInt32()
            if (processCount == 0 || linesPerProcess == 0) {
                return arrayOf()
            }

            for (i in 0 until processCount) {
                val lines = response.readAnsi(linesPerProcess + 1) ?: break
                val process = ProcessInfo()
                if (lines.isNotEmpty()) {
                    process.number = emptyToNull(lines[0])
                }
                if (lines.size > 1) {
                    process.ipAddress = emptyToNull(lines[1])
                }
                if (lines.size > 2) {
                    process.name = emptyToNull(lines[2])
                }
                if (lines.size > 3) {
                    process.clientId = emptyToNull(lines[3])
                }
                if (lines.size > 4) {
                    process.workstation = emptyToNull(lines[4])
                }
                if (lines.size > 5) {
                    process.started = emptyToNull(lines[5])
                }
                if (lines.size > 6) {
                    process.lastCommand = emptyToNull(lines[6])
                }
                if (lines.size > 7) {
                    process.commandNumber = emptyToNull(lines[7])
                }
                if (lines.size > 8) {
                    process.processId = emptyToNull(lines[8])
                }
                if (lines.size > 9) {
                    process.state = emptyToNull(lines[9])
                }
                result.add(process)
            }

            return result.toTypedArray()
        }
    }

    @Contract(pure = true)
    override fun toString(): String {
        return "ProcessInfo{" +
                "number='" + number + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", name='" + name + '\'' +
                ", clientId='" + clientId + '\'' +
                ", workstation='" + workstation + '\'' +
                ", started='" + started + '\'' +
                ", lastCommand='" + lastCommand + '\'' +
                ", commandNumber='" + commandNumber + '\'' +
                ", processId='" + processId + '\'' +
                ", state='" + state + '\'' +
                '}'
    }
}

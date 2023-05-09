package arsmagna

import org.jetbrains.annotations.Contract

/**
 * Информация о клиенте, подключенном к серверу ИРБИС64,
 * не обязательно о текущем
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ClientInfo {
    /**
     * Порядковый номер.
     */
    var number: String? = null

    /**
     * Адрес клиента.
     */
    var ipAddress: String? = null

    /**
     * Порт клиента.
     */
    var port: String? = null

    /**
     * Логин.
     */
    var name: String? = null

    /**
     * Идентификатор клиентской программы
     * (просто уникальное число).
     */
    var id: String? = null

    /**
     * Клиентский АРМ.
     */
    var workstation: String? = null

    /**
     * Время подключения к серверу.
     */
    var registered: String? = null

    /**
     * Последнее подтверждение, посланное серверу.
     */
    var acknowledged: String? = null

    /**
     * Последняя команда, посланная серверу.
     */
    var lastCommand: String? = null

    /**
     * Номер последней команды.
     */
    var commandNumber: String? = null

    @Contract(pure = true)
    override fun toString(): String {
        return "ClientInfo{" +
                "number='" + number + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port='" + port + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", workstation='" + workstation + '\'' +
                ", registered='" + registered + '\'' +
                ", acknowledged='" + acknowledged + '\'' +
                ", lastCommand='" + lastCommand + '\'' +
                ", commandNumber='" + commandNumber + '\'' +
                '}'
    }
}

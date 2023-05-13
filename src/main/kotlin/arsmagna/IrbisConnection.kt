package arsmagna

import java.util.*


/**
 * Подключение к серверу ИРБИС64.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class IrbisConnection {
    /**
     * Адрес сервера.
     */
    var host: String = "localhost"

    /**
     * Порт сервера.
     */
    var port = 6666

    /**
     * Имя пользователя (логин).
     */
    var username: String? = null

    /**
     * Пароль.
     */
    var password: String? = null

    /**
     * Имя текущей базы данных.
     */
    var database: String = "IBIS"

    /**
     * Тип АРМ.
     */
    var workstation = 'C'

    /**
     * Идентификатор клиента.
     */
    var clientId = 0

    /**
     * Номер команды.
     */
    var queryId = 0

//    /**
//     * Полученный с сервера INI-файл.
//     */
//    var iniFile: IniFile? = null

    /**
     * Произвольные пользовательские данные.
     */
    var userData: Any? = null

    private var _isConnected = false

    private val _databaseStack: Stack<String> = Stack()

}
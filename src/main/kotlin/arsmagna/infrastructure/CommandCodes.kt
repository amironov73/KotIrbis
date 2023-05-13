@file:Suppress("unused")

package arsmagna.infrastructure

/**
 * Получение признака монопольной блокировки базы данных.
 */
const val EXCLUSIVE_DATABASE_LOCK = "#"

/**
 * Получение списка удаленных, неактуализированных
 * и заблокированных записей.
 */
const val RECORD_LIST = "0"

/**
 * Получение версии сервера.
 */
const val SERVER_INFO = "1"

/**
 * Получение статистики по базе данных.
 */
const val DATABASE_STAT = "2"

/**
 * IRBIS_FORMAT_ISO_GROUP.
 */
const val FORMAT_ISO_GROUP = "3"

/**
 * ???
 */
const val UNKNOWN_COMMAND_4 = "4"

/**
 * Глобальная корректировка.
 */
const val GLOBAL_CORRECTION = "5"

/**
 * Сохранение группы записей.
 */
const val SAVE_RECORD_GROUP = "6"

/**
 * Печать.
 */
const val PRINT = "7"

/**
 * Запись параметров в ini-файл, расположенный на сервере.
 */
const val UPDATE_INI_FILE = "8"

/**
 * IRBIS_IMPORT_ISO.
 */
const val IMPORT_ISO = "9"

/**
 * Регистрация клиента на сервере.
 */
const val REGISTER_CLIENT = "A"

/**
 * Разрегистрация клиента.
 */
const val UNREGISTER_CLIENT = "B"

/**
 * Чтение записи, ее расформатирование.
 */
const val READ_RECORD = "C"

/**
 * Сохранение записи.
 */
const val UPDATE_RECORD = "D"

/**
 * Разблокировка записи.
 */
const val UNLOCK_RECORD = "E"

/**
 * Актуализация записи.
 */
const val ACTUALIZE_RECORD = "F"

/**
 * Форматирование записи или группы записей.
 */
const val FORMAT_RECORD = "G"

/**
 * Получение терминов и ссылок словаря, форматирование записей
 */
const val READ_TERMS = "H"

/**
 * Получение ссылок для термина (списка терминов).
 */
const val READ_POSTINGS = "I"

/**
 * Глобальная корректировка виртуальной записи.
 */
const val CORRECT_VIRTUAL_RECORD = "J"

/**
 * Поиск записей с опциональным форматированием
 */
const val SEARCH = "K"

/**
 * Получение/сохранение текстового файла, расположенного
 * на сервере (группы текстовых файлов).
 */
const val READ_DOCUMENT = "L"

/**
 * IRBIS_BACKUP.
 */
const val BACKUP = "M"

/**
 * Пустая операция. Периодическое подтверждение
 * соединения с сервером.
 */
const val NOP = "N"

/**
 * Получение максимального MFN для базы данных.
 */
const val GET_MAX_MFN = "O"

/**
 * Получение терминов и ссылок словаря в обратном порядке.
 */
const val READ_TERMS_REVERSE = "P"

/**
 * Разблокирование записей.
 */
const val UNLOCK_RECORDS = "Q"

/**
 * Полнотекстовый поиск.
 */
const val FULL_TEXT_SEARCH = "R"

/**
 * Опустошение базы данных.
 */
const val EMPTY_DATABASE = "S"

/**
 * Создание базы данных.
 */
const val CREATE_DATABASE = "T"

/**
 * Разблокирование базы данных.
 */
const val UNLOCK_DATABASE = "U"

/**
 * Чтение ссылок для заданного MFN.
 */
const val GET_RECORD_POSTINGS = "V"

/**
 * Удаление базы данных.
 */
const val DELETE_DATABASE = "W"

/**
 * Реорганизация мастер-файла.
 */
const val RELOAD_MASTER_FILE = "X"

/**
 * Реорганизация словаря.
 */
const val RELOAD_DICTIONARY = "Y"

/**
 * Создание поискового словаря заново.
 */
const val CREATE_DICTIONARY = "Z"

/**
 * Получение статистики работы сервера.
 */
const val GET_SERVER_STAT = "+1"

/**
 * ???
 */
const val UNKNOWN_COMMAND_PLUS_2 = "+2"

/**
 * Получение списка запущенных процессов.
 */
const val GET_PROCESS_LIST = "+3"

/**
 * ???
 */
const val UNKNOWN_COMMAND_PLUS_4 = "+4"

/**
 * ???
 */
const val UNKNOWN_COMMAND_PLUS_5 = "+5"

/**
 * ???
 */
const val UNKNOWN_COMMAND_PLUS_6 = "+6"

/**
 * Сохранение списка пользователей.
 */
const val SET_USER_LIST = "+7"

/**
 * Перезапуск сервера.
 */
const val RESTART_SERVER = "+8"

/**
 * Получение списка пользователей.
 */
const val GET_USER_LIST = "+9"

/**
 * Получение списка файлов на сервере.
 */
const val LIST_FILES = "!"

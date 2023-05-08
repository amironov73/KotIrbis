@file:Suppress("unused")

package arsmagna

/**
 * Код, задающий путь к файлам на сервере ИРБИС64.
 */
enum class IrbisPath(val path: Int) {
    /**
     * Общесистемный путь
     */
    SYSTEM(0),

    /**
     * Путь размещения сведений о базах данных сервера ИРБИС64
     */
    DATA(1),

    /**
     * Путь на мастер-файл базы данных
     */
    MASTER_FILE(2),

    /**
     * Путь на словарь базы данных
     */
    INVERTED_FILE(3),

    /**
     * путь на параметрию базы данных
     */
    PARAMETER_FILE(10),

    /**
     * Полный текст
     */
    FULL_TEXT(11),

    /**
     * Внутренний ресурс
     */
    INTERNAL_RESOURCE(12)
}
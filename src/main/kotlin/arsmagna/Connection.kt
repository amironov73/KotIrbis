package arsmagna

import arsmagna.infrastructure.*
import arsmagna.infrastructure.ClientQuery
import arsmagna.infrastructure.IniFile
import arsmagna.infrastructure.ServerResponse
import arsmagna.utils.*
import org.jetbrains.annotations.Contract
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import java.net.Socket
import java.util.*


/**
 * Подключение к серверу ИРБИС64.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Connection {
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

    /**
     * Полученный с сервера INI-файл.
     */
    var iniFile: IniFile? = null

    /**
     * Произвольные пользовательские данные.
     */
    var userData: Any? = null

    private var _isConnected = false

    private val _databaseStack: Stack<String> = Stack()

    /**
     * Актуализация записи с указанным MFN.
     *
     * @param database Имя базы данных.
     * @param mfn      MFN.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun actualizeRecord(database: String, mfn: Int) {
        val query = ClientQuery(this, ACTUALIZE_RECORD)
        query.addAnsi(database)
        query.add(mfn)
        execute(query).use { response -> response.checkReturnCode() }
    }

    /**
     * Подключение к серверу ИРБИС64.
     *
     * @return INI-файл пользователя.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun connect(): Boolean {
        if (_isConnected) {
            return true
        }
        //logInfo("Connecting to $host:$port")
        while (true) {
            queryId = 0
            clientId = 100000 + Random().nextInt(800000)
            val query = ClientQuery(this, REGISTER_CLIENT)
            query.addAnsi(username)
            query.addAnsiNoLF(password)
            execute(query).use { response ->
                val allowed = intArrayOf(-3337)
                response.checkReturnCode(allowed)
                if (response.returnCode != -3337) {
                    response.readAnsi()
                    _isConnected = true
                    //iniFile = IniFile.parse(response)
                    return true
                }
            }
        }
    }

    /**
     * Создание базы данных.
     *
     * @param databaseName Имя создаваемой базы.
     * @param description  Описание в свободной форме.
     * @param readerAccess Читатель будет иметь доступ?
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun createDatabase(
        databaseName: String,
        description: String,
        readerAccess: Boolean
    ) {
        val query = ClientQuery(this, CREATE_DATABASE)
        query.addAnsi(databaseName)
        query.addAnsi(description)
        query.add(readerAccess)
        execute(query).use { response -> response.checkReturnCode() }
    }

    /**
     * Создание словаря в указанной базе данных.
     *
     * @param databaseName Имя базы данных.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun createDictionary(databaseName: String) {
        val query = ClientQuery(this, CREATE_DICTIONARY)
        query.addAnsi(databaseName)
        execute(query).use { response -> response.checkReturnCode() }
    }

    /**
     * Удаление указанной базы данных.
     *
     * @param databaseName Имя удаляемой базы.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun deleteDatabase(databaseName: String) {
        val query = ClientQuery(this, DELETE_DATABASE)
        query.addAnsi(databaseName)
        execute(query).use { response -> response.checkReturnCode() }
    }

    /**
     * Удаление записи по её MFN.
     *
     * @param mfn MFN удаляемой записи.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun deleteRecord(mfn: Int) {
        val record: Record = readRecord(mfn)
        if (!record.isDeleted()) {
            record.status = record.status or LOGICALLY_DELETED
            writeRecord(record, false, true, true)
        }
    }

    /**
     * Отключение от сервера.
     *
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun disconnect() {
        if (!_isConnected) {
            return
        }
        // logInfo("Disconnecting from $host:$port")
        val query = ClientQuery(this, UNREGISTER_CLIENT)
        query.addAnsiNoLF(username)
        executeAndForget(query)
    }

    /**
     * Выполнение произвольного запроса к серверу.
     *
     * @param query Запрос.
     * @return Ответ сервера (не забыть закрыть!).
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun execute(query: ClientQuery): ServerResponse {
        val socket = Socket(host, port)
        // socket.setSoTimeout(30_000); // milliseconds
        val outputData = query.encode()
        val outputStream = socket.getOutputStream()
        outputStream.write(outputData[0])
        outputStream.write(outputData[1])
        // closing the OutputStream will close the associated socket!
        // socket will be closed by ServerResponse.close method
        return ServerResponse(socket)
    }

    /**
     * Выполнение запроса, когда нам не важен результат
     * (мы не собираемся его парсить).
     *
     * @param query Клиентский запрос.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun executeAndForget(query: ClientQuery) {
        execute(query).use { response -> response.nop() }
    }

    /**
     * Простой вызов сервера, когда все строки запроса в кодировке ANSI.
     *
     * @param commands Команда и параметры запроса (не забыть закрыть!).
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun executeAnsi(vararg commands: String) {
        val query = ClientQuery(this, commands[0])
        for (i in 1 until commands.size) {
            query.addAnsi(commands[i])
        }
        executeAndForget(query)
    }

    /**
     * Форматирование записи с указанным MFN.
     *
     * @param format Текст формата.
     * @param mfn    MFN записи.
     * @return Результат расформатирования.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun formatRecord(format: String, mfn: Int): String? {
        val query = ClientQuery(this, FORMAT_RECORD)
        query.addAnsi(database)
        query.addAnsi(prepareFormat(format))
        query.add(1)
        query.add(mfn)
        execute(query).use { response ->
            response.checkReturnCode()
            return response.readRemainingUtfText()
        }
    }

    /**
     * Форматирование виртуальной записи.
     *
     * @param format Текст формата.
     * @param record Запись.
     * @return Результат расформатирования.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun formatRecord(format: String, record: Record): String? {
        val query = ClientQuery(this, FORMAT_RECORD)
        query.addAnsi(database)
        query.addAnsi(prepareFormat(format))
        query.add(-2)
        query.addUtf(record.toString())
        execute(query).use { response ->
            response.checkReturnCode()
            return response.readRemainingUtfText()
        }
    }

    @Throws(IOException::class, IrbisException::class)
    fun formatRecords(format: String, mfns: IntArray): Array<String>? {
        if (mfns.size == 0) {
            return arrayOf()
        }
        val query = ClientQuery(this, FORMAT_RECORD)
        query.addAnsi(database)
        query.addAnsi(prepareFormat(format))
        query.add(mfns.size)
        for (i in mfns.indices) {
            query.add(mfns[i])
        }
        execute(query).use { response ->
            response.checkReturnCode()
            return response.readRemainingUtfLines()
        }
    }

    /**
     * Получение информации о базе данных.
     *
     * @param databaseName Имя базы данных.
     * @return Информация о базе.
     */
    @Throws(IOException::class, IrbisException::class)
    fun getDatabaseInfo(databaseName: String): DatabaseInfo? {
        val query = ClientQuery(this, RECORD_LIST)
        query.addAnsi(databaseName)
        execute(query).use { response ->
            response.checkReturnCode()
            TODO()
            // return DatabaseInfo.parse(response)
        }
    }

    /**
     * Получение максимального MFN для указанной базы данных.
     *
     * @param databaseName База данных.
     * @return MFN, который будет присвоен следующей записи.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun getMaxMfn(databaseName: String): Int {
        val query = ClientQuery(this, GET_MAX_MFN)
        query.addAnsiNoLF(databaseName)
        execute(query).use { response ->
            response.checkReturnCode()
            return response.returnCode
        }
    } // getMaxMfn


//    /**
//     * Получение постингов для указанных записи и префикса.
//     *
//     * @param mfn MFN записи.
//     * @param prefix Префикс в виде "A=$".
//     * @return Массив постингов.
//     * @throws IOException Ошибка ввода-вывода.
//     * @throws IrbisException Ошибка протокола.
//     */
//    @Throws(IOException::class, IrbisException::class)
//    fun getRecordPostings(mfn: Int, prefix: String?): Array<TermPosting?>? {
//        val query = ClientQuery(this, GET_RECORD_POSTINGS)
//        query.addAnsi(database)
//        query.add(mfn)
//        query.addUtf(prefix)
//        execute(query).use { response ->
//            response.checkReturnCode()
//            return TermPosting.parse(response)
//        }
//    } // getRecordPostings


//    /**
//     * Получение статистики с сервера.
//     *
//     * @return Полученная статистика.
//     * @throws IOException    Ошибка ввода-вывода.
//     * @throws IrbisException Ошибка протокола.
//     */
//    @Throws(IOException::class, IrbisException::class)
//    fun getServerStat(): ServerStat? {
//        val query = ClientQuery(this, GET_SERVER_STAT)
//        execute(query).use { response ->
//            response.checkReturnCode()
//            return ServerStat.parse(response)
//        }
//    }

//    /**
//     * Получение версии сервера.
//     *
//     * @return Версия сервера.
//     * @throws IOException    Ошибка ввода-вываода.
//     * @throws IrbisException Ошибка протокола.
//     */
//    @Throws(IOException::class, IrbisException::class)
//    fun getServerVersion(): IrbisVersion? {
//        val query = ClientQuery(this, SERVER_INFO)
//        execute(query).use { response ->
//            response.checkReturnCode()
//            return IrbisVersion.parse(response)
//        }
//    }

//    /**
//     * Получение списка пользователей с сервера.
//     *
//     * @return Массив пользователей.
//     * @throws IOException    Ошибка ввода-вывода.
//     * @throws IrbisException Ошибка протокола.
//     */
//    @Throws(IOException::class, IrbisException::class)
//    fun getUserList(): Array<UserInfo?>? {
//        val query = ClientQuery(this, GET_USER_LIST)
//        execute(query).use { response ->
//            response.checkReturnCode()
//            return UserInfo.parse(response)
//        }
//    }

    /**
     * Подключен ли клиент в данный момент?
     *
     * @return true, если подключен.
     */
    @Contract(pure = true)
    fun isConnected(): Boolean {
        return _isConnected
    }

//    /**
//     * Получение списка баз данных с сервера.
//     *
//     * @param iniFile Серверный INI-файл (полученный при подключении к серверу)
//     * @param defaultFileName Имя файла со списком баз по умолчанию
//     * @return Массив баз данных
//     * @throws IOException Ошибка ввода-вывода
//     */
//    @Throws(IOException::class)
//    fun listDatabases(iniFile: IniFile, defaultFileName: String): Array<DatabaseInfo?>? {
//        val fileName: String = iniFile.getValue("Main", "DBNNAMECAT", defaultFileName)
//            ?: return arrayOfNulls(0)
//        val specification = FileSpecification(IrbisPath.DATA, null, fileName)
//        val menuFile: MenuFile = readMenuFile(specification)
//        return DatabaseInfo.parse(menuFile)
//    }

//    /**
//     * Загрузка списка баз данных с сервера.
//     *
//     * @param specification Спецификация
//     * @return Список баз данных
//     * @throws IOException Ошибка ввода-вывода
//     */
//    @Throws(IOException::class)
//    fun listDatabases(specification: FileSpecification): Array<DatabaseInfo?>? {
//        val menuFile: MenuFile = readMenuFile(specification)
//        return DatabaseInfo.parse(menuFile)
//    }

    /**
     * Получение списка файлов с сервера.
     *
     * @param specification Спецификация файлов (поддерживаются метасимволы).
     * @return Перечень файлов.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun listFiles(specification: FileSpecification): Array<String>? {
        val query = ClientQuery(this, LIST_FILES)
        query.addAnsi(specification.toString())
        val result = ArrayList<String>()
        execute(query).use { response ->
            val lines = response.readRemainingAnsiLines()
            for (line in lines) {
                TODO()
                //val converted: Array<String> = IrbisText.fromFullDelimiter(line)
                //Collections.addAll(result, *converted)
            }
        }
        return result.toTypedArray<String>()
    }

    /**
     * Получение списка файлов с сервера.
     *
     * @param specifications Спецификации файлов (поддерживаются метасимволы).
     * @return Перечень файлов.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun listFiles(specifications: Array<FileSpecification>): Array<String?>? {
        if (specifications.size == 0) {
            return arrayOfNulls(0)
        }
        val query = ClientQuery(this, LIST_FILES)
        for (specification in specifications) {
            query.addAnsi(specification.toString())
        }
        val result = ArrayList<String?>()
        execute(query).use { response ->
            val lines = response.readRemainingAnsiLines()
            for (line in lines) {
                TODO()
//                val converted: Array<String> = IrbisText.fromFullDelimiter(line)
//                result.addAll(Arrays.asList(*converted))
            }
        }
        return result.toTypedArray<String?>()
    }

//    /**
//     * Получение списка серверных процессов.
//     *
//     * @return Массив процессов.
//     * @throws IOException    Ошибка ввода-вывода.
//     * @throws IrbisException Ошибка протокола.
//     */
//    @Throws(IOException::class, IrbisException::class)
//    fun listProcesses(): Array<IrbisProcessInfo?> {
//        val query = ClientQuery(this, GET_PROCESS_LIST)
//        execute(query).use { response ->
//            response.checkReturnCode()
//            return IrbisProcessInfo.parse(response)
//        }
//    }

//    /**
//     * Мониторинг (ждем окончания указанной операции).
//     *
//     * @param operation Какую операцию ждем
//     * @return Серверный лог-файл (результат выполнения операции)
//     */
//    @Throws(IOException::class, IrbisException::class, InterruptedException::class)
//    fun monitorOperation(operation: String): String? {
//        val clientId = Integer.toString(clientId)
//        while (true) {
//            var found = false
//            val processes: Array<IrbisProcessInfo?> = listProcesses()
//            for (process in processes) {
//                if (clientId.compareTo(process.clientId) == 0
//                    && operation.compareTo(process.lastCommand) == 0
//                ) {
//                    found = true
//                }
//            }
//            if (!found) {
//                break
//            }
//            Thread.sleep(1000)
//        }
//        val filename = "$clientId.ibf"
//        val specification = FileSpecification(IrbisPath.SYSTEM, null, filename)
//        return readTextContent(specification)
//    }

    /**
     * Пустая операция (используется для периодического подтверждения подключения клиента).
     *
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun noOp() {
        executeAnsi(NOP)
    }

    /**
     * Разбор строки подключения.
     *
     * @param connectionString Строка подключения.
     * @throws IrbisException Ошибка при разборе строки.
     */
    @Throws(IrbisException::class)
    fun parseConnectionString(connectionString: String) {
        val items = connectionString.split(";".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()
        for (item in items) {
            val parts = item.split("=".toRegex(), limit = 2).toTypedArray()
            if (parts.size != 2) {
                throw IrbisException()
            }

            val name = parts[0].trim { it <= ' ' }.lowercase(Locale.getDefault())
            val value = parts[1].trim { it <= ' ' }
            if (isNullOrEmpty(name) || isNullOrEmpty(value)) {
                throw IrbisException()
            }

            when (name) {
                "host", "server", "address" -> host = value
                "port" -> port = value.toInt()
                "user", "username", "name", "login" -> username = value
                "pwd", "password" -> password = value
                "db", "catalog", "database" -> database = value
                "arm", "workstation" -> workstation = value[0]
                "data" -> userData = value
                "debug" -> {}
                else -> throw IrbisException()
            }
        }
    }

    /**
     * Восстановление подключения к прошлой базе данных,
     * запомненной с помощью pushDatabase.
     *
     * @return Прошлая база данных.
     */
    fun popDatabase(): String {
        val result = database
        database = _databaseStack.pop()
        return result
    }

//    /**
//     * Расформатирование таблицы.
//     *
//     * @param definition Определение таблицы.
//     * @return Результат расформатирования.
//     * @throws IOException Ошибка ввода-вывода.
//     */
//    @Throws(IOException::class)
//    fun printTable(definition: TableDefinition): String? {
//        val query = ClientQuery(this, PRINT)
//        query.addAnsi(definition.database)
//        query.addAnsi(definition.table)
//        query.addAnsi("") // instead of headers
//        query.addAnsi(definition.mode)
//        query.addUtf(definition.searchQuery)
//        query.add(definition.minMfn)
//        query.add(definition.maxMfn)
//        query.addUtf(definition.sequentialQuery)
//        query.addAnsi("") // instead of MFN list
//        execute(query).use { response -> return response.readRemainingUtfText() }
//    }

    /**
     * Установка подключения к новой базе данных с запоминанием
     * предыдущуей базы.
     *
     * @param newDatabase Новая база данных.
     * @return Предыдущая база данных.
     */
    fun pushDatabase(newDatabase: String): String {
        val result = database
        _databaseStack.push(database)
        database = newDatabase
        return result
    }

    /**
     * Чтение двоичного файла с сервера.
     *
     * @param specification Спецификация файла.
     * @return Содержимое файла или null.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun readBinaryContent(specification: FileSpecification): ByteArray? {
        var stream: InputStream? = null
        val result: ByteArray? = null
        try {
            stream = readBinaryFile(specification)
            if (stream == null) {
                return null
            }
            TODO()
            // result = Utility.readToEnd(stream)
        } finally {
            stream?.close()
        }
        return result
    }

    /**
     * Чтение двоичного файла с сервера.
     *
     * @param specification Спецификация файла.
     * @return Поток или null, если файл не найден.
     */
    @Throws(IOException::class)
    fun readBinaryFile(specification: FileSpecification): InputStream? {
        specification.isBinaryFile = true
        val query = ClientQuery(this, READ_DOCUMENT)
        query.addAnsi(specification.toString())
        val response = execute(query)
        return response.getBinaryFile()
    }

    /**
     * Чтение INI-файла с сервера.
     *
     * @param specification Спецификация
     * @return INI-файл
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun readIniFile(specification: FileSpecification): IniFile {
        val text = readTextContent(specification)
        val reader = StringReader(text)
        val scanner = Scanner(reader)
        TODO()
        // return IniFile.parse(scanner)
    }

//    /**
//     * Чтение MNU-файла с сервера.
//     *
//     * @param specification Спецификация
//     * @return MNU-файл
//     * @throws IOException Ошибка ввода-вывода
//     */
//    @Throws(IOException::class)
//    fun readMenuFile(specification: FileSpecification): MenuFile {
//        val text = readTextContent(specification)
//        val reader = StringReader(text)
//        val scanner = Scanner(reader)
//        return MenuFile.parse(scanner)
//    }

//    /**
//     * Считывание постингов из поискового индекса.
//     *
//     * @param parameters Параметры постингов.
//     * @return Массив постингов.
//     * @throws IOException    Ошибка ввода-вывода.
//     * @throws IrbisException Ошибка протокола.
//     */
//    @Throws(IOException::class, IrbisException::class)
//    fun readPostings(parameters: PostingParameters): Array<TermPosting?>? {
//        val databaseName: String = iif(parameters.database, database)
//        val query = ClientQuery(this, READ_POSTINGS)
//        query.addAnsi(databaseName)
//        query.add(parameters.numberOfPostings)
//        query.add(parameters.firstPosting)
//        query.addAnsi(parameters.format)
//        if (parameters.listOftTerms == null) {
//            query.addUtf(parameters.term)
//        } else {
//            for (term in parameters.listOftTerms) {
//                query.addUtf(term)
//            }
//        }
//        execute(query).use { response ->
//            response.checkReturnCode(Utility.READ_TERMS_CODES)
//            return TermPosting.parse(response)
//        }
//    }

//    @Throws(IOException::class, IrbisException::class)
//    fun readRawRecord(databaseName: String, mfn: Int): RawRecord? {
//        val query = ClientQuery(this, READ_RECORD)
//        query.addAnsi(databaseName)
//        query.add(mfn)
//        execute(query).use { response ->
//            response.checkReturnCode(Utility.READ_RECORD_CODES)
//            val lines = response.readRemainingUtfLines()
//            val result = RawRecord()
//            result.parseSingle(lines)
//            return result
//        }
//    }

    /**
     * Чтение записи с указанным MFN.
     *
     * @param mfn MFN записи
     * @return Загруженная запись
     * @throws IOException    Ошибка ввода-вывода
     * @throws IrbisException Ошибка протокола
     */
    @Throws(IOException::class, IrbisException::class)
    fun readRecord(mfn: Int): Record {
        return readRecord(database, mfn)
    }

    /**
     * Чтение записи с указанным MFN.
     *
     * @param databaseName База данных
     * @param mfn          MFN записи
     * @return  Загруженная запись
     * @throws IOException    Ошибка ввода-вывода
     * @throws IrbisException Ошибка протокола
     */
    @Throws(IOException::class, IrbisException::class)
    fun readRecord(databaseName: String, mfn: Int): Record {
        val query = ClientQuery(this, READ_RECORD)
        query.addAnsi(databaseName)
        query.add(mfn)
        execute(query).use { response ->
            response.checkReturnCode(READ_RECORD_CODES)
            val lines = response.readRemainingUtfLines()
            val result = Record()
            result.parseSingle(lines)
            return result
        }
    }

    /**
     * Чтение указанной версии записи.
     *
     * @param databaseName  База данных
     * @param mfn           MFN записи
     * @param versionNumber Номер версии
     * @return Загруженная запись
     * @throws IOException    Ошибка ввода-вывода
     * @throws IrbisException Ошибка протокола
     */
    @Throws(IOException::class, IrbisException::class)
    fun readRecord(
        databaseName: String, mfn: Int, versionNumber: Int
    ): Record? {
        val query = ClientQuery(this, READ_RECORD)
        query.addAnsi(databaseName)
        query.add(mfn)
        query.add(versionNumber)
        execute(query).use { response ->
            TODO()
//            response.checkReturnCode(Utility.READ_RECORD_CODES)
//            val lines = response.readRemainingUtfLines()
//            val result = MarcRecord()
//            result.parseSingle(lines)
//
//            // unlock the record
//            unlockRecords(databaseName, mfn)
//            return result
        }
    }

//    /**
//     * Загрузка сценариев поиска с сервера.
//     *
//     * @param specification Спецификация
//     * @return Загруженные сценарии
//     * @throws IOException Ошибка ввода-вывода
//     */
//    @Throws(IOException::class)
//    fun readSearchScenario(specification: FileSpecification): Array<SearchScenario?>? {
//        val iniFile: IniFile = readIniFile(specification)
//        return SearchScenario.parse(iniFile)
//    }

//    /**
//     * Получение термов поискового словаря.
//     *
//     * @param parameters Параметры термов.
//     * @return Массив термов.
//     * @throws IOException    Ошибка ввода-вывода.
//     * @throws IrbisException Ошибка протокола.
//     */
//    @Throws(IOException::class, IrbisException::class)
//    fun readTerms(parameters: TermParameters): Array<TermInfo?>? {
//        val databaseName: String = iif(parameters.database, database)
//        val command: String = if (parameters.reverseOrder) READ_TERMS_REVERSE else READ_TERMS
//        val query = ClientQuery(this, command)
//        query.addAnsi(databaseName)
//        query.addUtf(parameters.startTerm)
//        query.add(parameters.numberOfTerms)
//        query.addAnsi(parameters.format)
//        execute(query).use { response ->
//            response.checkReturnCode(Utility.READ_TERMS_CODES)
//            return TermInfo.parse(response)
//        }
//    }

    /**
     * Получение текстового файла с сервера.
     *
     * @param specification Спецификация файла.
     * @return Текст файла (пустая строка, если файл не найден).
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun readTextContent(specification: FileSpecification): String {
        val query = ClientQuery(this, READ_DOCUMENT)
        query.addAnsi(specification.toString())
        execute(query).use { response ->
            var result = response.readAnsi()
            result = fromIrbisToDos(result) ?: ""
            return result
        }
    }

    /**
     * Получение текстового файла с сервера в виде потока.
     * Закрыть поток должен будет вызывающий код.
     * Сервер выдаёт текстовые файлы с заменой
     * стандартного перевода строки на свой собственный.
     * Преобразовывать переводы строки должен будет
     * вызывающий код.
     *
     * @param specification Спецификация файла.
     * @return Текст файла (пустая строка, если файл не найден).
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun readTextFile(specification: FileSpecification): InputStream {
        val query = ClientQuery(this, READ_DOCUMENT)
        query.addAnsi(specification.toString())
        val response = execute(query)
        return response.getStream()
    }

    /**
     * Получение текстовых файлов с сервера.
     *
     * @param specifications Спецификации файлов.
     * @return Тексты файлов.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun readTextFiles(specifications: Array<FileSpecification>): Array<String>? {
        val query = ClientQuery(this, READ_DOCUMENT)
        for (specification in specifications) {
            query.addAnsi(specification.toString())
        }
        execute(query).use { response ->
            val result = ArrayList<String>()
            while (true) {
                // TODO FIX ME!
                var text = response.readAnsi()
                if (isNullOrEmpty(text)) {
                    break
                }
                text = fromIrbisToDos(text) ?: ""
                result.add(text)
            }
            return result.toTypedArray<String>()
        }
    }

    /**
     * Пересоздание словаря.
     *
     * @param databaseName База данных.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun reloadDictionary(databaseName: String) {
        executeAnsi(RELOAD_DICTIONARY, databaseName)
    }

    /**
     * Пересоздание мастер-файла.
     *
     * @param databaseName База данных.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun reloadMasterFile(databaseName: String) {
        executeAnsi(RELOAD_MASTER_FILE, databaseName)
    }

    /**
     * Чтение двоичного файла с сервера.
     *
     * @param specification Спецификация файла.
     * @return Содержимое файла или null.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class, IrbisFileNotFoundException::class)
    fun requireBinaryFile(specification: FileSpecification): ByteArray? {
        val result = readBinaryContent(specification)
        if (result == null || result.size == 0) {
            throw IrbisFileNotFoundException(specification)
        }
        return result
    }

    /**
     * Чтение INI-файла с сервера.
     *
     * @param specification Спецификация
     * @return INI-файл
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class, IrbisFileNotFoundException::class)
    fun requireIniFile(specification: FileSpecification): IniFile? {
        val text = readTextContent(specification)
        if (isNullOrEmpty(text)) {
            throw IrbisFileNotFoundException(specification)
        }
        TODO()
        // val reader = StringReader(text)
        // val scanner = Scanner(reader)
        // return IniFile.parse(scanner)
    }

    /**
     * Чтение MNU-файла с сервера.
     *
     * @param specification Спецификация
     * @return MNU-файл
     * @throws IOException Ошибка ввода-вывода
     */
    @Throws(IOException::class, IrbisFileNotFoundException::class)
    fun requireMenuFile(specification: FileSpecification): MenuFile? {
        val text = readTextContent(specification)
        if (isNullOrEmpty(text)) {
            throw IrbisFileNotFoundException(specification)
        }
        TODO()
        // val reader = StringReader(text)
        // val scanner = Scanner(reader)
        // return MenuFile.parse(scanner)
    }

    /**
     * Получение текстового файла с сервера.
     *
     * @param specification Спецификация файла.
     * @return Текст файла (пустая строка, если файл не найден).
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class, IrbisFileNotFoundException::class)
    fun requireTextFile(specification: FileSpecification): String? {
        val result = readTextContent(specification)
        if (isNullOrEmpty(result)) {
            throw IrbisFileNotFoundException(specification)
        }
        return result
    }

    /**
     * Перезапуск сервера (без утери подключенных клиентов).
     *
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun restartServer() {
        executeAnsi(RESTART_SERVER)
    }

    /**
     * Поиск записей.
     *
     * @param expression Поисковое выражение.
     * @return Найденные MFN.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Suppress("UNUSED_PARAMETER")
    @Throws(IOException::class, IrbisException::class)
    fun search(expression: String): IntArray? {
        TODO()
//        val parameters = SearchParameters()
//        parameters.database = database
//        parameters.searchExpression = expression
//        parameters.numberOfRecords = 0
//        parameters.firstRecord = 1
//        return search(parameters)
    }

//    /**
//     * Поиск записей.
//     *
//     * @param parameters Параметры поиска.
//     * @return Найденные MFN.
//     * @throws IOException    Ошибка ввода-вывода.
//     * @throws IrbisException Ошибка протокола.
//     */
//    @Throws(IOException::class, IrbisException::class)
//    fun search(parameters: SearchParameters): IntArray? {
//        val databaseName: String = iif(parameters.database, database)
//        val query = ClientQuery(this, SEARCH)
//        query.addAnsi(databaseName)
//        query.addUtf(parameters.searchExpression)
//        query.add(parameters.numberOfRecords)
//        query.add(parameters.firstRecord)
//        query.addAnsi(parameters.formatSpecification)
//        query.add(parameters.minMfn)
//        query.add(parameters.maxMfn)
//        query.addAnsi(parameters.sequentialSpecification)
//        var expected: Int
//        var batchSize: Int
//        var result: IntArray
//        execute(query).use { response ->
//            response.checkReturnCode()
//            expected = response.readInt32()
//            batchSize = Math.min(expected, Utility.MAX_PACKET)
//            result = IntArray(batchSize)
//            for (i in 0 until batchSize) {
//                val line = response.readAnsi()
//                val parts = line.split("#".toRegex(), limit = 2).toTypedArray()
//                val mfn: Int = FastNumber.parseInt32(parts[0])
//                result[i] = mfn
//            }
//        }
//        if (parameters.firstRecord !== 0 && !parameters.nestedCall && expected > batchSize) {
//            val accumulator = ArrayList<Int>(result.size)
//            for (i in result.indices) {
//                accumulator.add(result[i])
//            }
//            var lowerBound = accumulator.size
//            while (lowerBound < expected) {
//                val nestedParameters: SearchParameters = parameters.clone()
//                nestedParameters.nestedCall = true
//                nestedParameters.firstRecord = parameters.firstRecord + lowerBound
//                result = search(nestedParameters)
//                for (i in result.indices) {
//                    accumulator.add(result[i])
//                }
//                lowerBound = accumulator.size
//            }
//            result = IntArray(accumulator.size)
//            for (i in accumulator.indices) {
//                result[i] = accumulator[i]
//            }
//        }
//        return result
//    }

    /**
     * Выдача строки подключения для текущего соединения.
     *
     * @return Строка подключения.
     */
    @Contract(pure = true)
    fun toConnectionString(): String {
        return ("host=" + host + ";port=" + port + ";username=" + username + ";password=" + password + ";database" + database + ";arm=" + workstation + ";")
    }

    /**
     * Опустошение базы данных.
     *
     * @param databaseName База данных.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun truncateDatabase(databaseName: String) {
        executeAnsi(EMPTY_DATABASE, databaseName)
    }

    /**
     * Разблокирование базы данных.
     *
     * @param databaseName База данных.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun unlockDatabase(databaseName: String) {
        executeAnsi(UNLOCK_DATABASE, databaseName)
    }

    /**
     * Разблокирование записей.
     *
     * @param databaseName База данных.
     * @param mfnList      Список MFN.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun unlockRecords(
        databaseName: String, vararg mfnList: Int
    ) {
        if (mfnList.size == 0) {
            return
        }
        val query = ClientQuery(this, UNLOCK_RECORDS)
        query.addAnsi(databaseName)
        for (mfn in mfnList) {
            query.add(mfn)
        }
        executeAndForget(query)
    }

    /**
     * Обновление строк серверного INI-файла.
     *
     * @param lines Измененные строки.
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun updateIniFile(lines: Array<String?>) {
        if (lines.size == 0) {
            return
        }
        val query = ClientQuery(this, UPDATE_INI_FILE)
        for (line in lines) {
            query.addAnsi(line)
        }
        executeAndForget(query)
    }

//    /**
//     * Обновление списка пользователей на сервере.
//     *
//     * @param userList Список пользователей.
//     * @throws IOException Ошибка ввода-вывода.
//     */
//    @Throws(IOException::class)
//    fun updateUserList(userList: Array<UserInfo?>) {
//        val query = ClientQuery(this, SET_USER_LIST)
//        for (user in userList) {
//            val line: String = user.encode()
//            query.addAnsi(line)
//        }
//        executeAndForget(query)
//    }

    /**
     * Сохранение записи на сервере.
     *
     * @param record Запись для сохранения (новая или ранее считанная)
     * @return Новый максимальный MFN
     * @throws IOException Ошибка ввода-вывода
     * @throws IrbisException Ошибка протокола
     */
    @Throws(IOException::class, IrbisException::class)
    fun writeRecord(record: Record): Int {
        return writeRecord(record, false, true, false)
    }

    /**
     * Сохранение записи на сервере.
     *
     * @param record            Запись для сохранения (новая или ранее считанная).
     * @param lockFlag          Оставить запись заблокированной?
     * @param actualize         Актуализировать запись?
     * @param dontParseResponse Не разбирать ответ сервера?
     * @return Новый максимальный MFN.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun writeRecord(
        record: Record, lockFlag: Boolean, actualize: Boolean, dontParseResponse: Boolean
    ): Int {
        val databaseName: String = iif(record.database, database) ?: throw IllegalArgumentException()
        val query = ClientQuery(this, UPDATE_RECORD)
        query.addAnsi(databaseName)
        query.add(lockFlag)
        query.add(actualize)
        query.addUtf(record.toString())
        execute(query).use { response ->
            response.checkReturnCode()
            if (!dontParseResponse) {
                record.fields.clear()
                var lines = response.readRemainingUtfLines()
                if (lines.size > 1) {
                    val temp = ArrayList<String>()
                    temp.add(lines[0])
                    TODO()
                    // temp.addAll(Arrays.asList(fromShortDelimiter(lines[1])))
                    // lines = temp.toTypedArray<String>()
                    // record.parseSingle(lines)
                }
            }
            return response.returnCode
        }
    }

    /**
     * Сохранение записей на сервере.
     *
     * @param records           Записи для сохранения (новая или ранее считанная).
     * @param lockFlag          Оставить записи заблокированными?
     * @param actualize         Актуализировать записи?
     * @param dontParseResponse Не разбирать ответ сервера?
     * @return Новый максимальный MFN.
     * @throws IOException    Ошибка ввода-вывода.
     * @throws IrbisException Ошибка протокола.
     */
    @Throws(IOException::class, IrbisException::class)
    fun writeRecords(
        records: Array<Record>, lockFlag: Boolean, actualize: Boolean, dontParseResponse: Boolean
    ): Int {
        if (records.size == 0) {
            return getMaxMfn(database)
        }
        if (records.size == 1) {
            return writeRecord(records[0], lockFlag, actualize, dontParseResponse)
        }
        val query = ClientQuery(this, SAVE_RECORD_GROUP)
        query.add(lockFlag)
        query.add(actualize)
        for (record in records) {
            val line: String = (iif(record.database, database) + IRBIS_DELIMITER + record.toString())
            query.addUtf(line)
        }
        execute(query).use { response ->
            response.obtainReturnCode()
            if (!dontParseResponse) {
                val lines = response.readRemainingUtfLines()
                for (i in records.indices) {
                    val record: Record = records[i]
                    TODO()
                    // val splitted: Array<String> = fromShortDelimiter(lines[i])
                    // record.parseSingle(splitted)
                }
            }
            return response.returnCode
        }
    }

    /**
     * Сохранить текстовый файл на сервере.
     *
     * @param specification Спецификация (включая текст для сохранения).
     * @throws IOException Ошибка ввода-вывода.
     */
    @Throws(IOException::class)
    fun writeTextFile(specification: FileSpecification) {
        val query = ClientQuery(this, READ_DOCUMENT)
        query.addAnsi(specification.toString())
        execute(query).use { response -> response.obtainReturnCode() }
    }

}
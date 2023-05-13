package arsmagna

/**
 * Исключение, связанное с ИРБИС.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class IrbisException: Exception {
    val errorCode: Int

    constructor() {
        errorCode = 0
    }

    constructor(errorCode: Int) : super() {
        this.errorCode = errorCode
    }

    constructor(message: String): super(message) {
        errorCode = 0
    }

    constructor(cause: Throwable) : super(cause) {
        errorCode = 0
    }
}

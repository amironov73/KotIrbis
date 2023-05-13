package arsmagna

/**
 * Файл не найден на сервере ИРБИС64.
 */
@Suppress("unused")
class IrbisFileNotFoundException(var fileName: String): IrbisException() {
    constructor(specification: FileSpecification): this (specification.toString())
}

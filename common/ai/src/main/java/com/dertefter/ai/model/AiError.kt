package com.dertefter.ai.model

sealed class AiError(
    message: String?,
    cause: Throwable? = null,
) : Exception(message, cause) {

    data object NotConfigured : AiError("Конфигурация модели не задана")

    class ModelNotFound(path: String) : AiError("Файл модели не найден: $path")

    class InvalidModel(reason: String?) :
        AiError("Файл не является моделью LiteRT-LM: ${reason.orEmpty()}")

    class ImportFailed(cause: Throwable?) :
        AiError("Не удалось импортировать модель: ${cause?.message}", cause)

    class DeleteFailed(cause: Throwable?) :
        AiError("Не удалось удалить модель: ${cause?.message}", cause)

    data object EmptyInput : AiError("Пустой текст: нечего переписывать")

    class EngineInitializationFailed(cause: Throwable?) :
        AiError("Не удалось инициализировать движок: ${cause?.message}", cause)

    class GenerationFailed(cause: Throwable?) :
        AiError("Ошибка генерации ответа: ${cause?.message}", cause)

    data object EmptyResponse : AiError("Модель вернула пустой ответ")

    class Unexpected(cause: Throwable?) :
        AiError("Непредвиденная ошибка: ${cause?.message}", cause)
}

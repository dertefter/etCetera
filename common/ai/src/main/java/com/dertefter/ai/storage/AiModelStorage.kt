package com.dertefter.ai.storage

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.dertefter.ai.model.AiError
import com.dertefter.ai.model.AiModel
import com.google.ai.edge.litertlm.ModelInfo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@SingleIn(AppScope::class)
@Inject
class AiModelStorage(
    private val context: Context,
) {

    private val modelsDir: File
        get() = File(context.noBackupFilesDir, MODELS_DIR).apply { mkdirs() }

    fun list(): List<AiModel> =
        modelsDir.listFiles()
            .orEmpty()
            .filter { file -> file.isFile && file.extension.equals(MODEL_EXTENSION, ignoreCase = true) }
            .map { file -> file.toAiModel() }
            .sortedByDescending { model -> model.importedAt }

    suspend fun import(sourcePath: String, name: String? = null): AiModel =
        withContext(Dispatchers.IO) {
            val source = File(sourcePath)
            if (!source.isFile || !source.canRead()) throw AiError.ModelNotFound(sourcePath)
            if (source.length() == 0L) throw AiError.InvalidModel("файл пустой")
            validateLlm(source)
            val target = uniqueFile(name ?: source.nameWithoutExtension)
            try {
                source.copyTo(target, overwrite = false)
            } catch (error: IOException) {
                // Обрывок копии не должен попасть в список моделей.
                target.delete()
                throw AiError.ImportFailed(error)
            }
            target.toAiModel()
        }

    suspend fun import(uri: Uri, name: String? = null): AiModel =
        withContext(Dispatchers.IO) {
            val documentName = displayName(uri)?.substringBeforeLast('.')
            val target = uniqueFile(name ?: documentName ?: DEFAULT_MODEL_NAME)
            try {
                copyDocument(uri, target)
                if (target.length() == 0L) throw AiError.InvalidModel("файл пустой")
                validateLlm(target)
                target.toAiModel()
            } catch (error: Throwable) {
                // Обрывок копии или невалидный файл не должен попасть в список моделей.
                target.delete()
                if (error is CancellationException || error is AiError) throw error
                throw AiError.ImportFailed(error)
            }
        }

    suspend fun delete(model: AiModel) = withContext(Dispatchers.IO) {
        val file = File(model.path)
        if (file.exists() && !file.delete()) {
            throw AiError.DeleteFailed(IOException(model.name))
        }
    }

    private fun validateLlm(file: File) {
        val info = try {
            ModelInfo.from(file.absolutePath)
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            throw AiError.InvalidModel(error.message)
        }
        try {
            if (info !is ModelInfo.Llm) throw AiError.InvalidModel("это не языковая модель")
        } finally {
            runCatching { info.close() }
        }
    }

    private fun copyDocument(uri: Uri, target: File) {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Не удалось открыть документ: $uri")
        input.use { source ->
            target.outputStream().use { destination -> source.copyTo(destination) }
        }
    }

    private fun displayName(uri: Uri): String? = runCatching {
        context.contentResolver
            .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
    }.getOrNull()


    private fun uniqueFile(rawName: String): File {
        val base = rawName.toSafeFileName().ifEmpty { DEFAULT_MODEL_NAME }
        val dir = modelsDir
        var candidate = File(dir, "$base.$MODEL_EXTENSION")
        var index = 1
        while (candidate.exists()) {
            index++
            candidate = File(dir, "$base ($index).$MODEL_EXTENSION")
        }
        return candidate
    }

    private fun String.toSafeFileName(): String =
        replace(UNSAFE_FILE_NAME_CHARS, "_").trim('_', '.')

    private fun File.toAiModel(): AiModel = AiModel(
        id = name,
        name = nameWithoutExtension,
        path = absolutePath,
        sizeBytes = length(),
        importedAt = lastModified(),
    )

    private companion object {
        const val MODELS_DIR = "ai_models"
        const val MODEL_EXTENSION = "litertlm"
        const val DEFAULT_MODEL_NAME = "model"

        val UNSAFE_FILE_NAME_CHARS = Regex("[\\\\/:*?\"<>|\\s]+")
    }
}

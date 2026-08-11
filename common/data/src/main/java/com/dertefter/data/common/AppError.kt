package com.dertefter.data.common

import com.dertefter.data.dto.common.ErrorResponseDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.io.IOException

private val errorJson = Json { ignoreUnknownKeys = true }

sealed interface AppError {
    val message: String?

    data class Network(override val message: String?) : AppError
    data class ApiError(val code: String?, override val message: String?) : AppError
    data class Unexpected(override val message: String?) : AppError

    // Authentication & Authorization
    data class Unauthorized(override val message: String?) : AppError
    data class SessionNotFound(override val message: String?) : AppError
    data class SessionExpired(override val message: String?) : AppError
    data class SessionRevoked(override val message: String?) : AppError
    data class RefreshTokenMissing(override val message: String?) : AppError
    data class InvalidCredentials(override val message: String?) : AppError
    data class AccountBanned(override val message: String?) : AppError
    data class AccountDeleted(override val message: String?, val canRestore: Boolean = true) : AppError
    data class ProfileRequired(override val message: String?) : AppError
    data class EmailDomainNotAllowed(override val message: String?) : AppError

    // Validation & Constraints
    data class ValidationError(override val message: String?) : AppError
    data class UsernameTaken(override val message: String?) : AppError
    data class InvalidDisplayName(override val message: String?) : AppError
    data class SamePassword(override val message: String?) : AppError
    data class InvalidOldPassword(override val message: String?) : AppError
    data class InvalidPassword(override val message: String?) : AppError
    data class BannedWord(override val message: String?) : AppError

    // Resource Status
    data class NotFound(override val message: String?) : AppError
    data class AlreadyDeleted(override val message: String?) : AppError
    data class Conflict(override val message: String?) : AppError
    data class UserBlocked(override val message: String?) : AppError
    data class NotPinned(override val message: String?) : AppError

    // Permissions & Access
    data class Forbidden(override val message: String?) : AppError
    data class PinNotOwned(override val message: String?) : AppError
    data class RequiresVerification(override val message: String?) : AppError
    data class RequiresSubscription(override val message: String?) : AppError

    // Operations
    data class RateLimit(override val message: String?, val retryAfter: Int = 0) : AppError
    data class TurnstileVerificationFailed(override val message: String?) : AppError
    data class UploadError(override val message: String?) : AppError
    data class ModerationError(override val message: String?) : AppError
    data class EditWindowExpired(override val message: String?) : AppError
    data class NotDeleted(override val message: String?) : AppError
    data class Internal(override val message: String?) : AppError
}

fun Throwable.toAppError(): AppError {
    val errorMessage = this.localizedMessage ?: this.message ?: ""

    val jsonStart = errorMessage.indexOf('{')
    val jsonEnd = errorMessage.lastIndexOf('}')

    if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
        val json = errorMessage.substring(jsonStart, jsonEnd + 1)
        try {
            val errorResponse = errorJson.decodeFromString<ErrorResponseDto>(json)
            val error = errorResponse.error
            val errorCode = error?.code
            val message = error?.message
            if (errorCode != null) {
                return when (errorCode) {
                    "TURNSTILE_VERIFICATION_FAILED" -> AppError.TurnstileVerificationFailed(message)
                    "VALIDATION_ERROR" -> AppError.ValidationError(message)
                    "RATE_LIMIT_EXCEEDED" -> AppError.RateLimit(message, error.retryAfter ?: 0)
                    "NOT_FOUND", "PROFILE_NOT_FOUND" -> AppError.NotFound(message)
                    "SESSION_NOT_FOUND" -> AppError.SessionNotFound(message)
                    "REFRESH_TOKEN_MISSING" -> AppError.RefreshTokenMissing(message)
                    "SESSION_EXPIRED" -> AppError.SessionExpired(message)
                    "UNAUTHORIZED" -> AppError.Unauthorized(message)
                    "SESSION_REVOKED" -> AppError.SessionRevoked(message)
                    "ACCOUNT_INVALID_CREDENTIALS" -> AppError.InvalidCredentials(message)
                    "ACCOUNT_EMAIL_DOMAIN_NOT_ALLOWED" -> AppError.EmailDomainNotAllowed(message)
                    "SAME_PASSWORD" -> AppError.SamePassword(message)
                    "INVALID_OLD_PASSWORD" -> AppError.InvalidOldPassword(message)
                    "INVALID_PASSWORD" -> AppError.InvalidPassword(message)
                    "PIN_NOT_OWNED" -> AppError.PinNotOwned(message)
                    "FORBIDDEN" -> AppError.Forbidden(message)
                    "GIF_REQUIRES_VERIFICATION" -> AppError.RequiresVerification(message)
                    "VIDEO_REQUIRES_NUKSTA" -> AppError.RequiresSubscription(message)
                    "USERNAME_TAKEN" -> AppError.UsernameTaken(message)
                    "INVALID_DISPLAY_NAME" -> AppError.InvalidDisplayName(message)
                    "CONFLICT" -> AppError.Conflict(message)
                    "ALREADY_DELETED" -> AppError.AlreadyDeleted(message)
                    "UPLOAD_ERROR" -> AppError.UploadError(message)
                    "CONTENT_MODERATION_ERROR" -> AppError.ModerationError(message)
                    "EDIT_WINDOW_EXPIRED" -> AppError.EditWindowExpired(message)
                    "NOT_DELETED" -> AppError.NotDeleted(message)
                    "BLOCKED" -> AppError.UserBlocked(message)
                    "NOT_PINNED" -> AppError.NotPinned(message)
                    "INTERNAL_ERROR" -> AppError.Internal(message)
                    "BANNED_WORD" -> AppError.BannedWord(message)
                    "ACCOUNT_BANNED", "ACCOUNT_DEACTIVATED" -> AppError.AccountBanned(message)
                    "PROFILE_REQUIRED" -> AppError.ProfileRequired(message)
                    "ACCOUNT_DELETED" -> AppError.AccountDeleted(message, error.canRestore ?: true)
                    else -> AppError.ApiError(errorCode, message ?: errorMessage)
                }
            }
        } catch (_: Exception) {
        }

        try {
            val jsonObject = errorJson.parseToJsonElement(json).jsonObject
            val errorField = jsonObject["error"]?.jsonPrimitive?.contentOrNull
            val messageField = jsonObject["message"]?.jsonPrimitive?.contentOrNull
            if (errorField != null) {
                return when (errorField) {
                    "invalid signature", "invalid token" -> AppError.Unauthorized("Invalid access token")
                    "token expired" -> AppError.SessionExpired("Token expired")
                    "Unsupported token algorithm" -> AppError.Unauthorized("Access token JWT algorithm unsupported")
                    "Too Many Requests" -> AppError.RateLimit("Rate limit exceeded")
                    else -> AppError.ApiError(null, errorField)
                }
            }
            if (messageField == "Invalid or expired token") {
                return AppError.SessionExpired(messageField)
            }
        } catch (_: Exception) {
        }
    }

    return when (this) {
        is IOException -> AppError.Network(errorMessage)
        else -> AppError.Unexpected(errorMessage)
    }
}

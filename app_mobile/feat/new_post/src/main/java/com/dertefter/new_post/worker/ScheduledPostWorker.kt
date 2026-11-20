package com.dertefter.new_post.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.new_post.NewPollDto
import com.dertefter.data.dto.new_post.NewPollOptionDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.repository.PostRepository
import com.dertefter.new_post.R
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ScheduledPostWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    private val postRepository: PostRepository =
        (applicationContext as PostRepositoryProvider).postRepository

    override suspend fun doWork(): Result {
        val content: String = inputData.getString(KEY_CONTENT) ?: run {
            showNotification(success = false)
            return Result.failure()
        }
        val spansJson: String = inputData.getString(KEY_SPANS) ?: "[]"
        val pollJson: String? = inputData.getString(KEY_POLL)
        val attachmentIdsJson: String = inputData.getString(KEY_ATTACHMENT_IDS) ?: "[]"
        val wallRecipientId: String? = inputData.getString(KEY_WALL_RECIPIENT_ID)
        val repostId: String? = inputData.getString(KEY_REPOST_ID)

        val json = Json { ignoreUnknownKeys = true }

        val spans: List<SpanDto> = try {
            json.parseToJsonElement(spansJson).jsonArray.map { element ->
                val obj = element.jsonObject
                SpanDto(
                    type = obj["type"]?.jsonPrimitive?.content.orEmpty(),
                    length = obj["length"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                    offset = obj["offset"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                    username = obj["username"]?.jsonPrimitive?.content,
                    tag = obj["tag"]?.jsonPrimitive?.content,
                    url = obj["url"]?.jsonPrimitive?.content,
                )
            }
        } catch (_: Exception) {
            emptyList()
        }

        val poll: NewPollDto? = pollJson?.let { pollStr ->
            try {
                val obj = json.parseToJsonElement(pollStr).jsonObject
                NewPollDto(
                    question = obj["question"]?.jsonPrimitive?.content.orEmpty(),
                    options = obj["options"]?.jsonArray
                        ?.map { NewPollOptionDto(it.jsonObject["text"]?.jsonPrimitive?.content.orEmpty()) }
                        .orEmpty(),
                    multipleChoice =
                        obj["multipleChoice"]?.jsonPrimitive?.content?.toBoolean() ?: false,
                )
            } catch (_: Exception) {
                null
            }
        }

        val attachmentIds: List<String> = try {
            json.parseToJsonElement(attachmentIdsJson).jsonArray.map { it.jsonPrimitive.content }
        } catch (_: Exception) {
            emptyList()
        }

        val request = NewPostRequestDto(
            content = content,
            spans = spans,
            poll = poll,
            attachmentIds = attachmentIds,
            wallRecipientId = wallRecipientId?.takeIf { it.isNotEmpty() }
        )

        val result = if (repostId != null) {
            postRepository.repost(repostId, request)
        } else {
            postRepository.newPost(request)
        }

        val publishedPost = result.getOrNull()
        showNotification(
            success = result.isSuccess,
            postId = publishedPost?.id,
            username = publishedPost?.author?.username
        )

        return if (result.isSuccess) Result.success() else Result.failure()
    }

    private fun showNotification(
        success: Boolean,
        postId: String? = null,
        username: String? = null,
    ) {
        val context = applicationContext
        val manager = context.getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_scheduled_posts_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description =
                context.getString(R.string.notification_scheduled_posts_channel_description)
        }
        manager.createNotificationChannel(channel)

        val notificationsAllowed =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                            PackageManager.PERMISSION_GRANTED)
        if (!notificationsAllowed) return

        val intent: Intent? = if (success && !username.isNullOrEmpty() && !postId.isNullOrEmpty()) {
            Intent(
                Intent.ACTION_VIEW,
                "https://итд.com/@$username/post/$postId".toUri()
            ).apply {
                setPackage(context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        } else {
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(
                context.getString(
                    if (success) R.string.notification_post_published_title
                    else R.string.notification_post_failed_title
                )
            )
            .setContentText(
                context.getString(
                    if (success) R.string.notification_post_published_text
                    else R.string.notification_post_failed_text
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (intent != null) {
            builder.setContentIntent(
                PendingIntent.getActivity(
                    context,
                    REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        manager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        const val KEY_CONTENT = "content"
        const val KEY_SPANS = "spans"
        const val KEY_POLL = "poll"
        const val KEY_ATTACHMENT_IDS = "attachment_ids"
        const val KEY_WALL_RECIPIENT_ID = "wall_recipient_id"
        const val KEY_REPOST_ID = "repost_id"

        private const val CHANNEL_ID = "scheduled_posts"
        private const val NOTIFICATION_ID = 42001
        private const val REQUEST_CODE = 42001
    }
}
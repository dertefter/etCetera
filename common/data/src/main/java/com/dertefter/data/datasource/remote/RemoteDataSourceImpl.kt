package com.dertefter.data.datasource.remote

import android.util.Log
import com.dertefter.data.dto.auth.AuthSessionsResponseDto
import com.dertefter.data.dto.auth.SignInRequest
import com.dertefter.data.dto.auth.SignInResponse
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.comments.CommentsDataDto
import com.dertefter.data.dto.comments.NewCommentRequestDto
import com.dertefter.data.dto.comments.RepliesDataDto
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.PostDataDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.dertefter.data.dto.feed.stats.PostStatsRequest
import com.dertefter.data.dto.followers.FollowersResponseDataDto
import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponseDto
import com.dertefter.data.dto.new_post.EditPostRequestDto
import com.dertefter.data.dto.new_post.EditPostResponseDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.dto.notifications.NotificationsResponseDto
import com.dertefter.data.dto.poll.VotePollRequestDto
import com.dertefter.data.dto.search.SearchDataDto
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.search.TopClanDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.dto.user.UserDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : RemoteDataSource {

    
    override suspend fun signIn(signInRequest: SignInRequest): Result<SignInResponse> {
        return runCatching {
            apiService.signIn(signInRequest).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun refreshToken(): Result<Unit> {
        return runCatching {
            apiService.refreshToken().handleUnitResponse().getOrThrow()
        }
    }

    override suspend fun getAuthSessions(): Result<AuthSessionsResponseDto> {
        return runCatching {
            apiService.sessions().handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun deleteAuthSession(sessionId: String): Result<Unit> {
        return runCatching {
            apiService.deleteSession(sessionId).handleUnitResponse().getOrThrow()
        }
    }

    override suspend fun deleteAllAuthSessions(): Result<Unit> {
        return runCatching {
            apiService.deleteAllSessions().handleUnitResponse().getOrThrow()
        }
    }

    override suspend fun getMe(): Result<MeDto> {
        return runCatching {
            apiService.me().handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun unpinPost(postId: String): Result<Unit> {
        return runCatching {
            apiService.unpinPost(postId).handleUnitResponse().getOrThrow()
        }
    }

    override suspend fun pinPost(postId: String): Result<Unit> {
        return runCatching {
            apiService.pinPost(postId).handleUnitResponse().getOrThrow()
        }
    }

    override suspend fun getUser(userId: String): Result<UserDto> {
        return runCatching {
            apiService.user(userId).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun getPosts(tab: String, cursor: String?): Result<PostDataDto> {
        return runCatching {
            Log.e("sss getPosts","tab: $tab , cursor: $cursor")
            apiService.posts(tab = tab, cursor = cursor).handleResponse { it.data }.getOrThrow()
        }
    }

    override suspend fun getPostsForHashtag(
        hashtagId: String,
        cursor: String?
    ): Result<PostDataDto> {
        return runCatching {
            apiService.postsForHashtag(hashtagId = hashtagId, cursor = cursor).handleResponse { it.data }.getOrThrow()
        }
    }

    override suspend fun getPost(postId: String): Result<PostDto> {
        return runCatching {
            apiService.post(postId).handleResponse { it.data }.getOrThrow()
        }
    }

    override suspend fun getPosts(
        userId: String,
        pinnedPostId: String?,
        sort: String,
        cursor: String?
    ): Result<PostDataDto> {
        return runCatching {
            apiService.posts(userId = userId, pinnedPostId = pinnedPostId, sort = sort, cursor = cursor).handleResponse { response ->
                val data = response.data
                data.copy(
                    posts = data.posts.map { post ->
                        if (post.id == pinnedPostId) post.copy(isPinned = true) else post
                    }
                )
            }.getOrThrow()
        }
    }

    override suspend fun getLikedPosts(
        userId: String,
        cursor: String?
    ): Result<PostDataDto> {
        return runCatching {
            apiService.likedPosts(userId = userId, cursor = cursor).handleResponse { it.data }.getOrThrow()
        }
    }

    override suspend fun getComments(
        postId: String,
        cursor: String?,
        sort: String
    ): Result<CommentsDataDto> {
        return runCatching {
            apiService.comments(postId = postId, sort = sort, cursor = cursor).handleResponse { it.data }.getOrThrow()
        }
    }

    override suspend fun getReplies(
        commentId: String,
        page: String?
    ): Result<RepliesDataDto> {
        return runCatching {
            apiService.replies(commentId = commentId, page = page).handleResponse { it.data }.getOrThrow()
        }
    }

    override suspend fun getStats(ids: List<String>): Result<List<PostStatsDto>> {
        return runCatching {
            apiService.stats(PostStatsRequest(ids)).handleResponse { it.postStats }.getOrThrow()
        }
    }

    override suspend fun likePost(postId: String): Result<LikeResponseDto> {
        return runCatching {
            apiService.likePost(postId = postId).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun unlikePost(postId: String): Result<LikeResponseDto> {
        return runCatching {
            apiService.unlikePost(postId = postId).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun likeComment(commentId: String): Result<LikeResponseDto> {
        return runCatching {
            apiService.likeComment(commentId = commentId).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun unlikeComment(commentId: String): Result<LikeResponseDto> {
        return runCatching {
            apiService.unlikeComment(commentId = commentId).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun vote(postId: String, optionIds: List<String>): Result<PollDto> {
        return runCatching {
            apiService.vote(postId, VotePollRequestDto(optionIds)).handleResponse { it.data }.getOrThrow()
        }
    }

    override suspend fun newPost(newPostRequest: NewPostRequestDto): Result<PostDto> {
        return runCatching {
            apiService.newPost(newPostRequest).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun editPost(
        postId: String,
        editPostRequest: EditPostRequestDto
    ): Result<EditPostResponseDto> {
        return runCatching {
            apiService.editPost(postId, editPostRequest).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun repost(postId: String, newPostRequest: NewPostRequestDto): Result<PostDto> {
        return runCatching {
            apiService.repost(postId, newPostRequest).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun newComment(postId: String, newCommentRequest: NewCommentRequestDto): Result<CommentDto> {
        return runCatching {
            apiService.newComment(postId, newCommentRequest).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        return runCatching {
            apiService.deleteComment(commentId).handleUnitResponse().getOrThrow()
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return runCatching {
            apiService.deletePost(postId).handleUnitResponse().getOrThrow()
        }
    }

    override suspend fun newCommentReply(
        commentId: String,
        newCommentRequest: NewCommentRequestDto
    ): Result<CommentDto> {
        return runCatching {
            apiService.newCommentReply(commentId, newCommentRequest).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun uploadMyFile(myFile: File): Result<AttachmentUploadResponseDto> {
        return runCatching {
            val requestBody = myFile.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                myFile.name,
                requestBody
            )
            apiService.upload(multipartBody).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun follow(userId: String): Result<FollowResponseDto> {
        return runCatching {
            apiService.follow(userId).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun unfollow(userId: String): Result<FollowResponseDto> {
        return runCatching {
            apiService.unfollow(userId).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun updateMe(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponseDto> {
        return runCatching {
            apiService.updateMe(updateMeRequestDto).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun getFollowers(
        userId: String,
        page: Int?
    ): Result<FollowersResponseDataDto> {
        return runCatching {
            apiService.followers(userId = userId, page = page).handleResponse { it.data }.getOrThrow()
        }
    }

    override suspend fun getFollowing(
        userId: String,
        page: Int?
    ): Result<FollowersResponseDataDto> {
        return runCatching {
            apiService.following(userId = userId, page = page).handleResponse { it.data }.getOrThrow()
        }
    }

    override suspend fun getNotifications(offset: Int?): Result<NotificationsResponseDto> {
        return runCatching {
            apiService.notifications(offset = offset).handleResponse { it }.getOrThrow()
        }
    }

    override suspend fun getNotificationCount(): Result<Int> {
        return runCatching {
            apiService.notificationCount().handleResponse { it.count }.getOrThrow()
        }
    }

    override suspend fun readAllNotifications(): Result<Unit> {
        return runCatching {
            apiService.readAllNotifications().handleUnitResponse().getOrThrow()
        }
    }

    override suspend fun getTrendingHashtags(): Result<List<SearchHashtagDto>> {
        return runCatching {
            apiService.trendingHashtags().handleResponse { it.data.hashtags }.getOrThrow()
        }
    }

    override suspend fun getTopClans(): Result<List<TopClanDto>> {
        return runCatching {
            apiService.topClans().handleResponse { it.clans }.getOrThrow()
        }
    }

    override suspend fun getSearchResults(q: String): Result<SearchDataDto> {
        return runCatching {
            apiService.search(query = q).handleResponse { it.data }.getOrThrow()
        }
    }
}

private fun <T, R> Response<T>.handleResponse(transform: (T) -> R): Result<R> {
    val body = body()
    return if (isSuccessful && body != null) {
        Result.success(transform(body))
    } else {
        val errorBodyString = errorBody()?.string()
        Result.failure(Exception("API Error ${code()}: $errorBodyString"))
    }
}

private fun <T> Response<T>.handleUnitResponse(): Result<Unit> {
    return if (isSuccessful) {
        Result.success(Unit)
    } else {
        val errorBodyString = errorBody()?.string()
        Result.failure(Exception("API Error ${code()}: $errorBodyString"))
    }
}

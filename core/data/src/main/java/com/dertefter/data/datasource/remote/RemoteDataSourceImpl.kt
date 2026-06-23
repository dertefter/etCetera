package com.dertefter.data.datasource.remote

import com.dertefter.data.dto.auth.SignInRequest
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
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.dto.notifications.NotificationsResponseDto
import com.dertefter.data.dto.poll.VotePollRequestDto
import com.dertefter.data.dto.search.SearchDataDto
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.dto.user.UserDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.w3c.dom.Comment
import java.io.File
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : RemoteDataSource {

    
    override suspend fun signIn(signInRequest: SignInRequest): Result<Unit> {
        return runCatching {
            val response = apiService.signIn(signInRequest)
            if (response.isSuccessful) {
                Unit
            } else {
                throw Exception("Sign in failed: ${response.code()}")
            }
        }
    }

    override suspend fun refreshToken(): Result<Unit> {
        return runCatching {
            val response = apiService.refreshToken()
            if (response.isSuccessful) {
                Unit
            } else {
                throw Exception("Refresh failed: ${response.code()}")
            }
        }
    }

    override suspend fun getMe(): Result<MeDto> {
        return runCatching {
            val response = apiService.me()
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getUser(userId: String): Result<UserDto> {
        return runCatching {
            val response = apiService.user(userId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getPosts(tab: String, cursor: String?): Result<PostDataDto> {
        return runCatching {
            val response = apiService.posts(tab = tab, cursor = cursor)
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getPostsForHashtag(
        hashtagId: String,
        cursor: String?
    ): Result<PostDataDto> {
        return runCatching {
            val response = apiService.postsForHashtag(hashtagId = hashtagId, cursor = cursor)

            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getPost(postId: String): Result<PostDto> {
        return runCatching {
            val response = apiService.post(postId)
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getPosts(
        userId: String,
        pinnedPostId: String?,
        sort: String,
        cursor: String?
    ): Result<PostDataDto> {
        return runCatching {
            val response = apiService.posts(userId = userId, pinnedPostId = pinnedPostId, sort = sort, cursor = cursor)
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getLikedPosts(
        userId: String,
        cursor: String?
    ): Result<PostDataDto> {
        return runCatching {
            val response = apiService.likedPosts(userId = userId, cursor = cursor)
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getComments(
        postId: String,
        cursor: String?,
        sort: String
    ): Result<CommentsDataDto> {
        return runCatching {
            val response = apiService.comments(postId = postId, sort = sort, cursor = cursor)
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getReplies(
        commentId: String,
        page: String?
    ): Result<RepliesDataDto> {
        return runCatching {
            val response = apiService.replies(commentId = commentId, page = page)
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getStats(ids: List<String>): Result<List<PostStatsDto>> {
        return runCatching {
            val response  = apiService.stats(
                PostStatsRequest(ids)
            )
            if (response.isSuccessful) {
                response.body()!!.postStats
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun likePost(postId: String): Result<LikeResponseDto> {
        return runCatching {
            val response = apiService.likePost(postId = postId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun unlikePost(postId: String): Result<LikeResponseDto> {
        return runCatching {
            val response = apiService.unlikePost(postId = postId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun likeComment(commentId: String): Result<LikeResponseDto> {
        return runCatching {
            val response = apiService.likeComment(commentId = commentId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun unlikeComment(commentId: String): Result<LikeResponseDto> {
        return runCatching {
            val response = apiService.unlikeComment(commentId = commentId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun vote(postId: String, optionIds: List<String>): Result<PollDto> {
        return runCatching {
            val response = apiService.vote(postId, VotePollRequestDto(optionIds))
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun newPost(newPostRequest: NewPostRequestDto): Result<PostDto> {
        return runCatching {
            val response = apiService.newPost(newPostRequest)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun newComment(postId: String, newCommentRequest: NewCommentRequestDto): Result<CommentDto> {
        return runCatching {
            val response = apiService.newComment(postId, newCommentRequest)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        return runCatching {
            val response = apiService.deleteComment(commentId)
            if (response.isSuccessful) {
                Unit
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun newCommentReply(
        commentId: String,
        newCommentRequest: NewCommentRequestDto
    ): Result<CommentDto> {
        return runCatching {
            val response = apiService.newCommentReply(commentId, newCommentRequest)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
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
            val response = apiService.upload(multipartBody)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun follow(userId: String): Result<FollowResponseDto> {
        return runCatching {
            val response = apiService.follow(userId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun unfollow(userId: String): Result<FollowResponseDto> {
        return runCatching {
            val response = apiService.unfollow(userId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun updateMe(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponseDto> {
        return runCatching {
            val response = apiService.updateMe(updateMeRequestDto)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getFollowers(
        userId: String,
        page: Int?
    ): Result<FollowersResponseDataDto> {
        return runCatching {
            val response = apiService.followers(userId = userId, page = page)
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getFollowing(
        userId: String,
        page: Int?
    ): Result<FollowersResponseDataDto> {
        return runCatching {
            val response = apiService.following(userId = userId, page = page)
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getNotifications(offset: Int?): Result<NotificationsResponseDto> {
        return runCatching {
            val response = apiService.notifications(offset = offset)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getTrendingHashtags(): Result<List<SearchHashtagDto>> {
        return runCatching {
            val response = apiService.trendingHashtags()
            if (response.isSuccessful) {
                response.body()!!.data.hashtags
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }

    override suspend fun getSearchResults(q: String): Result<SearchDataDto> {
        return runCatching {
            val response = apiService.search(query = q)
            if (response.isSuccessful) {
                response.body()!!.data
            } else {
                throw Exception("${response.code()}, ${response.body()}, ${response.errorBody()}")
            }
        }
    }
}

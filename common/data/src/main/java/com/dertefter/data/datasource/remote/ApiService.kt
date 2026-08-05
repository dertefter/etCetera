package com.dertefter.data.datasource.remote

import com.dertefter.data.dto.auth.SignInRequest
import com.dertefter.data.dto.auth.SignInResponse
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.comments.CommentsResponseDto
import com.dertefter.data.dto.comments.NewCommentRequestDto
import com.dertefter.data.dto.comments.RepliesResponseDto
import com.dertefter.data.dto.feed.FeedResponseDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.PostResponseDto
import com.dertefter.data.dto.feed.like.LikeResponseDto
import com.dertefter.data.dto.feed.stats.PostStatsRequest
import com.dertefter.data.dto.feed.stats.PostStatsResponse
import com.dertefter.data.dto.followers.FollowersResponseDto
import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponseDto
import com.dertefter.data.dto.new_post.EditPostRequestDto
import com.dertefter.data.dto.new_post.EditPostResponseDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.dto.notifications.NotificationsResponseDto
import com.dertefter.data.dto.poll.PollVoteResponseDto
import com.dertefter.data.dto.poll.VotePollRequestDto
import com.dertefter.data.dto.search.SearchResponseDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.dto.user.UserDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/v1/auth/sign-in")
    suspend fun signIn(
        @Body signInRequest: SignInRequest
    ): Response<SignInResponse>

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@retrofit2.http.Tag login: String? = null): Response<Map<String, String>>

    @GET("api/users/me")
    suspend fun me(): Response<MeDto>

    @GET("api/posts/{postId}")
    suspend fun post(
        @Path("postId") postId: String,
    ): Response<PostResponseDto>

    @GET("api/notifications")
    suspend fun notifications(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int?,
    ): Response<NotificationsResponseDto>


    @GET("api/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("userLimit") userLimit: Int = 10,
        @Query("hashtagLimit") hashtagLimit: Int = 5,
    ): Response<SearchResponseDto>

    @GET("api/users/{userId}")
    suspend fun user(
        @Path("userId") userId: String,
    ): Response<UserDto>

    @POST("api/posts")
    suspend fun newPost(
        @Body postRequest: NewPostRequestDto
    ): Response<PostDto>

    @PUT("api/posts/{postId}")
    suspend fun editPost(
        @Path("postId") postId: String,
        @Body postRequest: EditPostRequestDto
    ): Response<EditPostResponseDto>

    @POST("api/posts/{postId}/repost")
    suspend fun repost(
        @Path("postId") postId: String,
        @Body postRequest: NewPostRequestDto
    ): Response<PostDto>

    @POST("api/posts/{postId}/comments")
    suspend fun newComment(
        @Path("postId") postId: String,
        @Body postRequest: NewCommentRequestDto
    ): Response<CommentDto>

    @DELETE("api/comments/{commentId}")
    suspend fun deleteComment(
        @Path("commentId") commentId: String
    ): Response<Unit>

    @DELETE("api/posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: String
    ): Response<Unit>

    @DELETE("api/posts/{postId}/pin")
    suspend fun unpinPost(
        @Path("postId") postId: String
    ): Response<Unit>

    @POST("api/posts/{postId}/pin")
    suspend fun pinPost(
        @Path("postId") postId: String
    ): Response<Unit>

    @POST("api/comments/{commentId}/replies")
    suspend fun newCommentReply(
        @Path("commentId") commentId: String,
        @Body postRequest: NewCommentRequestDto
    ): Response<CommentDto>

    @GET("api/posts")
    suspend fun posts(
        @Query("limit") limit: Int = 20,
        @Query("tab") tab: String,
        @Query("cursor") cursor: String?
    ): Response<FeedResponseDto>

    @GET("api/hashtags/{hashtagId}/posts")
    suspend fun postsForHashtag(
        @Path("hashtagId") hashtagId: String,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String?
    ): Response<FeedResponseDto>


    @GET("api/hashtags/trending")
    suspend fun trendingHashtags(
        @Query("limit") limit: Int = 10
    ): Response<SearchResponseDto>

    @GET("api/posts/user/{userId}")
    suspend fun posts(
        @Path("userId") userId: String,
        @Query("limit") limit: Int = 20,
        @Query("pinnedPostId") pinnedPostId: String?,
        @Query("sort") sort: String = "new",
        @Query("cursor") cursor: String?
    ): Response<FeedResponseDto>

    @GET("api/posts/user/{userId}/liked")
    suspend fun likedPosts(
        @Path("userId") userId: String,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String?
    ): Response<FeedResponseDto>

    @Multipart
    @POST("api/files/upload")
    suspend fun upload(
        @Part file: MultipartBody.Part
    ): Response<AttachmentUploadResponseDto>

    @GET("api/posts/{id}/comments")
    suspend fun comments(
        @Path("id") postId: String,
        @Query("limit") limit: Int = 100,
        @Query("sort") sort: String = "popular",
        @Query("cursor") cursor: String?
    ): Response<CommentsResponseDto>

    @GET("api/comments/{id}/replies")
    suspend fun replies(
        @Path("id") commentId: String,
        @Query("limit") limit: Int = 100,
        @Query("page") page: String?
    ): Response<RepliesResponseDto>

    @GET("api/users/{id}/followers")
    suspend fun followers(
        @Path("id") userId: String,
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int?
    ): Response<FollowersResponseDto>

    @GET("api/users/{id}/following")
    suspend fun following(
        @Path("id") userId: String,
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int?
    ): Response<FollowersResponseDto>


    @POST("api/posts/{id}/like")
    suspend fun likePost(
        @Path("id") postId: String
    ): Response<LikeResponseDto>

    @DELETE("api/posts/{id}/like")
    suspend fun unlikePost(
        @Path("id") postId: String
    ): Response<LikeResponseDto>

    @POST("api/comments/{id}/like")
    suspend fun likeComment(
        @Path("id") commentId: String
    ): Response<LikeResponseDto>

    @DELETE("api/comments/{id}/like")
    suspend fun unlikeComment(
        @Path("id") commentId: String
    ): Response<LikeResponseDto>

    @POST("api/posts/stats")
    suspend fun stats(
        @Body postStatsRequest: PostStatsRequest
    ): Response<PostStatsResponse>

    @POST("api/posts/{postId}/poll/vote")
    suspend fun vote(
        @Path("postId") postId: String,
        @Body votePollRequestDto: VotePollRequestDto
    ): Response<PollVoteResponseDto>

    @POST("api/users/{userId}/follow")
    suspend fun follow(
        @Path("userId") userId: String
    ): Response<FollowResponseDto>

    @DELETE("api/users/{userId}/follow")
    suspend fun unfollow(
        @Path("userId") userId: String
    ): Response<FollowResponseDto>

    @PUT("api/users/me")
    suspend fun updateMe(
        @Body body: UpdateMeRequestDto
    ): Response<UpdateMeResponseDto>

}
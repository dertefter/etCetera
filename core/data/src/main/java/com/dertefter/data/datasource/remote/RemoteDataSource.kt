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
import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.feed.stats.PostStatsDto
import com.dertefter.data.dto.followers.FollowersResponseDataDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.me.UpdateMeResponseDto
import com.dertefter.data.dto.new_post.NewPostRequestDto
import com.dertefter.data.dto.notifications.NotificationsResponseDto
import com.dertefter.data.dto.search.SearchDataDto
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.upload.AttachmentUploadResponseDto
import com.dertefter.data.dto.user.FollowResponseDto
import com.dertefter.data.dto.user.UserDto
import java.io.File

interface RemoteDataSource {

    suspend fun signIn(signInRequest: SignInRequest): Result<Unit>
    suspend fun refreshToken(): Result<Unit>
    suspend fun getMe(): Result<MeDto>

    suspend fun getUser(userId: String): Result<UserDto>

    suspend fun getPosts(tab: String, cursor: String?): Result<PostDataDto>
    suspend fun getPostsForHashtag(hashtagId: String, cursor: String?): Result<PostDataDto>

    suspend fun getPost(postId: String): Result<PostDto>

    suspend fun getPosts(userId: String, pinnedPostId: String?, sort: String, cursor: String?): Result<PostDataDto>

    suspend fun getLikedPosts(userId: String, cursor: String?): Result<PostDataDto>

    suspend fun getComments(postId: String, cursor: String?, sort: String): Result<CommentsDataDto>

    suspend fun getReplies(commentId: String, page: String?): Result<RepliesDataDto>

    suspend fun getStats(ids: List<String>): Result<List<PostStatsDto>>

    suspend fun likePost(postId: String): Result<LikeResponseDto>

    suspend fun unlikePost(postId: String): Result<LikeResponseDto>

    suspend fun likeComment(commentId: String): Result<LikeResponseDto>

    suspend fun unlikeComment(commentId: String): Result<LikeResponseDto>

    suspend fun vote(postId: String, optionIds: List<String>): Result<PollDto>

    suspend fun newPost(newPostRequest: NewPostRequestDto): Result<PostDto>

    suspend fun newComment(postId: String, newCommentRequest: NewCommentRequestDto): Result<CommentDto>

    suspend fun deleteComment(commentId: String): Result<Unit>

    suspend fun newCommentReply(
        commentId: String,
        newCommentRequest: NewCommentRequestDto
    ): Result<CommentDto>

    suspend fun uploadMyFile(myFile: File): Result<AttachmentUploadResponseDto>

    suspend fun follow(userId: String): Result<FollowResponseDto>

    suspend fun unfollow(userId: String): Result<FollowResponseDto>

    suspend fun updateMe(updateMeRequestDto: UpdateMeRequestDto): Result<UpdateMeResponseDto>

    suspend fun getFollowers(userId: String, page: Int?): Result<FollowersResponseDataDto>

    suspend fun getFollowing(userId: String, page: Int?): Result<FollowersResponseDataDto>

    suspend fun getNotifications(offset: Int?): Result<NotificationsResponseDto>

    suspend fun getTrendingHashtags(): Result<List<SearchHashtagDto>>

    suspend fun getSearchResults(q: String): Result<SearchDataDto>

}

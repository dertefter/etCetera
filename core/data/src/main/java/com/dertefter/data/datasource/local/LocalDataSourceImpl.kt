package com.dertefter.data.datasource.local

import com.dertefter.data.datasource.local.room.dao.CommentDao
import com.dertefter.data.datasource.local.room.dao.NotificationDao
import com.dertefter.data.datasource.local.room.dao.PageDao
import com.dertefter.data.datasource.local.room.dao.PostDao
import com.dertefter.data.datasource.local.room.dao.SearchDao
import com.dertefter.data.datasource.local.room.dao.UserDao
import com.dertefter.data.datasource.local.room.entity.CommentEntity
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.datasource.local.room.entity.asEntity
import com.dertefter.data.datasource.local.room.entity.asExternalModel
import com.dertefter.data.datasource.local.room.entity.asFollowerExternalModel
import com.dertefter.data.datasource.local.room.entity.asMeExternalModel
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.user.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSourceImpl @Inject constructor(
    private val pageDao: PageDao,
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val searchDao: SearchDao,
    private val notificationDao: NotificationDao
) : LocalDataSource {

    override val meDto: Flow<MeDto?> = userDao.getMe()
        .map { it?.asMeExternalModel() }

    override suspend fun saveMe(meDto: MeDto) {
        userDao.insertUser(meDto.asEntity())
    }

    override fun getUser(userId: String): Flow<UserDto?> = userDao.getUser(userId)
        .map { it?.asExternalModel() }

    override suspend fun saveUser(userDto: UserDto) {
        val existingUser = userDao.getUserById(userDto.id)
        val isMe = existingUser?.isMe ?: false
        userDao.insertUser(userDto.asEntity().copy(isMe = isMe))
    }

    override suspend fun upsertPage(page: PageEntity) {
        pageDao.upsert(page)
    }

    override suspend fun getPage(type: PageType, tab: String, self: String): PageEntity? {
        return pageDao.getPage(type, tab, self)
    }

    override suspend fun getAllPages(type: PageType, tab: String): List<PageEntity> {
        return pageDao.getAllPages(type, tab)
    }

    override suspend fun getAllPages(type: PageType): List<PageEntity> {
        return pageDao.getAllPages(type)
    }

    override suspend fun deletePage(type: PageType, tab: String, self: String) {
        pageDao.deletePage(type, tab, self)
    }

    override suspend fun deleteAllPages(type: PageType, tab: String) {
        pageDao.deleteAll(type, tab)
    }

    override suspend fun savePosts(type: PageType, tab: String, self: String, posts: List<PostDto>) {
        postDao.savePageWithPosts(type, tab, self, posts.map { it.asEntity() })
    }

    override suspend fun getPostsForPage(type: PageType, tab: String, self: String): List<PostDto> {
        return postDao.getPostsForPage(type, tab, self).map { it.asExternalModel() }
    }

    override suspend fun getAllPosts(): List<PostDto> {
        return postDao.getAllPosts().map { it.asExternalModel() }
    }

    override suspend fun savePost(post: PostDto) {
        postDao.upsertPosts(listOf(post.asEntity()))
    }

    override suspend fun deletePost(postId: String) {
        postDao.deletePost(postId)
    }

    override fun getPost(postId: String): Flow<PostDto?> {
        return postDao.getPost(postId).map { it?.asExternalModel() }
    }

    override suspend fun saveComments(type: PageType, tab: String, self: String, comments: List<CommentDto>) {
        val flatComments = mutableListOf<CommentEntity>()
        fun flatten(list: List<CommentDto>, parentId: String?) {
            list.forEach { dto ->
                flatComments.add(dto.asEntity(parentId))
                dto.replies?.let { flatten(it, dto.id) }
            }
        }
        flatten(comments, null)
        commentDao.savePageWithComments(type, tab, self, flatComments.filter { it.parentId == null })
        commentDao.upsertComments(flatComments.filter { it.parentId != null })
    }

    override suspend fun getCommentsForPage(type: PageType, tab: String, self: String): List<CommentDto> {
        val topLevel = commentDao.getCommentsForPage(type, tab, self)
        suspend fun fillReplies(entity: CommentEntity): CommentDto {
            val replies = commentDao.getRepliesForComment(entity.id).map { fillReplies(it) }
            return entity.asExternalModel(replies.ifEmpty { null })
        }
        return topLevel.map { fillReplies(it) }
    }

    override suspend fun getAllComments(): List<CommentDto> {
        // This is tricky because we need the tree. For now, let's just get all and don't care about nested structure for simple updates
        return commentDao.getAllComments().map { it.asExternalModel() }
    }

    override suspend fun saveComment(comment: CommentDto) {
        val flatComments = mutableListOf<CommentEntity>()
        fun flatten(dto: CommentDto, parentId: String?) {
            flatComments.add(dto.asEntity(parentId))
            dto.replies?.forEach { flatten(it, dto.id) }
        }
        val existing = commentDao.getCommentById(comment.id)
        flatten(comment, existing?.parentId)
        commentDao.upsertComments(flatComments)
    }

    override suspend fun saveUsers(type: PageType, tab: String, self: String, users: List<FollowerUserDto>) {
        userDao.savePageWithUsers(type, tab, self, users.map { it.asEntity() })
    }

    override suspend fun getUsersForPage(type: PageType, tab: String, self: String): List<FollowerUserDto> {
        return userDao.getUsersForPage(type, tab, self).map { it.asFollowerExternalModel() }
    }

    override suspend fun saveNotifications(type: PageType, tab: String, self: String, notifications: List<NotificationDto>) {
        notificationDao.savePageWithNotifications(type, tab, self, notifications.map { it.asEntity() })
    }

    override suspend fun getNotificationsForPage(type: PageType, tab: String, self: String): List<NotificationDto> {
        return notificationDao.getNotificationsForPage(type, tab, self).map { it.asExternalModel() }
    }

    override fun getTrendingHashtags(): Flow<List<SearchHashtagDto>?> {
        return searchDao.getTrendingHashtags().map { it.map { it.asExternalModel() } }
    }

    override suspend fun saveTrendingHashtags(hashtags: List<SearchHashtagDto>) {
        searchDao.updateTrendingHashtags(hashtags.map { it.asEntity() })
    }
}

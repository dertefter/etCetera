package com.dertefter.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.room.Room
import com.dertefter.data.datasource.local.room.AppDatabase
import com.dertefter.data.datasource.local.room.entity.CommentEntity
import com.dertefter.data.datasource.local.room.entity.PageEntity
import com.dertefter.data.datasource.local.room.entity.PageType
import com.dertefter.data.datasource.local.room.entity.asEntity
import com.dertefter.data.datasource.local.room.entity.asExternalModel
import com.dertefter.data.datasource.local.room.entity.asFollowerExternalModel
import com.dertefter.data.datasource.local.room.entity.asMeExternalModel
import com.dertefter.data.di.AuthDataStore
import com.dertefter.data.di.SettingsDataStore
import com.dertefter.data.dto.auth.AuthSessionDto
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.search.TopClanDto
import com.dertefter.data.dto.user.UserDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class LocalDataSourceImpl @Inject constructor(
    @AuthDataStore private val authDataStore: DataStore<Preferences>,
    @SettingsDataStore private val settingsDataStore: DataStore<Preferences>,
    @param:ApplicationContext private val context: Context
) : LocalDataSource {

    private val CURRENT_LOGIN_KEY = stringPreferencesKey("current_login")
    private val LOGIN_HISTORY_KEY = stringSetPreferencesKey("login_history")
    private val EMOJI_AVATAR_HARMONIZATION_COLOR_KEY = stringPreferencesKey("emoji_avatar_harmonization_color")
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    private val dbCache = mutableMapOf<String?, AppDatabase>()

    private fun getDatabase(login: String?): AppDatabase {
        return dbCache.getOrPut(login) {
            val dbName = if (login == null) "etcetera-database-guest" else "etcetera-database-$login"
            Room.databaseBuilder(context, AppDatabase::class.java, dbName)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }

    private suspend fun db() = getDatabase(currentLogin.first())

    override val currentLogin: Flow<String?> = authDataStore.data.map { preferences ->
        preferences[CURRENT_LOGIN_KEY]
    }

    override suspend fun switchToLogin(login: String?) {
        authDataStore.edit { preferences ->
            if (login == null) {
                preferences.remove(CURRENT_LOGIN_KEY)
            } else {
                preferences[CURRENT_LOGIN_KEY] = login
                val currentHistory = preferences[LOGIN_HISTORY_KEY] ?: emptySet()
                preferences[LOGIN_HISTORY_KEY] = currentHistory + login
            }
        }
    }

    override val loginHistory: Flow<List<String>> = authDataStore.data.map { preferences ->
        preferences[LOGIN_HISTORY_KEY]?.toList() ?: emptyList()
    }

    override suspend fun removeLoginFromHistory(login: String) {
        authDataStore.edit { preferences ->
            val currentHistory = preferences[LOGIN_HISTORY_KEY] ?: emptySet()
            preferences[LOGIN_HISTORY_KEY] = currentHistory - login
        }
    }

    override val authSessions: Flow<List<AuthSessionDto>?> = currentLogin.flatMapLatest { login ->
        getDatabase(login).authSessionDao().getAuthSessions()
    }.map { list -> list.map { it.asExternalModel() } }

    override suspend fun saveAuthSessions(sessions: List<AuthSessionDto>) {
        val dao = db().authSessionDao()
        dao.clearAuthSessions()
        dao.insertAuthSessions(sessions.map { it.asEntity() })
    }

    override val meDto: Flow<MeDto?> = currentLogin.flatMapLatest { login ->
        getDatabase(login).userDao().getMe()
    }.map { it?.asMeExternalModel() }

    override suspend fun saveMe(meDto: MeDto) {
        db().userDao().insertUser(meDto.asEntity())
    }

    override fun getUser(userId: String): Flow<UserDto?> = currentLogin.flatMapLatest { login ->
        getDatabase(login).userDao().getUser(userId)
    }.map { it?.asExternalModel() }

    override suspend fun saveUser(userDto: UserDto) {
        val database = db()
        val existingUser = database.userDao().getUserById(userDto.id)
        val isMe = existingUser?.isMe ?: false
        database.userDao().insertUser(userDto.asEntity().copy(isMe = isMe))
    }

    override suspend fun upsertPage(page: PageEntity) {
        db().pageDao().upsert(page)
    }

    override suspend fun getPage(type: PageType, tab: String, self: String): PageEntity? {
        return db().pageDao().getPage(type, tab, self)
    }

    override suspend fun getAllPages(type: PageType, tab: String): List<PageEntity> {
        return db().pageDao().getAllPages(type, tab)
    }

    override suspend fun getAllPages(type: PageType): List<PageEntity> {
        return db().pageDao().getAllPages(type)
    }

    override suspend fun deletePage(type: PageType, tab: String, self: String) {
        db().pageDao().deletePage(type, tab, self)
    }

    override suspend fun deleteAllPages(type: PageType, tab: String) {
        db().pageDao().deleteAll(type, tab)
    }

    override suspend fun savePosts(type: PageType, tab: String, self: String, posts: List<PostDto>) {
        db().postDao().savePageWithPosts(type, tab, self, posts.map { it.asEntity() })
    }

    override suspend fun getPostsForPage(type: PageType, tab: String, self: String): List<PostDto> {
        return db().postDao().getPostsForPage(type, tab, self).map { it.asExternalModel() }
    }

    override suspend fun getAllPosts(): List<PostDto> {
        return db().postDao().getAllPosts().map { it.asExternalModel() }
    }

    override suspend fun savePost(post: PostDto) {
        db().postDao().upsertPosts(listOf(post.asEntity()))
    }

    override suspend fun deletePost(postId: String) {
        db().postDao().deletePost(postId)
    }

    override fun getPost(postId: String): Flow<PostDto?> = currentLogin.flatMapLatest { login ->
        getDatabase(login).postDao().getPost(postId)
    }.map { it?.asExternalModel() }

    override suspend fun saveComments(type: PageType, tab: String, self: String, comments: List<CommentDto>) {
        val database = db()
        val flatComments = mutableListOf<CommentEntity>()
        fun flatten(list: List<CommentDto>, parentId: String?) {
            list.forEach { dto ->
                flatComments.add(dto.asEntity(parentId))
                dto.replies?.let { flatten(it, dto.id) }
            }
        }
        flatten(comments, null)
        database.commentDao().savePageWithComments(type, tab, self, flatComments.filter { it.parentId == null })
        database.commentDao().upsertComments(flatComments.filter { it.parentId != null })
    }

    override suspend fun getCommentsForPage(type: PageType, tab: String, self: String): List<CommentDto> {
        val database = db()
        val topLevel = database.commentDao().getCommentsForPage(type, tab, self)
        suspend fun fillReplies(entity: CommentEntity): CommentDto {
            val replies = database.commentDao().getRepliesForComment(entity.id).map { fillReplies(it) }
            return entity.asExternalModel(replies.ifEmpty { null })
        }
        return topLevel.map { fillReplies(it) }
    }

    override suspend fun getAllComments(): List<CommentDto> {
        return db().commentDao().getAllComments().map { it.asExternalModel() }
    }

    override suspend fun saveComment(comment: CommentDto) {
        val database = db()
        val flatComments = mutableListOf<CommentEntity>()
        fun flatten(dto: CommentDto, parentId: String?) {
            flatComments.add(dto.asEntity(parentId))
            dto.replies?.forEach { flatten(it, dto.id) }
        }
        val existing = database.commentDao().getCommentById(comment.id)
        flatten(comment, existing?.parentId)
        database.commentDao().upsertComments(flatComments)
    }

    override suspend fun saveUsers(type: PageType, tab: String, self: String, users: List<FollowerUserDto>) {
        db().userDao().savePageWithUsers(type, tab, self, users.map { it.asEntity() })
    }

    override suspend fun getUsersForPage(type: PageType, tab: String, self: String): List<FollowerUserDto> {
        return db().userDao().getUsersForPage(type, tab, self).map { it.asFollowerExternalModel() }
    }

    override suspend fun saveNotifications(type: PageType, tab: String, self: String, notifications: List<NotificationDto>) {
        db().notificationDao().savePageWithNotifications(type, tab, self, notifications.map { it.asEntity() })
    }

    override suspend fun getNotificationsForPage(type: PageType, tab: String, self: String): List<NotificationDto> {
        return db().notificationDao().getNotificationsForPage(type, tab, self).map { it.asExternalModel() }
    }

    override fun getTrendingHashtags(): Flow<List<SearchHashtagDto>?> = currentLogin.flatMapLatest { login ->
        getDatabase(login).searchDao().getTrendingHashtags()
    }.map { it.map { hashtag -> hashtag.asExternalModel() } }

    override suspend fun saveTrendingHashtags(hashtags: List<SearchHashtagDto>) {
        db().searchDao().updateTrendingHashtags(hashtags.map { it.asEntity() })
    }

    override fun getTopClans(): Flow<List<TopClanDto>?> = currentLogin.flatMapLatest { login ->
        getDatabase(login).searchDao().getTopClans()
    }.map { it.map { clan -> clan.asExternalModel() } }

    override suspend fun saveTopClans(clans: List<TopClanDto>) {
        db().searchDao().updateTopClans(clans.map { it.asEntity() })
    }

    override val emojiAvatarHarmonizationColor: Flow<String?> = settingsDataStore.data.map { preferences ->
        preferences[EMOJI_AVATAR_HARMONIZATION_COLOR_KEY]
    }

    override suspend fun updateEmojiAvatarHarmonizationColor(color: String?) {
        settingsDataStore.edit { preferences ->
            if (color == null) {
                preferences.remove(EMOJI_AVATAR_HARMONIZATION_COLOR_KEY)
            } else {
                preferences[EMOJI_AVATAR_HARMONIZATION_COLOR_KEY] = color
            }
        }
    }

    override val darkTheme: Flow<Boolean?> = settingsDataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY]
    }

    override suspend fun updateDarkTheme(darkTheme: Boolean?) {
        settingsDataStore.edit { preferences ->
            if (darkTheme == null) {
                preferences.remove(DARK_THEME_KEY)
            } else {
                preferences[DARK_THEME_KEY] = darkTheme
            }
        }
    }
}

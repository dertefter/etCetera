package com.dertefter.data.di

import com.dertefter.data.repository.*
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
interface RepositoryModule {

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindAttachmentsRepository(impl: AttachmentsRepositoryImpl): AttachmentsRepository

    @Binds
    fun bindMeRepository(impl: MeRepositoryImpl): MeRepository

    @Binds
    fun bindFeedRepository(impl: FeedRepositoryImpl): FeedRepository

    @Binds
    fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    fun bindCommentsRepository(impl: CommentsRepositoryImpl): CommentsRepository

    @Binds
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    fun bindFollowersRepository(impl: FollowersRepositoryImpl): FollowersRepository

    @Binds
    fun bindNotificationsRepository(impl: NotificationsRepositoryImpl): NotificationsRepository

    @Binds
    fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    fun bindCrashlyticsRepository(impl: CrashlyticsRepositoryImpl): CrashlyticsRepository

    @Binds
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    fun bindReportsRepository(impl: ReportsRepositoryImpl): ReportsRepository

    @Binds
    fun bindEventsRepository(impl: EventsRepositoryImpl): EventsRepository
}

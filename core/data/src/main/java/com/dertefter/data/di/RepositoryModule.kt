package com.dertefter.data.di

import com.dertefter.data.repository.AuthRepository
import com.dertefter.data.repository.AuthRepositoryImpl
import com.dertefter.data.repository.CommentsRepository
import com.dertefter.data.repository.CommentsRepositoryImpl
import com.dertefter.data.repository.CrashlyticsRepository
import com.dertefter.data.repository.CrashlyticsRepositoryImpl
import com.dertefter.data.repository.FeedRepository
import com.dertefter.data.repository.FeedRepositoryImpl
import com.dertefter.data.repository.FollowersRepository
import com.dertefter.data.repository.FollowersRepositoryImpl
import com.dertefter.data.repository.MeRepository
import com.dertefter.data.repository.MeRepositoryImpl
import com.dertefter.data.repository.NewPostRepository
import com.dertefter.data.repository.NewPostRepositoryImpl
import com.dertefter.data.repository.NotificationsRepository
import com.dertefter.data.repository.NotificationsRepositoryImpl
import com.dertefter.data.repository.SearchRepository
import com.dertefter.data.repository.SearchRepositoryImpl
import com.dertefter.data.repository.UserRepository
import com.dertefter.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMeRepository(
        meRepositoryImpl: MeRepositoryImpl
    ): MeRepository

    @Binds
    @Singleton
    abstract fun bindFeedRepository(
        feedRepositoryImpl: FeedRepositoryImpl
    ): FeedRepository

    @Binds
    @Singleton
    abstract fun bindCommentsRepository(
        commentsRepositoryImpl: CommentsRepositoryImpl
    ): CommentsRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindFollowersRepository(
        followersRepositoryImpl: FollowersRepositoryImpl
    ): FollowersRepository

    @Binds
    @Singleton
    abstract fun bindNewPostRepository(
        newPostRepository: NewPostRepositoryImpl
    ): NewPostRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(
        notificationsRepositoryImpl: NotificationsRepositoryImpl
    ): NotificationsRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepoImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindCrashlyticsRepository(
        crashlyticsRepositoryImpl: CrashlyticsRepositoryImpl
    ): CrashlyticsRepository
}

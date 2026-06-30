package com.dertefter.etcetera.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dertefter.auth.AuthRoute
import com.dertefter.banner_edit.BannerEditRoute
import com.dertefter.comments.CommentsRoute
import com.dertefter.crash_reports.CrashReportsRoute
import com.dertefter.feed.FeedRoute
import com.dertefter.followers.FollowersRoute
import com.dertefter.attachment_viewer.AttachmentViewerRoute
import com.dertefter.hashtag_feed.HashtagFeedRoute
import com.dertefter.navigation.AttachmentNavigationModel
import com.dertefter.navigation.Routes
import com.dertefter.new_comment.NewCommentReplyRoute
import com.dertefter.new_comment.NewCommentRoute
import com.dertefter.new_post.NewPostRoute
import com.dertefter.notifications.NotificationsRoute
import com.dertefter.post.PostRoute
import com.dertefter.search.SearchRoute
import com.dertefter.user.UserRoute
import kotlin.reflect.typeOf


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Routes
) {
    NavHost(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(300)
            )
        }
    ) {
        graph()
    }
}

@Composable
fun RouteContent(route: Routes) {
    when (route) {
        is Routes.Auth -> AuthRoute()
        is Routes.Feed -> FeedRoute()
        is Routes.Comments -> CommentsRoute(route.postId)
        is Routes.User -> UserRoute(route.userId)
        is Routes.NewPost -> NewPostRoute(route.wallRecipientId, route.postIdForRepost)
        is Routes.NewComment -> NewCommentRoute(route.postId)
        is Routes.NewCommentReply -> NewCommentReplyRoute(route.postId, route.commentId, route.userId)
        is Routes.AttachmentsViewer -> AttachmentViewerRoute(route.attachments, route.viewPosition)
        is Routes.Followers -> FollowersRoute()
        is Routes.Notifications -> NotificationsRoute()
        is Routes.BannerEdit -> BannerEditRoute()
        is Routes.CrashReports -> CrashReportsRoute()
        is Routes.Post -> PostRoute()
        is Routes.Search -> SearchRoute()
        is Routes.HashtagFeed -> HashtagFeedRoute(route.hashtagName)
    }
}

fun NavGraphBuilder.graph() {

    composable<Routes.CrashReports> {
        RouteContent(Routes.CrashReports)
    }

    composable<Routes.Auth> {
        RouteContent(Routes.Auth)
    }

    composable<Routes.Feed> {
        RouteContent(Routes.Feed)
    }

    composable<Routes.Notifications> {
        RouteContent(Routes.Notifications)
    }

    composable<Routes.BannerEdit> {
        RouteContent(Routes.BannerEdit)
    }

    composable<Routes.Search> {
        RouteContent(Routes.Search)
    }

    composable<Routes.Comments> {
        val args = it.toRoute<Routes.Comments>()
        RouteContent(args)
    }

    composable<Routes.User> {
        val args = it.toRoute<Routes.User>()
        RouteContent(args)
    }

    composable<Routes.NewPost> {
        val args = it.toRoute<Routes.NewPost>()
        RouteContent(args)
    }

    composable<Routes.NewComment> {
        val args = it.toRoute<Routes.NewComment>()
        RouteContent(args)
    }

    composable<Routes.NewCommentReply> {
        val args = it.toRoute<Routes.NewCommentReply>()
        RouteContent(args)
    }

    composable<Routes.Followers> {
        val args = it.toRoute<Routes.Followers>()
        RouteContent(args)
    }

    composable<Routes.Post> {
        val args = it.toRoute<Routes.Post>()
        RouteContent(args)
    }

    composable<Routes.HashtagFeed> {
        val args = it.toRoute<Routes.HashtagFeed>()
        RouteContent(args)
    }

    composable<Routes.AttachmentsViewer>(
        typeMap = mapOf(typeOf<List<AttachmentNavigationModel>>() to AttachmentListType)
    ) {
        val args = it.toRoute<Routes.AttachmentsViewer>()
        RouteContent(args)
    }

}

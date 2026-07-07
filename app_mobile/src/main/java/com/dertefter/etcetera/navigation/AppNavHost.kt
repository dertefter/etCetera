package com.dertefter.etcetera.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dertefter.auth.AuthRoute
import com.dertefter.banner_edit.BannerEditRoute
import com.dertefter.comments.CommentsRoute
import com.dertefter.crash_reports.CrashReportsRoute
import com.dertefter.feed.FeedRoute
import com.dertefter.followers.FollowersRoute
import com.dertefter.attachment_viewer.AttachmentViewerRoute
import com.dertefter.hashtag_feed.HashtagFeedRoute
import com.dertefter.navigation.Routes
import com.dertefter.new_comment.NewCommentReplyRoute
import com.dertefter.new_comment.NewCommentRoute
import com.dertefter.new_post.NewPostRoute
import com.dertefter.notifications.NotificationsRoute
import com.dertefter.post.PostRoute
import com.dertefter.search.SearchRoute
import com.dertefter.user.UserRoute


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit
) {
    NavDisplay(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        backStack = backStack,
        onBack = onBack,
        transitionSpec = {
            (fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { it }
            )) togetherWith (fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { -it }
            ))
        },
        popTransitionSpec = {
            (fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { -it }
            )) togetherWith (fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { it }
            ))
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Routes.CrashReports> { RouteContent(it) }
            entry<Routes.Auth> { RouteContent(it) }
            entry<Routes.Feed> { RouteContent(it) }
            entry<Routes.Notifications> { RouteContent(it) }
            entry<Routes.BannerEdit> { RouteContent(it) }
            entry<Routes.Search> { RouteContent(it) }
            entry<Routes.Comments> { RouteContent(it) }
            entry<Routes.User> { RouteContent(it) }
            entry<Routes.NewPost> { RouteContent(it) }
            entry<Routes.NewComment> { RouteContent(it) }
            entry<Routes.NewCommentReply> { RouteContent(it) }
            entry<Routes.Followers> { RouteContent(it) }
            entry<Routes.Post> { RouteContent(it) }
            entry<Routes.HashtagFeed> { RouteContent(it) }
            entry<Routes.AttachmentsViewer> { RouteContent(it) }
        }
    )
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
        is Routes.Followers -> FollowersRoute(route.userId, route.startTabIsFollowing)
        is Routes.Notifications -> NotificationsRoute()
        is Routes.BannerEdit -> BannerEditRoute()
        is Routes.CrashReports -> CrashReportsRoute()
        is Routes.Post -> PostRoute(route.postId)
        is Routes.Search -> SearchRoute()
        is Routes.HashtagFeed -> HashtagFeedRoute(route.hashtagName)
    }
}

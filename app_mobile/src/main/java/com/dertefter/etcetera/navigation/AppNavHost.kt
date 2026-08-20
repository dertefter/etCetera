package com.dertefter.etcetera.navigation

import android.util.Log
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import com.dertefter.attachment_viewer.AttachmentViewerRoute
import com.dertefter.auth.AuthRoute
import com.dertefter.banner_edit.BannerEditRoute
import com.dertefter.comments.CommentsRoute
import com.dertefter.crash_reports.CrashReportsRoute
import com.dertefter.feed.FeedRoute
import com.dertefter.followers.FollowersRoute
import com.dertefter.hashtag_feed.HashtagFeedRoute
import com.dertefter.navigation.Routes
import com.dertefter.new_post.NewCommentReplyRoute
import com.dertefter.new_post.NewCommentRoute
import com.dertefter.new_post.NewPostRoute
import com.dertefter.new_post.RepostRoute
import com.dertefter.new_post.EditPostRoute
import com.dertefter.notifications.NotificationsRoute
import com.dertefter.post.PostRoute
import com.dertefter.search.SearchRoute
import com.dertefter.settings.SettingsRoute
import com.dertefter.settings_theme.SettingsThemeRoute
import com.dertefter.user.UserRoute

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit
) {

    val ms = MaterialTheme.motionScheme

    NavDisplay(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        backStack = backStack,
        onBack = onBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            fadeIn(
                initialAlpha = 0.5f,
                animationSpec = ms.slowEffectsSpec()
            ) + scaleIn(
                initialScale = 0.9f,
                animationSpec =  ms.slowEffectsSpec()
            ) + slideInVertically(
                initialOffsetY = {it/6},
                animationSpec = ms.slowEffectsSpec()
            ) togetherWith fadeOut(
                animationSpec = ms.fastEffectsSpec()
            )
        },
        popTransitionSpec = {
            fadeIn(
                animationSpec = ms.slowEffectsSpec()
            ) togetherWith fadeOut(
                animationSpec = tween(easing = EaseIn, durationMillis = 120)
            ) + scaleOut(
                targetScale = 0.9f,
                animationSpec = tween(easing = EaseIn, durationMillis = 200)
            ) + slideOutVertically(
                targetOffsetY = { it / 6 },
                animationSpec = tween(easing = EaseIn, durationMillis = 200)
            )
        },
        predictivePopTransitionSpec = { swipeEdge ->
            Log.e("swipeEdge", swipeEdge.toString())
            val enter =
                fadeIn(
                    initialAlpha = 0.5f,
                    animationSpec = tween(easing = EaseOut, durationMillis = 150)
                )
            val exit =
                when (swipeEdge) {
                    NavigationEvent.EDGE_RIGHT -> {
                        slideOutHorizontally( animationSpec = tween(easing = EaseIn, durationMillis = 100)) { -it }
                    }
                    else -> {
                        slideOutHorizontally( animationSpec = tween(easing = EaseIn, durationMillis = 100)) { it }
                    }
                }
            (enter togetherWith exit.apply {
                this@NavDisplay.initialState.apply {
                    modifier.shadow(elevation = 20.dp)
                }
            })
        },
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
            entry<Routes.Repost> { RouteContent(it) }
            entry<Routes.EditPost> { RouteContent(it) }
            entry<Routes.NewComment> { RouteContent(it) }
            entry<Routes.NewCommentReply> { RouteContent(it) }
            entry<Routes.Followers> { RouteContent(it) }
            entry<Routes.Post> { RouteContent(it) }
            entry<Routes.HashtagFeed> { RouteContent(it) }
            entry<Routes.AttachmentsViewer> { RouteContent(it) }
            entry<Routes.Settings> { RouteContent(it) }
            entry<Routes.SettingsTheme> { RouteContent(it) }
        }
    )
}

@Composable
fun RouteContent(route: Routes) {
    when (route) {
        is Routes.Auth -> AuthRoute()
        is Routes.Feed -> FeedRoute()
        is Routes.Comments -> CommentsRoute(route.postId)
        is Routes.User -> UserRoute(route.userId, route.showBackButton)
        is Routes.NewPost -> NewPostRoute(route.wallRecipientId)
    is Routes.Repost -> RepostRoute(route.postIdForRepost, route.wallRecipientId)
    is Routes.EditPost -> EditPostRoute(route.postId)
        is Routes.NewComment -> NewCommentRoute(route.postId)
        is Routes.NewCommentReply -> NewCommentReplyRoute(
            route.postId,
            route.commentId,
            route.userId
        )

        is Routes.AttachmentsViewer -> AttachmentViewerRoute(route.attachments, route.viewPosition)
        is Routes.Followers -> FollowersRoute(route.userId, route.startTabIsFollowing)
        is Routes.Notifications -> NotificationsRoute(route.showBackButton)
        is Routes.BannerEdit -> BannerEditRoute()
        is Routes.CrashReports -> CrashReportsRoute()
        is Routes.Post -> PostRoute(route.postId)
        is Routes.Search -> SearchRoute()
        is Routes.HashtagFeed -> HashtagFeedRoute(route.hashtagName)
        is Routes.Settings -> SettingsRoute()
        is Routes.SettingsTheme -> SettingsThemeRoute()
    }
}

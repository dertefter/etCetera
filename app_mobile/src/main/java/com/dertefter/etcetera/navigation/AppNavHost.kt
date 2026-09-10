package com.dertefter.etcetera.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.dertefter.attachment_viewer.AttachmentViewerRoute
import com.dertefter.auth.AuthRoute
import com.dertefter.banner_edit.BannerEditRoute
import com.dertefter.comments.CommentsRoute
import com.dertefter.crash_reports.CrashReportsRoute
import com.dertefter.feed.FeedRoute
import com.dertefter.followers.FollowersRoute
import com.dertefter.hashtag_feed.HashtagFeedRoute
import com.dertefter.navigation.Routes
import com.dertefter.new_post.EditPostRoute
import com.dertefter.new_post.NewCommentReplyRoute
import com.dertefter.new_post.NewCommentRoute
import com.dertefter.new_post.NewPostRoute
import com.dertefter.new_post.RepostRoute
import com.dertefter.notifications.NotificationsRoute
import com.dertefter.post.PostRoute
import com.dertefter.search.SearchRoute
import com.dertefter.settings.SettingsRoute
import com.dertefter.settings_about.SettingsAboutRoute
import com.dertefter.settings_account.SettingsAccountRoute
import com.dertefter.settings_privacy.SettingsPrivacyRoute
import com.dertefter.settings_security.SettingsSecurityRoute
import com.dertefter.settings_theme.EmojiAvatarsRoute
import com.dertefter.settings_theme.PostsThemeRoute
import com.dertefter.settings_theme.SettingsThemeRoute
import com.dertefter.switch_account.SwitchAccountRoute
import com.dertefter.user.UserRoute
import kotlin.math.roundToInt


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier, entries: List<NavEntry<NavKey>>, onBack: () -> Unit
) {

    val ms = MaterialTheme.motionScheme

    fun sharedAxisXTransitionSpec(): ContentTransform = ContentTransform(
        slideInHorizontally(
            animationSpec = ms.defaultSpatialSpec(),
            initialOffsetX = { (0.1f * it).roundToInt() },
        ) + fadeIn(
            animationSpec = ms.slowEffectsSpec(),
        ),
        slideOutHorizontally(
            animationSpec = ms.slowEffectsSpec(),
            targetOffsetX = { (-0.1f * it).roundToInt() },
        ) + fadeOut(
            animationSpec = ms.slowEffectsSpec(),
        ),
    )


    fun sharedAxisXPopTransitionSpec(): ContentTransform = ContentTransform(
        slideInHorizontally(
            animationSpec = ms.defaultSpatialSpec(),
            initialOffsetX = { (-0.1f * it).roundToInt() },
        ) + fadeIn(
            animationSpec = ms.fastEffectsSpec(),
        ),
        slideOutHorizontally(
            animationSpec = ms.defaultSpatialSpec(),
            targetOffsetX = { (0.1f * it).roundToInt() },
        ) + fadeOut(
            animationSpec = ms.fastEffectsSpec(),
        ),
    )

    NavDisplay(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        entries = entries,
        onBack = onBack,
        transitionSpec = { sharedAxisXTransitionSpec() },
        popTransitionSpec = { sharedAxisXPopTransitionSpec() },
        predictivePopTransitionSpec = { sharedAxisXPopTransitionSpec() },
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
            route.postId, route.commentId, route.userId
        )

        is Routes.AttachmentsViewer -> AttachmentViewerRoute(route.attachments, route.viewPosition)
        is Routes.Followers -> FollowersRoute(route.userId, route.startTabIsFollowing)
        is Routes.Notifications -> NotificationsRoute(route.showBackButton)
        is Routes.BannerEdit -> BannerEditRoute()
        is Routes.CrashReports -> CrashReportsRoute()
        is Routes.Post -> PostRoute(route.postId)
        is Routes.Search -> SearchRoute()
        is Routes.HashtagFeed -> HashtagFeedRoute(route.hashtagName)
        is Routes.SwitchAccount -> SwitchAccountRoute()
        is Routes.Settings -> SettingsRoute()
        is Routes.SettingsTheme -> SettingsThemeRoute()
        is Routes.SettingsThemeAvatars -> EmojiAvatarsRoute()
        is Routes.SettingsThemePosts -> PostsThemeRoute()
        is Routes.SettingsAccount -> SettingsAccountRoute()
        is Routes.SettingsSecurity -> SettingsSecurityRoute()
        is Routes.SettingsPrivacy -> SettingsPrivacyRoute()
        is Routes.SettingsAbout -> SettingsAboutRoute()

    }
}

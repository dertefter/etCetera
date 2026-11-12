package com.dertefter.etcetera.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.dertefter.attachment_viewer.AttachmentViewerRoute
import com.dertefter.auth.AuthRoute
import com.dertefter.banner_edit.BannerEditRoute
import com.dertefter.comments.CommentsRoute
import com.dertefter.crash_reports.CrashReportsRoute
import com.dertefter.edbi_users.EdbiUsers
import com.dertefter.feed.FeedRoute
import com.dertefter.followers.FollowersRoute
import com.dertefter.hashtag_feed.HashtagFeedRoute
import com.dertefter.navigation.Routes
import com.dertefter.new_post.EditPostRoute
import com.dertefter.new_post.EditCommentRoute
import com.dertefter.new_post.NewCommentReplyRoute
import com.dertefter.new_post.NewCommentRoute
import com.dertefter.new_post.NewPostRoute
import com.dertefter.new_post.RepostRoute
import com.dertefter.notifications.NotificationsRoute
import com.dertefter.post.PostRoute
import com.dertefter.report.ReportRoute
import com.dertefter.search.SearchRoute
import com.dertefter.settings.SettingsRoute
import com.dertefter.settings_about.SettingsAboutRoute
import com.dertefter.settings_account.SettingsAccountRoute
import com.dertefter.settings_privacy.SettingsPrivacyRoute
import com.dertefter.settings_security.SettingsSecurityRoute
import com.dertefter.settings_theme.SettingsThemeRoute
import com.dertefter.switch_account.SwitchAccountRoute
import com.dertefter.user.UserByUsernameRoute
import com.dertefter.user.UserRoute
import com.dertefter.event_viewer.EventViewerRoute
import com.dertefter.onboarding.OnBoardingRoute
import com.dertefter.pin_changer.PinChangerRoute
import com.dertefter.settings_ai.SettingsAiRoute
import com.dertefter.settings_oss.OssRoute
import com.dertefter.design.transition.LocalSharedAnimatedVisibilityScope
import com.dertefter.design.transition.LocalSharedTransitionScope
import kotlin.math.roundToInt


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
        ,
    entries: List<NavEntry<NavKey>>,
    onBack: () -> Unit
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
            animationSpec = ms.slowEffectsSpec(),
            initialOffsetX = { (-0.1f * it).roundToInt() },
        ) + fadeIn(
            animationSpec = ms.slowEffectsSpec(),
        ),
        slideOutHorizontally(
            animationSpec = ms.fastEffectsSpec(),
            targetOffsetX = { (0.1f * it).roundToInt() },
        ) + fadeOut(
            animationSpec = ms.fastEffectsSpec(),
        ),
    )

    SharedTransitionLayout {
        val sharedTransitionScope = this
        CompositionLocalProvider(LocalSharedTransitionScope provides sharedTransitionScope) {
            NavDisplay(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
                entries = entries,
                onBack = onBack,
                transitionSpec = { sharedAxisXTransitionSpec() },
                popTransitionSpec = { sharedAxisXPopTransitionSpec() },
                predictivePopTransitionSpec = { sharedAxisXPopTransitionSpec() },
            )
        }
    }
}

@Composable
fun RouteContent(route: Routes) {
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    CompositionLocalProvider(
        LocalSharedAnimatedVisibilityScope provides animatedVisibilityScope
    ) {
        when (route) {
            is Routes.Oss -> OssRoute()
            is Routes.OnBoarding -> OnBoardingRoute()
            is Routes.Auth -> AuthRoute()
            is Routes.Feed -> FeedRoute()
            is Routes.Comments -> CommentsRoute(route.postId)
            is Routes.User -> UserRoute(route.userId, route.showBackButton)
            is Routes.UserByUsername -> UserByUsernameRoute(route.username, route.showBackButton)
            is Routes.NewPost -> NewPostRoute(route.wallRecipientId)
            is Routes.Repost -> RepostRoute(route.postIdForRepost, route.wallRecipientId)
            is Routes.EditPost -> EditPostRoute(route.postId)
            is Routes.NewComment -> NewCommentRoute(route.postId)
            is Routes.NewCommentReply -> NewCommentReplyRoute(route.postId, route.commentId, route.userId)
            is Routes.EditComment -> EditCommentRoute(route.postId, route.commentId)
            is Routes.AttachmentsViewer -> AttachmentViewerRoute(route.attachments, route.viewPosition)
            is Routes.Followers -> FollowersRoute(route.userId, route.startTabIsFollowing)
            is Routes.Notifications -> NotificationsRoute(route.showBackButton)
            is Routes.BannerEdit -> BannerEditRoute()
            is Routes.EdbiUsers -> EdbiUsers(route.clan)
            is Routes.CrashReports -> CrashReportsRoute()
            is Routes.Post -> PostRoute(route.postId)
            is Routes.Search -> SearchRoute()
            is Routes.HashtagFeed -> HashtagFeedRoute(route.hashtagName)
            is Routes.SwitchAccount -> SwitchAccountRoute()
            is Routes.Settings -> SettingsRoute()
            is Routes.SettingsAi -> SettingsAiRoute()
            is Routes.SettingsTheme -> SettingsThemeRoute()
            is Routes.SettingsAccount -> SettingsAccountRoute()
            is Routes.SettingsSecurity -> SettingsSecurityRoute()
            is Routes.SettingsPrivacy -> SettingsPrivacyRoute()
            is Routes.SettingsAbout -> SettingsAboutRoute()
            is Routes.Report -> ReportRoute(route.targetType, route.targetId)
            is Routes.EventViewer -> EventViewerRoute()
            is Routes.PinChanger -> PinChangerRoute()
        }
    }
}

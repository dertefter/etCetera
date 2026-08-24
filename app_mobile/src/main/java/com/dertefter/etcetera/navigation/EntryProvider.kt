package com.dertefter.etcetera.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.dertefter.navigation.Routes

fun getAppEntryProvider(): (NavKey) -> NavEntry<NavKey> = entryProvider {
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
    entry<Routes.SettingsAccount> { RouteContent(it) }
    entry<Routes.SwitchAccount> { RouteContent(it) }
}

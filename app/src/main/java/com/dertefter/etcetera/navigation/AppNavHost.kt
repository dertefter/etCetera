package com.dertefter.etcetera.navigation

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
import com.dertefter.feed.FeedRoute
import com.dertefter.followers.FollowersRoute
import com.dertefter.navigation.Routes
import com.dertefter.new_post.NewPostRoute
import com.dertefter.notifications.NotificationsRoute
import com.dertefter.user.UserRoute


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Routes
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
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
        is Routes.NewPost -> NewPostRoute(route.wallRecipientId)
        is Routes.Followers -> FollowersRoute()
        is Routes.Notifications -> NotificationsRoute()
        is Routes.BannerEdit -> BannerEditRoute()
    }
}

fun NavGraphBuilder.graph() {
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

    composable<Routes.Followers> {
        val args = it.toRoute<Routes.Followers>()
        RouteContent(args)
    }

}

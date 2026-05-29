package com.dertefter.etcetera.navigation

import androidx.compose.runtime.Composable
import com.dertefter.design.icons.Icons
import com.dertefter.navigation.Routes

@Composable
fun getNavigationMenu(isAuthorized: Boolean): List<TabRouteItem> {
    return if (isAuthorized){
        listOf(
            TabRouteItem(
                label = "Лента",
                startDestination = Routes.Feed,
                icon = { Icons.Home },
                tab = Routes.Tab1
            ),

            TabRouteItem(
                label = "Уведомления",
                startDestination = Routes.Notifications,
                icon = { Icons.Notifications },
                tab = Routes.Tab3
            ),

            TabRouteItem(
                label = "Профиль",
                startDestination = Routes.User(null),
                icon = { Icons.User },
                tab = Routes.Tab2

            ),



        )

    } else {
        listOf(
            TabRouteItem(
                label = "Авторизация",
                startDestination = Routes.Auth,
                icon = { Icons.Home },
                tab = Routes.Tab1
            ),

        )

    }
}

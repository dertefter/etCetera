package com.dertefter.etcetera.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
import com.dertefter.comments.CommentsRoute
import com.dertefter.design.components.common.TransformingListItem
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.spacing
import com.dertefter.feed.FeedRoute
import com.dertefter.followers.FollowersRoute
import com.dertefter.hashtag_feed.HashtagFeedRoute
import com.dertefter.navigation.Routes
import com.dertefter.user.UserRoute

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>
) {
    val strategy = rememberSwipeDismissableSceneStrategy<NavKey>()

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        sceneStrategies = listOf(strategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Routes.Feed> {
                FeedRoute()
            }

            entry<Routes.Auth> {

                val transformationSpec = rememberTransformationSpec()

                ScreenScaffold() { contentPadding ->

                    TransformingLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = contentPadding,
                        verticalArrangement = Arrangement.spacedBy(
                            MaterialTheme.spacing.medium,
                            Alignment.CenterVertically
                        ),
                    ) {
                        item{
                            TransformingListItem(
                                transformationSpec = transformationSpec,
                            ){
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Explore,
                                    contentDescription = null,
                                )
                            }
                        }

                        item{
                            TransformingListItem(
                                transformationSpec = transformationSpec,
                            ){
                                Text(
                                    text = "Вход не выполнен",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        item{
                            TransformingListItem(
                                transformationSpec = transformationSpec,
                            ){
                                Text(
                                    text = "Войдите в приложение на мобильном устройстве",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                    }

                }
            }
            entry<Routes.Search> { }
            entry<Routes.CrashReports> { }
            entry<Routes.Notifications> { }
            entry<Routes.BannerEdit> { }
            entry<Routes.NewPost> { }
            entry<Routes.NewComment> { }
            entry<Routes.NewCommentReply> { }
            entry<Routes.Followers> { route ->
                FollowersRoute(route)
            }
            entry<Routes.Post> { }
            entry<Routes.HashtagFeed> { route ->
                HashtagFeedRoute(
                    hashtagName = route.hashtagName
                )
            }
            entry<Routes.User> { route ->
                UserRoute(
                    userId = route.userId
                )
            }
            entry<Routes.Comments> { route ->
                CommentsRoute(
                    postId = route.postId
                )
            }

            entry<Routes.AttachmentsViewer> { }
        }
    )
}

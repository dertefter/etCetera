package com.dertefter.pin_changer.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.me.Me
import com.dertefter.data.dto.user.VisibilityDto
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.pin_changer.R
import com.dertefter.pin_changer.presentation.components.PinItem

@Composable
fun PinChangerScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    val lazyColumnState = rememberLazyListState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Text(
                text = stringResource(R.string.settings_account_pins_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.defaultScreenPadding)
                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
            )
        }
    )
    { contentPadding ->

        LazyColumn(
            state = lazyColumnState,
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            contentPadding = contentPadding
                    + PaddingValues(bottom = MaterialTheme.bottomNavHeight)
                    + PaddingValues(vertical = MaterialTheme.spacing.medium)
                    + PaddingValues(horizontal = MaterialTheme.spacing.defaultScreenPadding)
        ) {

            item {
                SegmentedColumn{
                    item(
                        itemInnerPadding = PaddingValues(),
                        onClick = {
                            onEvent(Event.OnPinChange(null))
                        }
                    ) {
                        PinItem(
                            selected = uiState.me?.pin == null
                        )
                    }

                    uiState.pins.forEach { pin ->
                        item(
                            itemInnerPadding = PaddingValues(),
                            onClick = {
                                onEvent(Event.OnPinChange(pin.slug))
                            }
                        ) {
                            PinItem(
                                pin = pin,
                                selected = uiState.me?.pin?.slug == pin.slug
                            )
                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = false)
@Composable
fun PinChangerScreenPreview() {
    AppTheme {
        PinChangerScreen(
            uiState = UiState(
                me = Me(
                    clanAvatar = "🦐",
                    avatar = "🦐",
                    banner = null,
                    bio = "Bio",
                    createdAt = "",
                    displayName = "Display Name",
                    followersCount = 0,
                    followingCount = 0,
                    id = "id",
                    isPhoneVerified = false,
                    isPrivate = false,
                    likesVisibility = VisibilityDto.EVERYONE,
                    pin = null,
                    postsCount = 0,
                    subscription = null,
                    username = "username",
                    verified = false,
                    wallAccess = VisibilityDto.EVERYONE
                ),
                isLoading = false,
                pins = emptyList()
            ),
            onEvent = {}
        )
    }
}

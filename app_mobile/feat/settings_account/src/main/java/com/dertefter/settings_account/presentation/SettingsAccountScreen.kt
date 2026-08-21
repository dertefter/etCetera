package com.dertefter.settings_account.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dertefter.data.dto.me.MeDto
import com.dertefter.data.dto.user.VisibilityDto
import com.dertefter.design.components.avatar.EmojiAvatar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.lists.SegmentedColumn
import com.dertefter.design.components.text_fields.TextFieldItem
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_account.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsAccountScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_account_title))
                },
                navigationIcon = {
                    AppNavigationIcon(
                        icon = Icons.ArrowBack,
                        onClick = { onEvent(Event.OnNavigateBack) },
                        contentDescription = stringResource(com.dertefter.design.R.string.design_back_content_desc)
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.canSave,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = { onEvent(Event.OnSave) }
                ) {
                    Icon(
                        Icons.Save,
                        contentDescription = stringResource(R.string.settings_account_save)
                    )
                }
            }
        }


    ) { contentPadding ->

        val contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + MaterialTheme.spacing.medium,
            bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.medium,
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) + MaterialTheme.spacing.defaultScreenPadding,
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current) + MaterialTheme.spacing.defaultScreenPadding
        )


        uiState.me?.let { me ->
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                contentPadding = contentPadding
            ) {

                item{
                    SegmentedColumn(
                        title = stringResource(R.string.settings_account_section_title)
                    ){
                        item{
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Column(
                                    modifier = Modifier.weight(1f),
                                ){
                                    Text(
                                        text = stringResource(R.string.settings_account_emoji_clan_title)
                                    )
                                    Text(
                                        text = stringResource(R.string.settings_account_emoji_clan_desc),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                                EmojiAvatar(
                                    emoji = me.avatar,
                                    containerSize = 44.dp,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        item(
                            itemInnerPadding = PaddingValues()
                        ){
                            TextFieldItem(
                                value = uiState.displayNameInput,
                                hint = stringResource(R.string.settings_account_display_name_hint),
                                onValueChange = { onEvent(Event.OnDisplayNameChange(it)) }
                            )
                        }

                        item(
                            itemInnerPadding = PaddingValues()
                        ){
                            TextFieldItem(
                                value = uiState.usernameInput,
                                hint = stringResource(R.string.settings_account_username_hint),
                                onValueChange = { onEvent(Event.OnUsernameChange(it)) }
                            )
                        }

                        item(
                            itemInnerPadding = PaddingValues()
                        ){
                            TextFieldItem(
                                value = uiState.bioInput,
                                hint = stringResource(R.string.settings_account_bio_hint),
                                onValueChange = { onEvent(Event.OnBioChange(it)) },
                                singleLine = false
                            )
                        }



                    }
                }

            }
        }



    }
}

@Preview(showBackground = true)
@Composable
fun SettingsAccountScreenPreview() {
    AppTheme {
        SettingsAccountScreen(
            uiState = UiState(
                me = MeDto(
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
                displayNameInput = "Display Name",
                usernameInput = "username",
                bioInput = "Bio"
            ),
            onEvent = {}
        )
    }
}




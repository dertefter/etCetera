package com.dertefter.switch_account.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.lists.SegmentedContentItem
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.switch_account.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwitchAccountScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Text(
            text = stringResource(R.string.switch_acount_select_account),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
        )
        uiState.loginHistory.forEachIndexed { index, login ->
            val isCurrent = login == uiState.currentLogin
            SegmentedContentItem(
                index = index,
                count = uiState.loginHistory.count(),
                contentPadding = PaddingValues(all = 0.dp),
                onClick = {
                    onEvent(
                        Event.OnSwitchAccount(login)
                    )
                }
            ){

                val containerColor by animateColorAsState(
                    if (isCurrent) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f)
                    }
                )

                val contentColor by animateColorAsState(
                    if (isCurrent) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Row(
                    modifier = Modifier
                        .background(containerColor)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = login,
                        color = contentColor,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .padding(MaterialTheme.spacing.extraLarge)
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    AnimatedVisibility(
                        visible = !isCurrent
                    ) {
                        IconButton(
                            onClick = {
                                onEvent(Event.OnRemoveAccountFromHistory(login))
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Delete,
                                contentDescription = stringResource(R.string.switch_acount_remove_from_history),
                            )
                        }
                    }


                }

            }
        }

        FilledTonalButton(
            onClick = {
                onEvent(Event.OnAddAccount)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Add, contentDescription = null)
            Spacer(Modifier.width(MaterialTheme.spacing.small))
            Text(stringResource(R.string.switch_acount_add_account))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SwitchAccountScreenPreview() {
    AppTheme {
        SwitchAccountScreen(
            uiState = UiState(
                loginHistory = listOf("user1", "user2", "user3"),
                currentLogin = "user1"
            ),
            onEvent = {}
        )
    }
}

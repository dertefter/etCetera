package com.dertefter.auth.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.auth.R
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(onEvent: (Event) -> Unit, uiState: UiState) {

    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
    val uriHandler = LocalUriHandler.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    if (uiState.isTurnstileVisible) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(Event.OnDismissTurnstile) },
            sheetState = sheetState
        ) {
            TurnstileWebView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                onTokenReceived = { token ->
                    onEvent(Event.OnTurnstileTokenReceived(token))
                }
            )
        }
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(R.string.auth_login_title))
                },
                subtitle = {
                    Text(stringResource(R.string.auth_login_subtitle))
                },
                scrollBehavior = scrollBehavior
            )

        },
        floatingActionButtonPosition = FabPosition.Center
    )
    { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                modifier = Modifier
                    .padding(bottom = 140.dp)
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = uiState.login,
                    onValueChange = { onEvent(Event.OnLoginChanged(it)) },
                    label = { Text(stringResource(R.string.auth_login_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    isError = !uiState.isLoginValid,
                    supportingText = {
                        AnimatedVisibility(
                            visible = !uiState.isLoginValid
                        ) {
                            Text(text = stringResource(R.string.auth_invalid_email))
                        }
                    },
                    shape = MaterialTheme.shapes.large
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { onEvent(Event.OnPasswordChanged(it)) },
                    label = { Text(stringResource(R.string.auth_password_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { onEvent(Event.OnTogglePasswordVisibility) }) {
                            Icon(
                                imageVector = if (uiState.isPasswordVisible) Icons.VisibilityOff else Icons.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    enabled = !uiState.isLoading,
                    shape = MaterialTheme.shapes.large
                )

                AnimatedVisibility(uiState.error != null) {
                    Column {
                        Text(
                            color = MaterialTheme.colorScheme.error,
                            text = stringResource(R.string.auth_error_login_failed),
                            style = MaterialTheme.typography.labelLargeEmphasized,
                            modifier = Modifier
                                .padding(
                                    horizontal = MaterialTheme.rounding.large,
                                    vertical = MaterialTheme.spacing.small
                                )
                                .fillMaxWidth()
                        )
                        uiState.error?.message?.let{ message ->
                            Text(
                                color = MaterialTheme.colorScheme.error,
                                text = message,
                                style = MaterialTheme.typography.labelLargeEmphasized,
                                modifier = Modifier
                                    .padding(
                                        horizontal = MaterialTheme.rounding.large,
                                        vertical = MaterialTheme.spacing.small
                                    )
                                    .fillMaxWidth()
                            )
                        }
                    }

                }

                TextButton(
                    onClick = { uriHandler.openUri("https://итд.com/forgot-password") },
                    modifier = Modifier
                        .align(Alignment.End),
                ) {
                    Text(stringResource(R.string.auth_forgot_password))
                }

                Button(
                    onClick = { onEvent(Event.OnSubmit) },
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.extraLarge)
                        .widthIn(max = 400.dp),
                    enabled = uiState.login.isNotBlank() && uiState.password.isNotBlank() && uiState.isLoginValid,
                    shape = CircleShape
                ) {
                    AnimatedContent(
                        targetState = uiState.isLoading,
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.spacing.small),
                        label = ""
                    ) { isLoading ->
                        if (isLoading) {
                            AppLoadingIndicator(
                                modifier = Modifier.size(40.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.auth_login_button),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                textAlign = TextAlign.Center
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
fun AuthScreenPreview2() {
    AppTheme {
        AuthScreen(
            onEvent = {},
            uiState = UiState(isLoading = false,
                login = "1",
                password = "f"
            )
        )
    }
}


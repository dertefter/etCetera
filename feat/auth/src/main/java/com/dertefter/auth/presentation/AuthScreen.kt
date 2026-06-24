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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalWindowInfo
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
import com.dertefter.design.theme.circleShape
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(onEvent: (Event) -> Unit, uiState: UiState) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
    val uriHandler = LocalUriHandler.current

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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            //TextButton(
            //    onClick = { uriHandler.openUri("https://итд.com/register") },
            //    modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraLarge)
            //) {
            //    Text("Нет аккаунта? Создайте его на сайте ИТД!")
           // }
        },
        floatingActionButtonPosition = FabPosition.Center
    )
    { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
            contentPadding = contentPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    modifier = Modifier
                        .padding(top = LocalWindowInfo.current.containerDpSize.height / 5)
                        .widthIn(max = 400.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Авторизация",
                        style = MaterialTheme.typography.headlineMediumEmphasized,
                        modifier = Modifier
                            .padding(
                            horizontal = MaterialTheme.spacing.large,
                        )
                    )

                    Text(
                        text = "Войдите в аккаунт ИТД",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(
                            horizontal = MaterialTheme.spacing.large
                            )
                            .padding(bottom = MaterialTheme.spacing.large)
                    )


                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        OutlinedTextField(
                            value = uiState.login,
                            onValueChange = { onEvent(Event.OnLoginChanged(it)) },
                            label = { Text(stringResource(R.string.auth_login_hint)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !uiState.isLoading,
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
                    }

                    TextButton(
                        onClick = { uriHandler.openUri("https://итд.com/forgot-password") },
                        modifier = Modifier
                            .align(Alignment.End),
                    ) {
                        Text("Забыли пароль?")
                    }


                }
            }

            item {
                AnimatedVisibility(uiState.error != null) {
                    Column() {
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
            }

            item {
                Button(
                    onClick = { onEvent(Event.OnSubmit) },
                    modifier = Modifier
                        .widthIn(max = 400.dp),
                    enabled = uiState.login.isNotBlank() && uiState.password.isNotBlank(),
                    shape = MaterialTheme.circleShape()
                ) {
                    AnimatedContent(
                        targetState = uiState.isLoading,
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small),
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
            uiState = UiState(isLoading = true,
                login = "f",
                password = "f"
            )
        )
    }
}


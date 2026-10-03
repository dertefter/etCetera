package com.dertefter.design.components.text_fields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLengthTrim
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.WearableTheme

@Composable
fun TextFieldItem(
    modifier: Modifier = Modifier,
    value: String,
    enabled: Boolean = true,
    hint: String,
    icon: ImageVector? = null,
    onValueChange: (String) -> Unit = {},
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxSymbols: Int = Int.MAX_VALUE,
    singleLine: Boolean = true,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
) {
    val textFieldState = rememberTextFieldState(initialText = value)

    val containerColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer
    } else if (!enabled) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    val contentColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else if (!enabled) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.primary
    }

    LaunchedEffect(value) {
        if (value != textFieldState.text.toString()) {
            textFieldState.setTextAndPlaceCursorAtEnd(value)
        }
    }

    LaunchedEffect(textFieldState.text) {
        val newText = textFieldState.text.toString()
        if (newText != value) {
            onValueChange(newText)
        }
    }

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        AnimatedVisibility(
            visible = value.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = hint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.7f
                        )
                    )
                }

                val inputTransformation = if (maxSymbols < Int.MAX_VALUE) {
                    InputTransformation.maxLengthTrim(maxSymbols)
                } else null

                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = textFieldState,
                    enabled = enabled,
                    lineLimits = if (singleLine) TextFieldLineLimits.SingleLine else TextFieldLineLimits.Default,
                    inputTransformation = inputTransformation,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    cursorBrush = SolidColor(contentColor),
                    keyboardOptions = keyboardOptions
                )
            }
            if (trailingIcon != null) {
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = onTrailingIconClick ?: {}
                ) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldItemPreview() {
    WearableTheme {
        TextFieldItem(
            value = "",
            hint = "Full Name",
            icon = Icons.User,
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldItemWithValuePreview() {
    WearableTheme {
        TextFieldItem(
            value = "Ivan Ivanov",
            hint = "Full Name",
            icon = Icons.User,
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldItemWithValuePreview2() {
    WearableTheme {
        TextFieldItem(
            value = "Ivan Ivanov",
            hint = "Full Name",
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextFieldItemWithErrorPreview() {
    WearableTheme {
        TextFieldItem(
            value = "Invalid Inputя",
            hint = "Email",
            icon = Icons.User,
            isError = true
        )
    }
}

package com.dertefter.design.components.poll

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme

@Composable
fun NewPollQuestion(
    modifier: Modifier = Modifier,
    text: String,
    onRemove: () -> Unit,
    onTextChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChanged,
        modifier = modifier
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = {
            Text(
                "Вариант ответа...",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Delete,
                contentDescription = null,
                modifier = Modifier.clickable { onRemove() }
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.tertiary,
            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
            focusedTrailingIconColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onTertiary,
            cursorColor = MaterialTheme.colorScheme.onTertiary,
        ),
        shape = MaterialTheme.shapes.small
    )
}

@Preview(showBackground = true)
@Composable
private fun NewPollQuestionPreview() {
    AppTheme {
        NewPollQuestion(
            text = "Вариант ответаfffffffffffffffffffffffffffffffffffffffffffffff",
            onRemove = {},
            onTextChanged = {}
        )
    }
}



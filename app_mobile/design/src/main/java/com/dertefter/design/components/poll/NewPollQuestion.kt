package com.dertefter.design.components.poll

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPollQuestion(
    modifier: Modifier = Modifier,
    text: String,
    onRemove: () -> Unit,
    onTextChanged: (String) -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = text,
        onValueChange = onTextChanged,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary)
            .height(48.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onTertiary),
        singleLine = true,
        interactionSource = interactionSource
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = text,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            placeholder = { Text(
                "Вариант ответа...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)
            ) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.clickable { onRemove() }
                )
            },
            contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                top = 0.dp,
                bottom = 0.dp
            ),
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    shape = MaterialTheme.shapes.medium,
                )
            }
        )
    }
}

package com.dertefter.user.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.user.R

@Composable
fun BioCard(
    modifier: Modifier = Modifier,
    bio: String,
    canEdit: Boolean,
    onSaveClick: (String) -> Unit = {}
) {
    val textFieldState = rememberTextFieldState(initialText = bio)

    LaunchedEffect(bio) {
        if (bio != textFieldState.text.toString()) {
            textFieldState.setTextAndPlaceCursorAtEnd(bio)
        }
    }

    var isFocused by remember { mutableStateOf(false) }
    val currentBio = textFieldState.text.toString()
    val isChanged = currentBio != bio && canEdit

    val isEditing = isFocused

    val bgColor by animateColorAsState(
        if (isEditing) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent
    )

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = bgColor,
        border = BorderStroke(
            color = MaterialTheme.colorScheme.outlineVariant,
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            BasicTextField(
                state = textFieldState,
                modifier = Modifier
                    .onFocusChanged { isFocused = it.isFocused }
                    .padding(top = MaterialTheme.spacing.extraLarge)
                    .padding(horizontal = MaterialTheme.spacing.extraLarge)
                    .padding(bottom = if (isChanged) 0.dp else MaterialTheme.spacing.extraLarge)
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                readOnly = !canEdit
            )

            AnimatedVisibility(
                visible = isChanged,
                enter = fadeIn()
            ) {
                Row(
                    modifier = Modifier
                        .padding(all = MaterialTheme.spacing.medium)
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    FilledTonalButton(
                        onClick = { textFieldState.setTextAndPlaceCursorAtEnd(bio) }
                    ) {
                        Text(stringResource(R.string.user_cancel_bio))
                    }

                    Button(
                        onClick = { onSaveClick(currentBio) }
                    ) {
                        Text(stringResource(R.string.user_save_bio))
                    }
                }
            }

        }

    }
}

@Preview
@Composable
private fun BioCardPreview() {
    AppTheme(
        content = {
            BioCard(
                bio = "This is a sample bio text for the BioCard component. It should show how the text is rendered within the card.",
                canEdit = true
            )
        },
    )
}

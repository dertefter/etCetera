package com.dertefter.design.components.poll

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.R
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing







@Composable
fun NewPollCard(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChanged: (title: String) -> Unit = {},
    questions: List<NewPollOptionUiModel>,
    isMultipleChoice: Boolean = false,
    onMultipleChoiceChanged: (Boolean) -> Unit = {},
    onNewQuestion: () -> Unit = {},
    onRemoveQuestion: (id: String) -> Unit = {},
    onChangeQuestion: (id: String, text: String) -> Unit = { _, _ -> }
){
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .animateContentSize()
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ){
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChanged,
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.medium)
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleLargeEmphasized,
            placeholder = {
                Text(
                    text = stringResource(R.string.design_new_poll_title_placeholder),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
            },
            singleLine = false,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                cursorColor = MaterialTheme.colorScheme.primary,
            )
        )

        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.medium)
                .animateContentSize()
                .clip(MaterialTheme.shapes.largeIncreased)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            for (q in questions){
                key(q.id) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        NewPollQuestion(
                            text = q.text,
                            onRemove = { onRemoveQuestion(q.id) },
                            onTextChanged = { onChangeQuestion(q.id, it) }
                        )
                    }
                }
            }
        }


        AnimatedVisibility(
            visible = questions.size < 10,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            TextButton(
                onClick = onNewQuestion,
                colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.extraLarge)
                    .padding(top = MaterialTheme.spacing.small)
            ) {
                Icon(
                    imageVector = Icons.Add,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.small),
                    text = stringResource(R.string.design_new_poll_add_option)
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.extraLarge)
                .padding(bottom = MaterialTheme.spacing.extraLarge)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable { onMultipleChoiceChanged(!isMultipleChoice) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            Checkbox(
                checked = isMultipleChoice,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.tertiary,
                    uncheckedColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    checkmarkColor = MaterialTheme.colorScheme.onTertiary
                )
            )
            Text(
                text = stringResource(R.string.design_new_poll_multiple_choice),
                style = MaterialTheme.typography.bodyMediumEmphasized,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Preview(showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NewPollCardPreview() {
    AppTheme {
        NewPollCard(
            title = "Sample Poll",
            questions = listOf(
                NewPollOptionUiModel("Option 1", "1"),
                NewPollOptionUiModel("Option 2", "2"),
                NewPollOptionUiModel("", "3")
            ),
            isMultipleChoice = false
        )
    }
}

@Preview(showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NewPollCardPreview2() {
    AppTheme {
        NewPollCard(
            title = "Sample Poll",
            questions = listOf(
                NewPollOptionUiModel("Option 1", "1"),
                NewPollOptionUiModel("Option 2", "2"),
                NewPollOptionUiModel("", "3")
            ),
            isMultipleChoice = true
        )
    }
}


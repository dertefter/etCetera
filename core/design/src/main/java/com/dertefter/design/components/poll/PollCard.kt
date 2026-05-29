package com.dertefter.design.components.poll

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.R
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun PollCard(
    modifier: Modifier = Modifier,
    title: String,
    options: List<PollOptionUiModel>,
    isMultipleChoice: Boolean,
    totalCount: Int,
    isVoted: Boolean = options.any { it.isChecked },
    onVote: (optionIds: List<String>) -> Unit
){
    var isEditMode by remember(isVoted) { mutableStateOf(!isVoted) }
    var selectedOptionIds by remember(options) {
        mutableStateOf(options.filter { it.isChecked }.map { it.id }.toSet())
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .animateContentSize()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ){
        Text(
            text = title,
            modifier = Modifier
                .padding(all = MaterialTheme.spacing.extraLarge),
            style = MaterialTheme.typography.titleLargeEmphasized
        )

        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.medium)
                .animateContentSize()
                .clip(MaterialTheme.shapes.largeIncreased)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            for (option in options){
                val percent = if (isEditMode || !isVoted) null else {
                    if (totalCount > 0) option.votesCount.toFloat() / totalCount else 0f
                }
                PollOption(
                    text = option.text,
                    percent = percent,
                    isChecked = selectedOptionIds.contains(option.id),
                    onClick = {
                        if (isEditMode) {
                            if (isMultipleChoice) {
                                selectedOptionIds = if (selectedOptionIds.contains(option.id)) {
                                    selectedOptionIds - option.id
                                } else {
                                    selectedOptionIds + option.id
                                }
                            } else {
                                selectedOptionIds = setOf(option.id)
                            }
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (isVoted) {
                Text(
                    text = if (!isEditMode) {
                        stringResource(R.string.design_poll_total_votes, totalCount)
                    } else if (!isMultipleChoice) {
                        stringResource(R.string.design_poll_select_one)
                    } else {
                        stringResource(R.string.design_poll_select_multiple)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = MaterialTheme.spacing.medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            if (isVoted || isEditMode) {
                TextButton(
                    onClick = {
                        if (isEditMode) {
                            if (selectedOptionIds.isNotEmpty()) {
                                onVote(selectedOptionIds.toList())
                                isEditMode = false
                            }
                        } else {
                            isEditMode = true
                        }
                    },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isEditMode) {
                        Text(stringResource(R.string.design_poll_save))
                    } else {
                        Text(stringResource(R.string.design_poll_change_vote))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PollCardPreview() {
    AppTheme {
        PollCard(
            title = "What is your favorite color?",
            options = listOf(
                PollOptionUiModel(text = "Red", id = "1", votesCount = 10, false),
                PollOptionUiModel(text = "Blue", id = "2", votesCount = 60, true),
                PollOptionUiModel(text = "Green", id = "3", votesCount = 30, false)
            ),
            isMultipleChoice = false,
            modifier = Modifier.padding(16.dp),
            totalCount = 100,
            onVote = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PollCardPreviewEdit() {
    AppTheme {
        PollCard(
            title = "What is your favorite color?",
            options = listOf(
                PollOptionUiModel(text = "Red", id = "1", votesCount = 10, false),
                PollOptionUiModel(text = "Blue", id = "2", votesCount = 60, false),
                PollOptionUiModel(text = "Green", id = "3", votesCount = 30, false)
            ),
            isMultipleChoice = false,
            modifier = Modifier.padding(16.dp),
            totalCount = 100,
            onVote = {}
        )
    }
}



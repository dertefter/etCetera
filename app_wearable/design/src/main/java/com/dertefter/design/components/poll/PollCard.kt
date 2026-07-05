package com.dertefter.design.components.poll

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.design.R
import com.dertefter.design.theme.WearableTheme
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
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .fillMaxWidth()
    ){
        Text(
            text = title,
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.extraLarge)
                .padding(top = MaterialTheme.spacing.extraLarge),
            style = MaterialTheme.typography.titleSmall
        )

        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.medium)
                .animateContentSize()
                .clip(MaterialTheme.shapes.small)
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
                            selectedOptionIds = if (isMultipleChoice) {
                                if (selectedOptionIds.contains(option.id)) {
                                    selectedOptionIds - option.id
                                } else {
                                    selectedOptionIds + option.id
                                }
                            } else {
                                setOf(option.id)
                            }
                        }
                    }
                )
            }
        }

        if (isVoted) {
            Text(
                text = if (!isEditMode) {
                    stringResource(R.string.design_poll_total_votes, totalCount)
                } else if (!isMultipleChoice) {
                    stringResource(R.string.design_poll_select_one)
                } else {
                    stringResource(R.string.design_poll_select_multiple)
                },
                style = MaterialTheme.typography.bodyExtraSmall,
                modifier = Modifier.padding(start = MaterialTheme.spacing.medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        if (isVoted || isEditMode) {
            val text = if (isEditMode) {
                stringResource(R.string.design_poll_save)
            }else{
                stringResource(R.string.design_poll_change_vote)
            }

            Text(
                text = text,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.small)
                    .padding(horizontal = MaterialTheme.spacing.large)
                    .padding(bottom = MaterialTheme.spacing.large)
                    .clickable(
                        onClick = {
                            if (isEditMode) {
                                if (selectedOptionIds.isNotEmpty()) {
                                    onVote(selectedOptionIds.toList())
                                    isEditMode = false
                                }
                            } else {
                                isEditMode = true
                            }
                        }
                    )
            )

        }
    }
}

@Preview(device = "id:wearos_small_round")
@Composable
fun PollCardPreview() {
    WearableTheme {
        PollCard(
            title = "What is your favorite color?",
            options = listOf(
                PollOptionUiModel(text = "Red", id = "1", votesCount = 10, false),
                 ),
            isMultipleChoice = false,
            totalCount = 100,
            onVote = {}
        )
    }
}



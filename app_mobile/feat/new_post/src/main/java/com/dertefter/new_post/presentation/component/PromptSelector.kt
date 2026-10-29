package com.dertefter.new_post.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.ai.prompt.Prompt
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.new_post.R

@Composable
fun PromptSelector(
    modifier: Modifier = Modifier,
    prompts: List<Prompt>,
    onPromptSelected: (Prompt) -> Unit = {},
    onClose: () -> Unit = {}
){
    FlowRow(
        modifier = modifier
            .clip(MaterialTheme.shapes.largeIncreased)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(MaterialTheme.spacing.medium)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    )
    {
        FilledIconButton(
            onClick = onClose
        ) {
            Icon(
                imageVector = Icons.Close,
                contentDescription = stringResource(R.string.action_close)
            )
        }
        prompts.forEach { prompt ->
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = { onPromptSelected(prompt) }
            ) {
                val name = when (prompt){
                    Prompt.Boomer -> R.string.prompt_boomer
                    Prompt.Millennial -> R.string.prompt_millennial
                    Prompt.Scientific -> R.string.prompt_scientific
                    Prompt.Summarize -> R.string.prompt_summarize
                    Prompt.Zoomer -> R.string.prompt_zoomer
                }
                Text(
                    text = stringResource(name),
                    modifier = Modifier.padding(
                        vertical = MaterialTheme.spacing.medium,
                        horizontal = MaterialTheme.spacing.large
                    )
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun PromptSelectorPreview() {
    AppTheme {
        PromptSelector(
            prompts = listOf(
                Prompt.Summarize,
                Prompt.Boomer,
                Prompt.Millennial,
                Prompt.Scientific,
                Prompt.Zoomer
            )
        )
    }
}
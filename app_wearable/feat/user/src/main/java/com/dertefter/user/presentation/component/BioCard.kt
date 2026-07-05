package com.dertefter.user.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing

@Composable
fun BioCard(
    modifier: Modifier = Modifier,
    bio: String,
){
    Text(
        modifier = modifier
            .border(
                width = 1.dp,
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.outline
            )
            .padding(MaterialTheme.spacing.large)
            .fillMaxWidth(),
        text = bio,
        style = MaterialTheme.typography.bodyExtraSmall
    )
}

@Preview
@Composable
private fun BioCardPreview() {
    WearableTheme() {
        BioCard(
            bio = "This is a sample bio text for the BioCard component.",
        )
    }
}

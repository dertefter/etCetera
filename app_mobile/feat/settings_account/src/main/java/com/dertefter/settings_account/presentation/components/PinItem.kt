package com.dertefter.settings_account.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dertefter.data.dto.feed.Pin
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_account.R

@Composable
fun PinItem(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    pin: Pin? = null
) {

    val contentColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    )

    val containerColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(MaterialTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {

        if (pin?.url != null){
            AsyncImage(
                model = pin.url,
                contentDescription = pin.description,
                modifier = Modifier.size(38.dp)
            )
        } else {
            Icon(
                modifier = Modifier
                    .size(38.dp),
                imageVector = Icons.Block,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.75f)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = pin?.name ?: stringResource(R.string.settings_account_without_pin),
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
            if (!pin?.description.isNullOrBlank()) {
                Text(
                    text = pin.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PinItemPreview() {
    AppTheme {
        Column {
            PinItem(
                pin = Pin(
                    slug = "sample-pin",
                    name = "Sample Pin",
                    description = "This is a sample description for pin item.",
                    url = ""
                )
            )
            PinItem(
                selected = true,
                pin = null
            )
        }

    }
}


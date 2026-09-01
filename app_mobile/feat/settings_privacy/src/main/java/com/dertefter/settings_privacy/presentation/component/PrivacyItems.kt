package com.dertefter.settings_privacy.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.user.VisibilityDto
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_privacy.R

@Composable
fun SwitchItem(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun VisibilityItem(
    title: String,
    description: String? = null,
    value: VisibilityDto,
    onValueChange: (VisibilityDto) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = false, onClick = { showDialog = true }, role = Role.Button),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = value.toDisplayString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }

    if (showDialog) {
        VisibilityDialog(
            currentValue = value,
            onDismiss = { showDialog = false },
            onValueChange = {
                onValueChange(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun VisibilityDialog(
    currentValue: VisibilityDto,
    onDismiss: () -> Unit,
    onValueChange: (VisibilityDto) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.settings_privacy_wall_access)) }, // General title or pass as param
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        text = {
            Column(Modifier.selectableGroup()) {
                VisibilityDto.entries.forEach { visibility ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (visibility == currentValue),
                                onClick = { onValueChange(visibility) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (visibility == currentValue),
                            onClick = null
                        )
                        Text(
                            text = visibility.toDisplayString(),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun VisibilityDto.toDisplayString(): String {
    return when (this) {
        VisibilityDto.EVERYONE -> stringResource(R.string.visibility_everyone)
        VisibilityDto.FOLLOWERS -> stringResource(R.string.visibility_followers)
        VisibilityDto.MUTUAL -> stringResource(R.string.visibility_mutual)
        VisibilityDto.NOBODY -> stringResource(R.string.visibility_nobody)
    }
}

@Preview(showBackground = true)
@Composable
private fun VisibilityItemPreview() {
    AppTheme {
        VisibilityItem(
            title = "Wall access",
            description = "Who can see your posts",
            value = VisibilityDto.EVERYONE,
            onValueChange = {}
        )
    }
}

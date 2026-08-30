package com.dertefter.settings_security.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.auth.AuthSessionDto
import com.dertefter.design.common.DateParser
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_security.R
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SessionDialogContent(
    session: AuthSessionDto,
    modifier: Modifier = Modifier
) {
    val osInfo = if (session.osName != null) {
        if (session.osVersion != null) "${session.osName} ${session.osVersion}" else session.osName
    } else {
        stringResource(R.string.settings_security_unknown_os)
    }

    val locationInfo = remember(session.ipCity, session.ipCountry) {
        val parts = listOfNotNull(session.ipCity, session.ipCountry)
        if (parts.isEmpty()) null else parts.joinToString(", ")
    } ?: stringResource(R.string.settings_security_unknown_location)

    val lastActiveFormatted = remember(session.lastUsedAt) {
        val instant = DateParser.parseToInstant(session.lastUsedAt)
        if (instant != null) {
            val dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("d MMMM HH:mm", Locale.getDefault())
            dateTime.format(formatter)
        } else {
            ""
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ){
        if (session.isCurrent) {
            Text(
                text =stringResource(R.string.settings_security_current_session),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.extraSmall)
            )
        }
        Text(
            text = "$osInfo · $locationInfo",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = session.ipAddress,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = stringResource(R.string.settings_security_last_active, lastActiveFormatted),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun SessionDialogContentPreview() {
    AppTheme {
        SessionDialogContent(
            session = AuthSessionDto(
                id = "1",
                isCurrent = true,
                createdAt = "2024-01-01T00:00:00Z",
                lastUsedAt = "2024-01-01T00:00:00Z",
                expiresAt = "2024-01-01T00:00:00Z",
                ipAddress = "192.168.1.1",
                ipCountry = "Russia",
                ipCity = "Moscow",
                deviceType = "Mobile",
                osName = "Android",
                osVersion = "14",
                clientName = "etCetera",
                clientVersion = "1.0.0",
                deviceModel = "Pixel 7 Pro"
            )
        )
    }
}

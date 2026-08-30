package com.dertefter.settings_security.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.auth.AuthSessionDto
import com.dertefter.design.common.DateParser
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.settings_security.R
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SessionItemContent(
    session: AuthSessionDto,
    modifier: Modifier = Modifier
) {
    val deviceName = session.deviceModel ?: session.clientName ?: stringResource(R.string.settings_security_unknown_device)
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Icon(
            imageVector = Icons.Security,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = if (session.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(modifier = Modifier.weight(1f)) {
            FlowRow(
                itemVerticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (session.isCurrent) {
                    Text(
                        text =stringResource(R.string.settings_security_current_session),
                        style = MaterialTheme.typography.labelLargeEmphasized,
                        color = MaterialTheme.colorScheme.primary,

                    )
                }
            }
            Text(
                text = "$osInfo · $locationInfo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = session.ipAddress,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            if (!session.isCurrent) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.settings_security_last_active, lastActiveFormatted),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionItemContentPreview() {
    AppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            SessionItemContent(
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
            Spacer(modifier = Modifier.height(16.dp))
            SessionItemContent(
                session = AuthSessionDto(
                    id = "2",
                    isCurrent = false,
                    createdAt = "2024-01-01T00:00:00Z",
                    lastUsedAt = "2024-01-01T00:00:00Z",
                    expiresAt = "2024-01-01T00:00:00Z",
                    ipAddress = "192.168.1.2",
                    ipCountry = "Germany",
                    ipCity = "Berlin",
                    deviceType = "Desktop",
                    osName = "Windows",
                    osVersion = "11",
                    clientName = "Chrome",
                    clientVersion = "120.0.0",
                    deviceModel = null
                )
            )
        }
    }
}

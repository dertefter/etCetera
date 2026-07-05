package com.dertefter.design.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.R
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ErrorLarge(
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
    retryText: String? = null,
    onRetry: () -> Unit = {}
) {

    val title = title ?: stringResource(R.string.design_update_failed)
    val retryText = retryText ?: stringResource(R.string.design_retry)



    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Icon(
            imageVector = Icons.Refresh,
            contentDescription = retryText,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(
                    shape = MaterialShapes.Cookie6Sided.toShape(0)
                )
                .clickable(onClick = onRetry)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(MaterialTheme.spacing.extraLarge)
                .size(28.dp)

        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
            message?.let{ message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }

}

@Preview(locale = "en", showBackground = true)
@Composable
fun ErrorLargePreview() {
    AppTheme {
        ErrorLarge(
            onRetry = {},
            title = "Title string",
            message = "Lalal ccko cjkcs"
        )
    }

}
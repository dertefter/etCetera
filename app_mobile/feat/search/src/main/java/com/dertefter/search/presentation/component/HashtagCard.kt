package com.dertefter.search.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.design.common.PrettifyInt
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.search.R

@Composable
fun SearchHashtagCard(
    hashtag: SearchHashtagDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = MaterialTheme.spacing.large,
                vertical = MaterialTheme.spacing.small
            ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Text(
            text = "#${hashtag.name}",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMediumEmphasized
        )
        Text(
            text = stringResource(
                id = R.string.posts_count,
                hashtag.postsCount.PrettifyInt()
            ),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = false)
@Composable
fun SearchHashtagCardPreview() {
    AppTheme {
        SearchHashtagCard(
            hashtag = SearchHashtagDto(
                id = "1",
                name = "android",
                postsCount = 123
            )
        )
    }
}



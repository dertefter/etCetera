package com.dertefter.search.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.design.common.PrettifyInt
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.search.R

@Composable
fun SearchHashtagCard(
    hashtag: SearchHashtagDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.largeIncreased)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                all = MaterialTheme.spacing.extraLarge
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = "#${hashtag.name}",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLargeEmphasized,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(
                    id = R.string.posts_count,
                    hashtag.postsCount.PrettifyInt()
                ),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Icon(
            imageVector = Icons.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
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



package com.dertefter.design.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.R
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.customColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    likes: Int,
    isLiked: Boolean,
    onClick: () -> Unit = {}
) {

    PostActionButton(
        modifier = modifier,
        value = likes,
        isChecked = isLiked,
        onClick = onClick,
        checkedContentDescription = stringResource(R.string.design_like_button_unlike),
        uncheckedContentDescription = stringResource(R.string.design_like_button_like),
        checkedContainerColor = MaterialTheme.customColors.likeContainerColor,
        checkedContentColor = MaterialTheme.customColors.onLikeContainerColor,
        checkedIcon = Icons.FavFilled,
        uncheckedIcon = Icons.Fav
    )
}

@Preview(showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun LikeButtonPreview() {
    AppTheme(
        content = {
            val values = listOf(1, 2, 6, 10, 100, 200, 1000, 10000, 0)
            var index by remember { mutableIntStateOf(0) }
            var isLiked by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                while (true) {
                    delay(8000.milliseconds)
                    index = (index + 1) % values.size
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                LikeButton(
                    likes = values[index],
                    isLiked = isLiked,
                    onClick = { isLiked = !isLiked }
                )
                LikeButton(
                    likes = values[index],
                    isLiked = !isLiked,
                    onClick = { isLiked = !isLiked }
                )
            }

        },
    )
}

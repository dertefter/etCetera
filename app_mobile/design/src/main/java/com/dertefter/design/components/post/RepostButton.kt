package com.dertefter.design.components.post

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.R
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme

@Composable
fun RepostButton(
    modifier: Modifier = Modifier,
    reposts: Int,
    isReposted: Boolean,
    onClick: () -> Unit = {},
    style: PostActionButtonStyle = PostActionButtonStyle.SMALL
){

    PostActionButton(
        modifier = modifier,
        value = reposts,
        isChecked = isReposted,
        onClick = onClick,
        checkedContentDescription = stringResource(R.string.design_unrepost),
        uncheckedContentDescription = stringResource(R.string.design_repost),
        checkedIcon = Icons.Cached,
        uncheckedIcon = Icons.Cached,
        style = style
    )
}

@Preview(showBackground = false)
@Composable
fun RepostButtonPreview() {
    AppTheme {
        RepostButton(
            reposts = 10,
            isReposted = true
        )
    }
}
@Preview(showBackground = false)
@Composable
fun RepostButtonPreview2() {
    AppTheme {
        RepostButton(
            reposts = 0,
            isReposted = false
        )
    }
}

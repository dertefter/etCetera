package com.dertefter.design.components.post

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.R
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.customColors

@Composable
fun CommentsButton(
    modifier: Modifier = Modifier,
    comments: Int,
    onClick: () -> Unit = {}
){

    PostActionButton(
        modifier = modifier,
        value = comments,
        isChecked = false,
        onClick = onClick,
        checkedContentDescription = stringResource(R.string.design_comments),
        uncheckedContentDescription = stringResource(R.string.design_comments),
        checkedContainerColor = MaterialTheme.customColors.likeContainerColor,
        checkedContentColor = MaterialTheme.customColors.onLikeContainerColor,
        checkedIcon = Icons.Comment,
        uncheckedIcon = Icons.Comment
    )
}

@Preview(showBackground = false)
@Composable
fun CommentsButtonPreview() {
    AppTheme(
        content = {
            CommentsButton(
                comments = 10
            )
        },
    )
}
@Preview(showBackground = false)
@Composable
fun CommentsButtonPreview2() {
    AppTheme(
        content = {
            CommentsButton(
                comments = 0
            )
        },
    )
}

package com.dertefter.design.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dertefter.design.components.post.ActiveNicknameUiModel
import com.dertefter.design.components.post.PinUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import kotlinx.coroutines.launch


@Composable
fun ActiveNickname(
    activeNickname: ActiveNicknameUiModel,
    modifier: Modifier = Modifier,
){
    Text(
        text = activeNickname.label.toString(),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(
                vertical = MaterialTheme.spacing.extraSmall,
                horizontal = MaterialTheme.spacing.small
            ),
        color = MaterialTheme.colorScheme.onSecondary,
        style = MaterialTheme.typography.labelLargeEmphasized,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayName(
    modifier: Modifier = Modifier,
    name: String,
    activeNickname: ActiveNicknameUiModel? = null,
    verified: Boolean,
    hasNuksta: Boolean,
    pin: PinUiModel? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge
    )
{

    val textColor = if (hasNuksta){
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ){

        if (verified){
            Icon(
                contentDescription = "Есть верификация",
                imageVector = Icons.VerifiedFilled,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .size(22.dp)
            )
        }

        Text(
            text = name,
            modifier = Modifier.weight(1f, fill = false),
            color = textColor,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        activeNickname?.let { activeNickname ->
            ActiveNickname(
                activeNickname,
                modifier = Modifier
                    .weight(1f, fill = false)
            )
        }

        pin?.let { pinModel ->
            val tooltipState = rememberTooltipState()
            val scope = rememberCoroutineScope()
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    positioning = TooltipAnchorPosition.Above
                ),
                tooltip = {
                    PlainTooltip(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "${pinModel.name} • ${pinModel.description}",
                            style = MaterialTheme.typography.labelLargeEmphasized,
                            modifier = Modifier.padding(MaterialTheme.spacing.small)
                        )
                    }
                },
                state = tooltipState
            ) {
                AsyncImage(
                    model = pinModel.url,
                    contentDescription = pinModel.description,
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                scope.launch { tooltipState.show() }
                            }
                        )
                        .size(20.dp)
                )
            }
        }




    }

}

@Preview(showBackground = true, device = "spec:width=500px,height=2340px,dpi=440")
@Composable
fun DisplayNamePrev(){
    AppTheme(
        content = {
            DisplayName(
                name = "TEffffffffST",
                activeNickname = ActiveNicknameUiModel(label = "djdkfffffffjd"),
                verified = true,
                hasNuksta = true,
                pin = PinUiModel(
                    description = "wawawa",
                    name = "awawaw",
                    slug = "awawaw",
                    url = null
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
    )
}
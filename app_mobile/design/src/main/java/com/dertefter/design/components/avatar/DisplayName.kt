package com.dertefter.design.components.avatar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dertefter.design.components.post.PinUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayName(
    modifier: Modifier = Modifier,
    name: String,
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
        verticalAlignment = Alignment.CenterVertically
    ){

        if (verified){
            Icon(
                contentDescription = "Есть верификация",
                imageVector = Icons.VerifiedFilled,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(20.dp)
            )
        }

        Text(
            text = name,
            color = textColor,
            style = textStyle
        )


        pin?.let { pinModel ->
            val tooltipState = rememberTooltipState()
            val scope = rememberCoroutineScope()
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    positioning = TooltipAnchorPosition.Above
                ),
                tooltip = {
                    PlainTooltip {
                        Text("${pinModel.name} • ${pinModel.description}")
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
                        .padding(start = 6.dp)
                        .size(20.dp)
                )
            }
        }




    }

}

@Preview
@Composable
fun DisplayNamePrev(){
    AppTheme {
        DisplayName(
            name = "Пользователь",
            verified = true,
            hasNuksta = true,
        )
    }
}
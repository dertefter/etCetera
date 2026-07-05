package com.dertefter.design.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import coil.compose.AsyncImage
import com.dertefter.design.components.post.PinUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing

@Composable
fun DisplayName(
    modifier: Modifier = Modifier,
    name: String,
    verified: Boolean,
    hasNuksta: Boolean,
    pin: PinUiModel? = null,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall
    )
{

    val textColor = if (hasNuksta){
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
    ){

        if (verified){
            Icon(
                contentDescription = "Есть верификация",
                imageVector = Icons.VerifiedFilled,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .size(12.dp)
            )
        }

        Text(
            text = name,
            color = textColor,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )


        pin?.let { pinModel ->
            AsyncImage(
                model = pinModel.url,
                contentDescription = pinModel.description,
                modifier = Modifier
                    .size(12.dp)
            )
        }




    }

}

@Preview(device = "id:wearos_small_round")
@Composable
fun DisplayNamePrev(){
    WearableTheme() {
        ScreenScaffold() {
            DisplayName(
                name = "Пользователь",
                verified = true,
                hasNuksta = true,
            )
        }

    }
}
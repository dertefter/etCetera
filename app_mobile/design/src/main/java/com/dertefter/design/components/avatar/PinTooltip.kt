package com.dertefter.design.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dertefter.design.components.post.PinUiModel
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun PinTooltip(
    modifier: Modifier = Modifier,
    pin: PinUiModel,
){
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        shadowElevation = MaterialTheme.spacing.medium,
        tonalElevation = MaterialTheme.spacing.medium,

    ){

        FlowRow(
            modifier = Modifier.padding(MaterialTheme.spacing.large),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ){

            AsyncImage(
                model = pin.url,
                contentDescription = pin.description,
                modifier = Modifier
                    .size(56.dp)
            )

            Column{
                Text(
                    text = pin.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = pin.description,
                    style = MaterialTheme.typography.labelMediumEmphasized,
                )
            }



        }



    }
}

@Composable
@Preview(showBackground = true)
fun PinTooltipPrev(){
    AppTheme {
        PinTooltip(
            modifier = Modifier.padding(20.dp),
            pin = PinUiModel(
                description = "Поддержал ИТДdddddddddddddddddddddddddddddddddddddddddddddddddddd",
                name = "ИТД НУКСТА",
                slug = "",
                url = ""
            )
        )
    }
}
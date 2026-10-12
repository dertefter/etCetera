package com.dertefter.onboarding.presentation.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.onboarding.R
import com.dertefter.onboarding.presentation.Event
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Page1(onEvent: (Event) -> Unit) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val hazeState = rememberHazeState()

    val infiniteTransition = rememberInfiniteTransition(label = "angleTransition")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angleAnimation"
    )

    Scaffold(
        topBar = {
            AppTopBar(
                hazeState = hazeState,
                title = {
                    Text(stringResource(R.string.onboarding_welcome_title))
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(
                    contentPadding
                            + PaddingValues(top = MaterialTheme.spacing.small)
                            + PaddingValues(horizontal = MaterialTheme.spacing.extraLarge)
                            + PaddingValues(bottom = MaterialTheme.bottomNavHeight)
                )
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(R.string.onboarding_page1_description)
            )

            Text(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.largeIncreased)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(MaterialTheme.spacing.largeIncreased)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyLargeEmphasized,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                text = stringResource(R.string.onboarding_page1_warning)
            )

            FilledIconButton(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.largeIncreased)
                    .size(160.dp),
                onClick = { onEvent(Event.OnNextPage) },
                shape = MaterialShapes.Cookie6Sided.toShape(
                    startAngle = angle.toInt()
                )
            ) {
                Icon(
                    modifier = Modifier.size(100.dp),
                    imageVector = Icons.ArrowForward,
                    contentDescription = stringResource(R.string.onboarding_next)

                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
private fun Page1Preview() {
    AppTheme {
        Page1(
            onEvent = {}
        )
    }
}





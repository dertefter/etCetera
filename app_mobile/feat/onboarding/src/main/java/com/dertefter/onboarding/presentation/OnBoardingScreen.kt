package com.dertefter.onboarding.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.onboarding.R
import com.dertefter.onboarding.presentation.screens.Page1
import com.dertefter.onboarding.presentation.screens.Page2
import com.dertefter.onboarding.presentation.screens.Page3
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen(onEvent: (Event) -> Unit) {

    val pageCount = 3

    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()

    val handleEvent: (Event) -> Unit = { event ->
        when (event) {
            is Event.OnNextPage -> {
                if (pagerState.currentPage < pageCount - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
                onEvent(event)
            }
            else -> onEvent(event)
        }
    }

    BackHandler(
        pagerState.currentPage > 0
    ) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> Page1(onEvent = handleEvent)
                1 -> Page2(onEvent = handleEvent)
                2 -> Page3()
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        vertical = MaterialTheme.spacing.medium,
                        horizontal = MaterialTheme.spacing.extraLarge
                    )
                    + WindowInsets.safeContent.asPaddingValues()
                )
                .align(Alignment.BottomEnd),
            visible = pagerState.currentPage in 1..<pageCount-1
        ) {
            FloatingActionButton(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    if (pagerState.currentPage < pageCount - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.ArrowForward,
                    contentDescription = stringResource(R.string.onboarding_next)
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
private fun OnBoardingScreenPreview() {
    AppTheme {
        OnBoardingScreen(
            onEvent = {}
        )
    }
}

package com.dertefter.etcetera

data class MainState(
    val isAuthorized: Boolean,
    val isRefreshingToken: Boolean
)

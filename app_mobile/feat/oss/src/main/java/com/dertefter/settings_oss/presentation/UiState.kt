package com.dertefter.settings_oss.presentation

import com.github.droibit.oss_licenses.parser.OssLicense

sealed interface UiState {
    data object Loading : UiState
    data class Success(val licenses: List<OssLicense>) : UiState
    data class Error(val message: String) : UiState
}

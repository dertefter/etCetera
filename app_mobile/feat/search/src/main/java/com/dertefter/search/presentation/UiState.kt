package com.dertefter.search.presentation

import com.dertefter.data.dto.search.SearchHashtagDto
import com.dertefter.data.dto.search.SearchUserDto

data class UiState(
    val query: String = "",
    val users: List<SearchUserDto> = emptyList(),
    val hashtags: List<SearchHashtagDto> = emptyList(),
    val isError: Boolean = false,
    val isLoading: Boolean = false

)
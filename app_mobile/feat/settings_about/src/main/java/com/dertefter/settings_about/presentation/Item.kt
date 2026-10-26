package com.dertefter.settings_about.presentation

data class Item(
    val title: String,
    val subtitle: String? = null,
    val action: () -> Unit = {}
)
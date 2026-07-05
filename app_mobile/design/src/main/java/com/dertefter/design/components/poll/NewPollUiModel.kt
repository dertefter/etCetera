package com.dertefter.design.components.poll

data class NewPollUiModel(
    val title: String,
    val questions: List<NewPollOptionUiModel> = listOf(
        NewPollOptionUiModel("", "")
    ),
    val isMultipleChoice: Boolean = false,
){
    fun isReady(): Boolean{
        return !questions.any { it.text == "" }
    }
}

package com.dertefter.pin_changer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.repository.MeRepository
import com.dertefter.navigation.Navigator
import com.dertefter.pin_changer.presentation.Event
import com.dertefter.pin_changer.presentation.UiState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey


@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class PinChangerViewModel(
    private val navigator: Navigator,
    private val meRepository: MeRepository
) : ViewModel() {

    private val _me = meRepository.me
    private val _isLoading = MutableStateFlow(false)
    private val _pins = meRepository.pins

    val uiState = combine(
        _me,
        _pins,
        _isLoading
    ) { me, pins, isLoading ->
        UiState(
            me = me,
            isLoading = isLoading,
            pins = pins ?: emptyList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )

    init {
        fetchMe()
        fetchPins()
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnRefresh -> {
                fetchMe()
                fetchPins()
            }

            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

            is Event.OnPinChange -> {
                viewModelScope.launch {
                    event.slug?.let {
                        meRepository.savePin(event.slug)
                    } ?: meRepository.deletePin()
                    fetchMe()
                    fetchPins()
                }
            }
        }
    }

    private fun fetchMe() {
        viewModelScope.launch {
            _isLoading.value = true
            meRepository.updateMe()
            _isLoading.value = false
        }
    }

    private fun fetchPins() {
        viewModelScope.launch {
            meRepository.updatePins()
        }
    }

}

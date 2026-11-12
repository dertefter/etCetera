package com.dertefter.edbi_users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.ebdi.EbdiUser
import com.dertefter.data.repository.EbdiRepository
import com.dertefter.edbi_users.presentation.Event
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes.User
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.offset.MutablePaginator
import com.jamal_aliev.paginator.offset.extension.uiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class EdbiUsersViewModel(
    private val ebdiRepository: EbdiRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _clan = MutableStateFlow<String?>(null)

    private val _paginator = MutableStateFlow<MutablePaginator<EbdiUser>?>(null)
    val paginator: StateFlow<MutablePaginator<EbdiUser>?> = _paginator.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PaginatorUiState<EbdiUser>> = _paginator.flatMapLatest { paginator ->
        paginator?.uiState ?: flowOf(PaginatorUiState.Idle)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaginatorUiState.Idle
    )

    fun initWithClan(clan: String) {
        if (_clan.value == clan) return
        _clan.value = clan

        _paginator.value?.release()
        val p = ebdiRepository.getUsersPaginator(clan)
        _paginator.value = p
        setupPaginator(p)
    }

    private fun setupPaginator(paginator: MutablePaginator<EbdiUser>) {
        viewModelScope.launch {
            paginator.restart(silentlyLoading = true)
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.OnRefresh -> {
                viewModelScope.launch {
                    _paginator.value?.restart(silentlyLoading = true)
                }
            }

            Event.OnBackClick -> {
                navigator.navigateUp()
            }

            is Event.OnOpenUser -> {
                navigator.navigate(User(event.userId))
            }
        }
    }

    override fun onCleared() {
        _paginator.value?.release()
    }
}

package com.dertefter.etcetera

import com.dertefter.data.repository.AuthRepository
import com.dertefter.etcetera.presentation.MainScreenState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val authRepository: AuthRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `mainScreenState updates when authorization status changes`() = runTest {
        // Given
        val isAuthorizedFlow = MutableStateFlow(false)
        every { authRepository.isAuthorized } returns isAuthorizedFlow
        
        viewModel = MainViewModel(authRepository)

        // Then: Initial state should be false
        assertEquals(MainScreenState(isAuthorized = false), viewModel.mainScreenState.value)

        // When: Authorization status changes to true
        isAuthorizedFlow.value = true

        // Then: State should update
        val updatedState = viewModel.mainScreenState.first { it.isAuthorized }
        assertEquals(MainScreenState(isAuthorized = true), updatedState)
    }
}

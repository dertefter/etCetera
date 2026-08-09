package com.dertefter.etcetera

import android.content.Context
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.repository.AuthRepository
import com.dertefter.data.repository.CrashlyticsRepository
import com.dertefter.data.repository.MeRepository
import com.dertefter.data.dto.me.MeDto
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
    private val meRepository: MeRepository = mockk()
    private val crashlyticsRepository: CrashlyticsRepository = mockk()
    private val tokenManager: TokenManager = mockk()
    private val context: Context = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mocks for init block
        every { authRepository.currentLogin } returns flowOf(null)
        every { crashlyticsRepository.currentError } returns flowOf(null)
        every { meRepository.meDto } returns flowOf(null)
        coEvery { meRepository.fetchMe() } returns mockk()
        
        // Mock tokenManager for refreshToken and accessToken flows
        every { tokenManager.getRefreshTokenForLogin(any()) } returns flowOf(null)
        every { tokenManager.getAccessTokenForLogin(any()) } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState updates when currentLogin changes`() = runTest {
        // Given
        val currentLoginFlow = MutableStateFlow<String?>(null)
        every { authRepository.currentLogin } returns currentLoginFlow
        
        viewModel = MainViewModel(authRepository, meRepository, crashlyticsRepository, tokenManager, context)

        // Then: Initial state should be isReady = false
        assertEquals(MainUiState(isReady = false, currentLogin = null), viewModel.uiState.value)

        // When: currentLogin updates
        currentLoginFlow.value = "test_user"
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: uiState should be updated
        assertEquals(MainUiState(isReady = true, currentLogin = "test_user"), viewModel.uiState.value)
    }

    @Test
    fun `meUserId updates when meRepository meDto changes`() = runTest {
        // Given
        val meDtoFlow = MutableStateFlow<MeDto?>(null)
        every { meRepository.meDto } returns meDtoFlow
        
        viewModel = MainViewModel(authRepository, meRepository, crashlyticsRepository, tokenManager, context)

        // When: meDto updates
        val mockMeDto: MeDto = mockk()
        every { mockMeDto.id } returns "user_123"
        meDtoFlow.value = mockMeDto
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: meUserId should be updated
        assertEquals("user_123", viewModel.meUserId.value)
    }
}

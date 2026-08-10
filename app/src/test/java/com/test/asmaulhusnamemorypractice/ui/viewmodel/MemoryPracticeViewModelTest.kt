package com.test.asmaulhusnamemorypractice.ui.viewmodel

import com.test.asmaulhusnamemorypractice.data.AppPreferences
import com.test.asmaulhusnamemorypractice.data.AsmaulHusnaRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MemoryPracticeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val appPreferences: AppPreferences = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { appPreferences.lastPracticedIndexFlow } returns flowOf(0)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onPrevious_movesBackOneItem() {
        val viewModel = MemoryPracticeViewModel(appPreferences)

        // Advance until idle to allow init block to complete
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onNext()
        viewModel.onPrevious()

        val state = viewModel.uiState.value
        assertEquals(0, state.currentIndex)
        assertEquals(AsmaulHusnaRepository.items[0].number, state.currentItem.number)
    }
}

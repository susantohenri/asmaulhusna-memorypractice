package com.test.asmaulhusnamemorypractice.ui.viewmodel

import com.test.asmaulhusnamemorypractice.data.AsmaulHusnaRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class MemoryPracticeViewModelTest {

    @Test
    fun onPrevious_movesBackOneItem() {
        val viewModel = MemoryPracticeViewModel()

        viewModel.onNext()
        viewModel.onPrevious()

        val state = viewModel.uiState.value
        assertEquals(0, state.currentIndex)
        assertEquals(AsmaulHusnaRepository.items[0].number, state.currentItem.number)
    }
}

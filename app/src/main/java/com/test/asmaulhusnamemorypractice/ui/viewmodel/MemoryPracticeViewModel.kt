package com.test.asmaulhusnamemorypractice.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.test.asmaulhusnamemorypractice.data.AsmaulHusnaRepository
import com.test.asmaulhusnamemorypractice.model.AsmaulHusna
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI State for the Asmaul Husna Memory Practice screen.
 *
 * This is a sequential active recall trainer — meanings are always visible.
 * Tapping the card or pressing Next advances to the next name.
 */
data class MemoryPracticeUiState(
    val currentIndex: Int = 0,
    val currentItem: AsmaulHusna = AsmaulHusnaRepository.items[0],
    val isCompleted: Boolean = false,
    val totalCount: Int = AsmaulHusnaRepository.items.size
) {
    val progress: Float
        get() = (currentIndex + 1).toFloat() / totalCount.toFloat()

    val progressText: String
        get() = "${currentIndex + 1} / $totalCount"
}

class MemoryPracticeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryPracticeUiState())
    val uiState: StateFlow<MemoryPracticeUiState> = _uiState.asStateFlow()

    /**
     * Advance to the next Asmaul Husna.
     * When reaching the end (#99), mark as completed and invoke callback.
     */
    fun onNext(onRoundFinished: () -> Unit = {}) {
        _uiState.update { currentState ->
            if (currentState.currentIndex < currentState.totalCount - 1) {
                val nextIdx = currentState.currentIndex + 1
                currentState.copy(
                    currentIndex = nextIdx,
                    currentItem = AsmaulHusnaRepository.items[nextIdx]
                )
            } else {
                onRoundFinished()
                currentState.copy(isCompleted = true)
            }
        }
    }

    /**
     * Reset back to #1 (Ar-Rahman).
     */
    fun onReset(onResetTriggered: () -> Unit = {}) {
        onResetTriggered()
        _uiState.update {
            it.copy(
                currentIndex = 0,
                currentItem = AsmaulHusnaRepository.items[0],
                isCompleted = false
            )
        }
    }

    fun dismissCompletionDialog() {
        _uiState.update { it.copy(isCompleted = false) }
    }
}

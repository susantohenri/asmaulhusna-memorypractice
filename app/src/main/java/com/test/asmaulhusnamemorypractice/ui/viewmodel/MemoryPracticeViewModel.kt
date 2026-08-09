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
 */
data class MemoryPracticeUiState(
    val currentIndex: Int = 0,
    val currentItem: AsmaulHusna = AsmaulHusnaRepository.items[0],
    val isRevealed: Boolean = false, // Concealed by default for Active Recall
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

    fun onNext(onRoundFinished: () -> Unit = {}) {
        _uiState.update { currentState ->
            if (currentState.currentIndex < currentState.totalCount - 1) {
                val nextIdx = currentState.currentIndex + 1
                currentState.copy(
                    currentIndex = nextIdx,
                    currentItem = AsmaulHusnaRepository.items[nextIdx],
                    isRevealed = false
                )
            } else {
                onRoundFinished()
                currentState.copy(isCompleted = true)
            }
        }
    }

    fun onPrevious() {
        _uiState.update { currentState ->
            if (currentState.currentIndex > 0) {
                val prevIdx = currentState.currentIndex - 1
                currentState.copy(
                    currentIndex = prevIdx,
                    currentItem = AsmaulHusnaRepository.items[prevIdx],
                    isRevealed = false
                )
            } else {
                currentState
            }
        }
    }

    fun toggleReveal() {
        _uiState.update { currentState ->
            currentState.copy(isRevealed = !currentState.isRevealed)
        }
    }

    fun onReset(onResetTriggered: () -> Unit = {}) {
        onResetTriggered()
        _uiState.update { currentState ->
            currentState.copy(
                currentIndex = 0,
                currentItem = AsmaulHusnaRepository.items[0],
                isRevealed = false,
                isCompleted = false
            )
        }
    }

    fun dismissCompletionDialog() {
        _uiState.update { currentState ->
            currentState.copy(isCompleted = false)
        }
    }
}

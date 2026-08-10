package com.henrisusanto.asmaulhusnamemorypractice.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.henrisusanto.asmaulhusnamemorypractice.data.AppPreferences
import com.henrisusanto.asmaulhusnamemorypractice.data.AsmaulHusnaRepository
import com.henrisusanto.asmaulhusnamemorypractice.model.AsmaulHusna
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * UI State for the Asmaul Husna Memory Practice screen.
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

class MemoryPracticeViewModel(private val appPreferences: AppPreferences) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryPracticeUiState())
    val uiState: StateFlow<MemoryPracticeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val lastIndex = appPreferences.lastPracticedIndexFlow.first()
            if (lastIndex in 0 until AsmaulHusnaRepository.items.size) {
                _uiState.update {
                    it.copy(
                        currentIndex = lastIndex,
                        currentItem = AsmaulHusnaRepository.items[lastIndex]
                    )
                }
            }
        }
    }

    private fun saveIndex(index: Int) {
        viewModelScope.launch {
            appPreferences.setLastPracticedIndex(index)
        }
    }

    fun onNext(onRoundFinished: () -> Unit = {}) {
        _uiState.update { currentState ->
            if (currentState.currentIndex < currentState.totalCount - 1) {
                val nextIdx = currentState.currentIndex + 1
                saveIndex(nextIdx)
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

    fun onPrevious() {
        _uiState.update { currentState ->
            if (currentState.currentIndex > 0) {
                val prevIdx = currentState.currentIndex - 1
                saveIndex(prevIdx)
                currentState.copy(
                    currentIndex = prevIdx,
                    currentItem = AsmaulHusnaRepository.items[prevIdx],
                    isCompleted = false
                )
            } else {
                currentState
            }
        }
    }

    fun onReset(onResetTriggered: () -> Unit = {}) {
        onResetTriggered()
        saveIndex(0)
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

    class Factory(private val appPreferences: AppPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MemoryPracticeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MemoryPracticeViewModel(appPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

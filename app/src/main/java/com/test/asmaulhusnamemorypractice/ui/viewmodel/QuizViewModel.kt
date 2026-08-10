package com.test.asmaulhusnamemorypractice.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.test.asmaulhusnamemorypractice.data.AsmaulHusnaRepository
import com.test.asmaulhusnamemorypractice.model.AsmaulHusna
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

sealed class QuizQuestion {
    data class NextNameQuiz(
        val currentItem: AsmaulHusna,
        val correctNextItem: AsmaulHusna,
        val options: List<AsmaulHusna> // 3 options
    ) : QuizQuestion()

    data class MeaningQuiz(
        val item: AsmaulHusna,
        val options: List<AsmaulHusna>, // 3 options
        val correctAnswerNumber: Int
    ) : QuizQuestion()

    data class SequenceQuiz(
        val sequenceItems: List<AsmaulHusna>, // The correct 3 items in order
        val options: List<AsmaulHusna> // 4-5 items shuffled for the user to pick from
    ) : QuizQuestion()
}

data class QuizUiState(
    val isFinished: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val totalQuestions: Int = 5,
    val selectedOptionsSequence: List<AsmaulHusna> = emptyList() // For Sequence Quiz
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentQuestionIndex)
}

class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val repositoryItems = AsmaulHusnaRepository.items

    init {
        startNewQuiz()
    }

    fun startNewQuiz() {
        val questions = generateQuestions(5)
        _uiState.value = QuizUiState(
            questions = questions,
            totalQuestions = 5,
            currentQuestionIndex = 0,
            score = 0,
            isFinished = false,
            selectedOptionsSequence = emptyList()
        )
    }

    private fun generateQuestions(count: Int): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()
        for (i in 0 until count) {
            val type = Random.nextInt(3)
            when (type) {
                0 -> {
                    // NextNameQuiz
                    val index = Random.nextInt(repositoryItems.size - 1)
                    val current = repositoryItems[index]
                    val correctNext = repositoryItems[index + 1]
                    
                    val wrongOptions = repositoryItems.filter { it.number != correctNext.number }
                        .shuffled().take(2)
                    
                    val options = (wrongOptions + correctNext).shuffled()
                    questions.add(QuizQuestion.NextNameQuiz(current, correctNext, options))
                }
                1 -> {
                    // MeaningQuiz
                    val current = repositoryItems.random()
                    val correctAnswerNumber = current.number
                    val wrongOptions = repositoryItems.filter { it.number != current.number }
                        .shuffled().take(2)
                        
                    val options = (wrongOptions + current).shuffled()
                    questions.add(QuizQuestion.MeaningQuiz(current, options, correctAnswerNumber))
                }
                2 -> {
                    // SequenceQuiz
                    val startIndex = Random.nextInt(repositoryItems.size - 2)
                    val sequence = listOf(
                        repositoryItems[startIndex],
                        repositoryItems[startIndex + 1],
                        repositoryItems[startIndex + 2]
                    )
                    
                    val extraOptions = repositoryItems.filter { it !in sequence }
                        .shuffled().take(2)
                        
                    val options = (sequence + extraOptions).shuffled()
                    questions.add(QuizQuestion.SequenceQuiz(sequence, options))
                }
            }
        }
        return questions
    }

    fun submitAnswerNextName(selectedItem: AsmaulHusna) {
        val current = _uiState.value.currentQuestion as? QuizQuestion.NextNameQuiz ?: return
        if (selectedItem.number == current.correctNextItem.number) {
            _uiState.update { it.copy(score = it.score + 1) }
        }
        moveToNextQuestion()
    }

    fun submitAnswerMeaning(selectedItem: AsmaulHusna) {
        val current = _uiState.value.currentQuestion as? QuizQuestion.MeaningQuiz ?: return
        if (selectedItem.number == current.correctAnswerNumber) {
            _uiState.update { it.copy(score = it.score + 1) }
        }
        moveToNextQuestion()
    }

    fun selectSequenceOption(item: AsmaulHusna) {
        _uiState.update { currentState ->
            val currentSelected = currentState.selectedOptionsSequence.toMutableList()
            if (!currentSelected.contains(item) && currentSelected.size < 3) {
                currentSelected.add(item)
            }
            if (currentSelected.size < 3) {
                currentState.copy(selectedOptionsSequence = currentSelected)
            } else {
                val question = currentState.currentQuestion as? QuizQuestion.SequenceQuiz
                val score = if (question != null && currentSelected == question.sequenceItems) {
                    currentState.score + 1
                } else {
                    currentState.score
                }
                val nextIndex = currentState.currentQuestionIndex + 1

                if (nextIndex < currentState.totalQuestions) {
                    currentState.copy(
                        currentQuestionIndex = nextIndex,
                        score = score,
                        selectedOptionsSequence = emptyList()
                    )
                } else {
                    currentState.copy(
                        score = score,
                        isFinished = true,
                        selectedOptionsSequence = emptyList()
                    )
                }
            }
        }
    }
    
    fun removeSequenceOption(item: AsmaulHusna) {
        _uiState.update { currentState ->
            val currentSelected = currentState.selectedOptionsSequence.toMutableList()
            currentSelected.remove(item)
            currentState.copy(selectedOptionsSequence = currentSelected)
        }
    }
    
    private fun moveToNextQuestion() {
        _uiState.update { currentState ->
            val nextIndex = currentState.currentQuestionIndex + 1
            if (nextIndex < currentState.totalQuestions) {
                currentState.copy(
                    currentQuestionIndex = nextIndex,
                    selectedOptionsSequence = emptyList() // reset sequence selection
                )
            } else {
                currentState.copy(isFinished = true)
            }
        }
    }
}

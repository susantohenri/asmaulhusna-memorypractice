package com.test.asmaulhusnamemorypractice.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.asmaulhusnamemorypractice.model.AsmaulHusna
import com.test.asmaulhusnamemorypractice.data.AppLanguage
import com.test.asmaulhusnamemorypractice.ui.theme.LocalAppStrings
import com.test.asmaulhusnamemorypractice.ui.viewmodel.QuizQuestion
import com.test.asmaulhusnamemorypractice.ui.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    currentLanguage: AppLanguage,
    onQuizFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 64.dp) // Bottom nav padding
    ) {
        if (uiState.isFinished) {
            // Trigger interstitial (onQuizFinished is called when the result card is visible or right away)
            androidx.compose.runtime.LaunchedEffect(Unit) {
                onQuizFinished()
            }
            
            ScoreSummaryCard(
                score = uiState.score,
                total = uiState.totalQuestions,
                onTryAgain = { viewModel.startNewQuiz() },
                onShare = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Saya baru saja menyelesaikan Kuis Asmaul Husna dengan skor ${uiState.score}/${uiState.totalQuestions}! 🤲")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, strings.quizShareResult))
                }
            )
        } else {
            val currentQuestion = uiState.currentQuestion
            if (currentQuestion != null) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { (uiState.currentQuestionIndex).toFloat() / uiState.totalQuestions },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${strings.quizQuestion} ${uiState.currentQuestionIndex + 1} ${strings.quizOf} ${uiState.totalQuestions}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Question Header
                Text(
                    text = currentQuestion.questionText(currentLanguage),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Question Content
                when (currentQuestion) {
                    is QuizQuestion.NextNameQuiz -> {
                        NextNameQuizContent(currentQuestion, viewModel)
                    }
                    is QuizQuestion.MeaningQuiz -> {
                        MeaningQuizContent(currentQuestion, currentLanguage, viewModel)
                    }
                    is QuizQuestion.SequenceQuiz -> {
                        SequenceQuizContent(currentQuestion, uiState.selectedOptionsSequence, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun NextNameQuizContent(question: QuizQuestion.NextNameQuiz, viewModel: QuizViewModel) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = question.currentItem.arabic, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = question.currentItem.latin, fontSize = 20.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        question.options.forEach { option ->
            Button(
                onClick = { viewModel.submitAnswerNextName(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "${option.latin} (${option.arabic})", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun MeaningQuizContent(
    question: QuizQuestion.MeaningQuiz,
    currentLanguage: AppLanguage,
    viewModel: QuizViewModel
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = question.item.arabic, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = question.item.latin, fontSize = 20.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        question.options.forEach { option ->
            OutlinedButton(
                onClick = { viewModel.submitAnswerMeaning(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = option.meaning(currentLanguage),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun SequenceQuizContent(
    question: QuizQuestion.SequenceQuiz,
    selectedItems: List<AsmaulHusna>,
    viewModel: QuizViewModel
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Slots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0 until 3) {
                val item = selectedItems.getOrNull(i)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (item != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .clickable(enabled = item != null) {
                            if (item != null) viewModel.removeSequenceOption(item)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (item != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = item.arabic, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = item.latin, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    } else {
                        Text(text = "?", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chunkedOptions = question.options.chunked(2)
            chunkedOptions.forEach { rowOptions ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowOptions.forEach { option ->
                        val isSelected = selectedItems.contains(option)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = !isSelected) {
                                    viewModel.selectSequenceOption(option)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(if (isSelected) 0.dp else 4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = option.arabic, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface)
                                    Text(text = option.latin, fontSize = 12.sp, color = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                    if (rowOptions.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        
    }
}

private fun QuizQuestion.questionText(language: AppLanguage): String = when (this) {
    is QuizQuestion.NextNameQuiz -> if (language == AppLanguage.ID) {
        "Apa nama setelah ${currentItem.latin}?"
    } else {
        "What comes after ${currentItem.latin}?"
    }
    is QuizQuestion.MeaningQuiz -> if (language == AppLanguage.ID) {
        "Apa arti dari ${item.latin}?"
    } else {
        "What is the meaning of ${item.latin}?"
    }
    is QuizQuestion.SequenceQuiz -> if (language == AppLanguage.ID) {
        "Ketuk nama-nama berikut dalam urutan yang benar"
    } else {
        "Tap the names in the correct sequence"
    }
}

private fun AsmaulHusna.meaning(language: AppLanguage): String =
    if (language == AppLanguage.ID) meaningId else meaningEn

@Composable
fun ScoreSummaryCard(
    score: Int,
    total: Int,
    onTryAgain: () -> Unit,
    onShare: () -> Unit
) {
    val strings = LocalAppStrings.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = strings.quizScore,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "$score / $total",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val message = when {
                score == total -> "Perfect! Mashallah! 🌟"
                score >= total / 2 -> "Good job! Keep practicing! 👍"
                else -> "Don't give up! Let's try again. 💪"
            }
            
            Text(
                text = message,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onTryAgain,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.quizTryAgain)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.quizShareResult)
            }
        }
    }
}

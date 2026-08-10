package com.test.asmaulhusnamemorypractice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.asmaulhusnamemorypractice.data.AppLanguage
import com.test.asmaulhusnamemorypractice.model.AsmaulHusna
import com.test.asmaulhusnamemorypractice.ui.theme.AsmaulHusnaMemoryPracticeTheme

@Composable
fun ShareProgressLayout(
    item: AsmaulHusna,
    currentIndex: Int,
    totalCount: Int,
    currentLanguage: AppLanguage
) {
    AsmaulHusnaMemoryPracticeTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .width(1080.dp)
                .height(1920.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Saya sudah menghafal ${currentIndex + 1} dari $totalCount Asmaul Husna! 🤲",
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 72.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Enlarge the card for sharing
            Box(modifier = Modifier.fillMaxWidth(0.9f)) {
                AsmaulHusnaCard(
                    item = item,
                    currentLanguage = currentLanguage,
                    onCardTap = { }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Asmaul Husna: Memory Practice",
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

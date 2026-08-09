package com.test.asmaulhusnamemorypractice.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Single large "Next" button below the card.
 * Placed well above the AdMob banner to prevent accidental ad clicks.
 */
@Composable
fun NextButton(
    currentIndex: Int,
    totalCount: Int,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onNext,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(60.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = if (currentIndex >= totalCount - 1) "Selesai ✓" else "Lanjut ke Berikutnya",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Selanjutnya",
            modifier = Modifier.size(22.dp)
        )
    }
}

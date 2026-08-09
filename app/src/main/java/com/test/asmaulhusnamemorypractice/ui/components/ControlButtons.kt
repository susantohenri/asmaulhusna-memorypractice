package com.test.asmaulhusnamemorypractice.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ControlButtons(
    currentIndex: Int,
    totalCount: Int,
    isRevealed: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleReveal: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Navigation Row (Previous, Reveal, Next)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Button
            OutlinedButton(
                onClick = onPrevious,
                enabled = currentIndex > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Sebelumnya"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Sebelumnya", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            // Reveal/Conceal Toggle Button
            FilledTonalButton(
                onClick = onToggleReveal,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = if (isRevealed) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (isRevealed) "Sembunyikan" else "Buka Jawaban"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isRevealed) "Tutup" else "Buka",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Next Button
            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1.2f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text(
                    text = if (currentIndex == totalCount - 1) "Selesai" else "Lanjut",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Selanjutnya"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reset Button Row
        TextButton(
            onClick = onReset,
            modifier = Modifier.height(44.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset Hafalan",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Reset Ke Nomor 1 (Ar-Rahman)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

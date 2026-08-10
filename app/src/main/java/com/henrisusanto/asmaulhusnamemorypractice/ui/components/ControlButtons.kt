package com.henrisusanto.asmaulhusnamemorypractice.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.henrisusanto.asmaulhusnamemorypractice.ui.theme.LocalAppStrings

/**
 * Navigation buttons below the card: previous, reset, and next.
 * Placed well above the AdMob banner to prevent accidental ad clicks.
 */
@Composable
fun NextButton(
    currentIndex: Int,
    totalCount: Int,
    onPrevious: () -> Unit,
    onReset: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onPrevious,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = currentIndex > 0
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = strings.previous,
                modifier = Modifier.size(22.dp)
            )
        }

        FilledTonalButton(
            onClick = onReset,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.RestartAlt,
                contentDescription = strings.reset,
                modifier = Modifier.size(22.dp)
            )
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = currentIndex < totalCount - 1 || currentIndex == totalCount - 1
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = strings.next,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

package com.test.asmaulhusnamemorypractice.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.asmaulhusnamemorypractice.model.AsmaulHusna
import com.test.asmaulhusnamemorypractice.data.AppLanguage
import com.test.asmaulhusnamemorypractice.ui.theme.LocalAppStrings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size

/**
 * Main practice card. Tap anywhere on the card = NEXT.
 * Meanings are ALWAYS visible (this is sequential order recall, not vocab quiz).
 */
@Composable
fun AsmaulHusnaCard(
    item: AsmaulHusna,
    currentLanguage: AppLanguage,
    onCardTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var showQuote by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Subtle: no ripple on entire card since tap=next
            ) {
                onCardTap()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Row: Number Badge (left)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sequence Number Badge
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${item.number}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Arabic Text — large & prominent
            Text(
                text = item.arabic,
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                lineHeight = 60.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Latin Transliteration — always visible
            Text(
                text = item.latin,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Meaning — dynamic based on language
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.ID) item.meaningId else item.meaningEn,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hint text
            Text(
                text = strings.tapCardToContinue,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                fontWeight = FontWeight.Medium
            )
            }

            // Wisdom / Daily Quote pill badge at top-right
            val badgeLabel = "✨ ${strings.dailyWisdom}"

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showQuote = true }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = badgeLabel,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (showQuote) {
                val quoteToShow = if (currentLanguage == AppLanguage.ID) item.quoteId else item.quoteEn
                
                QuoteDialog(
                    quote = quoteToShow,
                    title = strings.dailyWisdom,
                    closeLabel = strings.close,
                    copyLabel = strings.copy,
                    shareLabel = strings.share,
                    onDismiss = { showQuote = false },
                    context = context
                )
            }
        }
    }
}

@Composable
fun QuoteDialog(
    quote: String,
    title: String,
    closeLabel: String,
    copyLabel: String,
    shareLabel: String,
    onDismiss: () -> Unit,
    context: Context
) {
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = closeLabel)
            }
        },
        title = {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column {
                Text(text = quote, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        // Copy to clipboard
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("quote", quote)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, strings.copiedToClipboard, Toast.LENGTH_SHORT).show()
                    }) {
                        Text(text = copyLabel)
                    }

                    TextButton(onClick = {
                        // Share via intent
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, quote)
                        }
                        context.startActivity(Intent.createChooser(intent, shareLabel))
                    }) {
                        Text(text = shareLabel)
                    }
                }
            }
        }
    )
}

package com.henrisusanto.asmaulhusnamemorypractice.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.henrisusanto.asmaulhusnamemorypractice.data.AppLanguage
import com.henrisusanto.asmaulhusnamemorypractice.data.AppTheme
import com.henrisusanto.asmaulhusnamemorypractice.ui.theme.LocalAppStrings

import android.app.TimePickerDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    currentTheme: AppTheme,
    reminderEnabled: Boolean,
    reminderHour: Int,
    reminderMinute: Int,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (AppTheme) -> Unit,
    onReminderToggle: (Boolean) -> Unit,
    onReminderTimeChange: (Int, Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onReminderTimeChange(hourOfDay, minute)
        },
        reminderHour,
        reminderMinute,
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = strings.settings, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section 1: Preferences
            SectionTitle(title = strings.preferences)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Language Selection
                    Text(text = strings.language, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SegmentedButton(
                            selected = currentLanguage == AppLanguage.AUTO,
                            onClick = { onLanguageChange(AppLanguage.AUTO) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                        ) {
                            Text(text = strings.auto)
                        }
                        SegmentedButton(
                            selected = currentLanguage == AppLanguage.EN,
                            onClick = { onLanguageChange(AppLanguage.EN) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                        ) {
                            Text(text = strings.english)
                        }
                        SegmentedButton(
                            selected = currentLanguage == AppLanguage.ID,
                            onClick = { onLanguageChange(AppLanguage.ID) },
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                        ) {
                            Text(text = strings.indonesian)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Theme Selection
                    Text(text = strings.theme, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SegmentedButton(
                            selected = currentTheme == AppTheme.AUTO,
                            onClick = { onThemeChange(AppTheme.AUTO) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                        ) {
                            Text(text = strings.auto)
                        }
                        SegmentedButton(
                            selected = currentTheme == AppTheme.LIGHT,
                            onClick = { onThemeChange(AppTheme.LIGHT) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                        ) {
                            Text(text = strings.light)
                        }
                        SegmentedButton(
                            selected = currentTheme == AppTheme.DARK,
                            onClick = { onThemeChange(AppTheme.DARK) },
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                        ) {
                            Text(text = strings.dark)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Reminder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = strings.dailyReminder, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            val timeStr = String.format(Locale.getDefault(), "%02d:%02d", reminderHour, reminderMinute)
                            Text(text = strings.everyDayAt.format(timeStr), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { 
                                onReminderToggle(it)
                                if (it) {
                                    timePickerDialog.show()
                                }
                            }
                        )
                    }
                    if (reminderEnabled) {
                        TextButton(onClick = { timePickerDialog.show() }, modifier = Modifier.align(Alignment.End)) {
                            Text(strings.changeTime)
                        }
                    }
                }
            }

            // Section 2: Legal & Information
            SectionTitle(title = strings.legalAndInformation)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    
                    // App Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = strings.appName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text(text = "${strings.version} 1.0.0", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    // Privacy Policy
                    SettingsItemRow(
                        icon = Icons.Default.OpenInBrowser,
                        title = strings.privacyPolicy,
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://susantohenri.github.io/privacy-policy"))
                                context.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(context, strings.noBrowserFound, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp)
    )
}

@Composable
fun SettingsItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        TextButton(onClick = onClick) {
            Text(strings.open)
        }
    }
}

package com.henrisusanto.asmaulhusnamemorypractice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.henrisusanto.asmaulhusnamemorypractice.ui.theme.AsmaulHusnaMemoryPracticeTheme
import com.henrisusanto.asmaulhusnamemorypractice.ui.theme.LocalAppStrings

private val CertificateGreen = Color(0xFF1B4332)
private val CertificateGold = Color(0xFFC59B27)
private val CertificateGoldLight = Color(0xFFFAF0D7)
private val CertificateBg = Color(0xFFFCFAF5)
private val CertificateDarkText = Color(0xFF2D312E)

@Composable
fun CertificateLayout(
    userName: String,
    dateString: String
) {
    val strings = LocalAppStrings.current

    AsmaulHusnaMemoryPracticeTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .width(1080.dp)
                .height(1560.dp)
                .background(CertificateBg)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Border
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(5.dp, CertificateGreen, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                // Inner Border (Gold)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, CertificateGold, RoundedCornerShape(8.dp))
                        .padding(40.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header Section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Basmalah Calligraphy
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = CertificateGreen,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Emblem
                            Surface(
                                shape = CircleShape,
                                color = CertificateGoldLight,
                                border = androidx.compose.foundation.BorderStroke(2.dp, CertificateGold),
                                modifier = Modifier.size(76.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "Emblem",
                                        tint = CertificateGold,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = strings.certificateTitle,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CertificateGreen,
                                letterSpacing = 3.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = strings.certificateSubtitle,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CertificateGold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            HorizontalDivider(
                                color = CertificateGold.copy(alpha = 0.5f),
                                thickness = 2.dp,
                                modifier = Modifier.fillMaxWidth(0.6f)
                            )
                        }

                        // Middle Section: Recipient & Description
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = strings.certificateAwardedTo,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = CertificateDarkText.copy(alpha = 0.75f),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Name
                            Text(
                                text = userName.ifBlank { "Hamba Allah" },
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CertificateGreen,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            HorizontalDivider(
                                color = CertificateGreen,
                                thickness = 3.dp,
                                modifier = Modifier.fillMaxWidth(0.5f)
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Text(
                                text = strings.certificateDescription,
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.Normal,
                                color = CertificateDarkText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Hadits Card
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CertificateGoldLight.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CertificateGold.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = strings.certificateHadithArabic,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CertificateGreen,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 32.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = strings.certificateHadithTranslation,
                                        fontSize = 14.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = CertificateDarkText.copy(alpha = 0.85f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        // Footer Section: Date, Stamp & Issuer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Date
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = strings.certificateDate.format(dateString),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CertificateDarkText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = strings.certificateOfficialStamp,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CertificateGold
                                )
                            }

                            // Center: Golden Stamp Seal
                            Surface(
                                shape = CircleShape,
                                color = CertificateGold,
                                modifier = Modifier.size(90.dp),
                                shadowElevation = 4.dp
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(1.5.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Stars,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text(
                                                text = "OFFICIAL",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White,
                                                letterSpacing = 1.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Right: Issuer
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = strings.certificateIssuer,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CertificateGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Digital Verified",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = CertificateDarkText.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

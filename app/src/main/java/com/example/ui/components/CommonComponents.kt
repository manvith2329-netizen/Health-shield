package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculation.ResilienceEngine
import com.example.model.DominantVulnerabilityType
import com.example.model.ReadinessAssessment

/**
 * Common HealthShield UI Components
 */

@Composable
fun DisclaimerBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("disclaimer_banner"),
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Medical & Prototype Disclaimer",
                tint = Color(0xFF475569),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "HealthShield is a research prototype & financial resilience organizer. It does not provide medical diagnosis, replace doctors, or grant government benefits.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = Color(0xFF334155),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun ReadinessScoreCard(
    assessment: ReadinessAssessment,
    onWhyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("readiness_score_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Family Healthcare Readiness",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = assessment.readinessLabel,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = when {
                                assessment.overallScore >= 75 -> Color(0xFF0D9488)
                                assessment.overallScore >= 55 -> Color(0xFF0284C7)
                                else -> Color(0xFFD97706)
                            }
                        )
                    )
                }

                // Circular Score Badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp)
                ) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = assessment.overallScore / 100f,
                        animationSpec = tween(durationMillis = 900),
                        label = "readiness_score"
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(72.dp),
                        color = when {
                            assessment.overallScore >= 75 -> Color(0xFF0D9488)
                            assessment.overallScore >= 55 -> Color(0xFF0284C7)
                            else -> Color(0xFFD97706)
                        },
                        strokeWidth = 7.dp,
                        trackColor = Color(0xFFE2E8F0),
                        strokeCap = StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${assessment.overallScore}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            ),
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "/ 100",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Dimension breakdown
            DimensionScoreRow(label = "Emergency Liquidity", score = assessment.emergencyLiquidityScore)
            DimensionScoreRow(label = "Healthcare Protection", score = assessment.healthcareProtectionScore)
            DimensionScoreRow(label = "Recurring Burden", score = assessment.recurringBurdenScore)
            DimensionScoreRow(label = "Coverage Understanding", score = assessment.coverageUnderstandingScore)
            DimensionScoreRow(label = "Health Savings Momentum", score = assessment.healthSavingsScore)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onWhyClick,
                    modifier = Modifier.testTag("why_did_i_get_this_score_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Why did I get this score?", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                }

                Text(
                    text = "Prototype methodology",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun DimensionScoreRow(label: String, score: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
            Text(text = "$score%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F172A))
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = when {
                score >= 70 -> Color(0xFF0D9488)
                score >= 50 -> Color(0xFF0284C7)
                else -> Color(0xFFD97706)
            },
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

@Composable
fun DominantVulnerabilityCard(
    vulnerability: DominantVulnerabilityType,
    onTakeActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dominant_vulnerability_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDE68A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFEF3C7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Dominant Vulnerability",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "DOMINANT VULNERABILITY IDENTIFIED",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = Color(0xFFB45309)
                    )
                    Text(
                        text = vulnerability.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF78350F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = vulnerability.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF451A03),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Recommended Practical Action:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = vulnerability.recommendedAction,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onTakeActionClick,
                    modifier = Modifier.testTag("simulate_recommended_action_button")
                ) {
                    Text("Simulate This Action", fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ShockSimulatorWidget(
    selectedShock: Int,
    accessibleSavings: Int,
    result: ResilienceEngine.ShockCalculationResult,
    onSelectAmount: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(10000, 25000, 50000, 100000, 200000)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("shock_simulator_widget"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE0F2FE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Medical Shock Simulator",
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Healthcare Financial Shock Simulator",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "“What happens if a medical expense happens tomorrow?”",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Presets Chips
            Text(
                text = "Select Hypothetical Medical Shock Amount:",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presets.forEach { amount ->
                    val isSelected = selectedShock == amount
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectAmount(amount) },
                        label = {
                            Text(
                                text = "₹${amount / 1000}k",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breakdown Cards
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Estimated Absorption Breakdown (₹${selectedShock.formatWithCommas()} Shock)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    ShockBreakdownItem(
                        title = "Medical Expense",
                        amount = "₹${result.shockAmount.formatWithCommas()}",
                        color = Color(0xFF0F172A),
                        subtitle = "Hypothetical in-patient bill"
                    )
                    ShockBreakdownItem(
                        title = "Immediately Accessible Health Savings",
                        amount = "− ₹${result.accessibleSavings.formatWithCommas()}",
                        color = Color(0xFF0D9488),
                        subtitle = "From your Health Wallet buffer"
                    )
                    ShockBreakdownItem(
                        title = "Insurance / Protection Available",
                        amount = "− ₹${result.potentialProtection.formatWithCommas()}",
                        color = Color(0xFF0284C7),
                        subtitle = "Estimated eligible coverage & cashless"
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFCBD5E1))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Estimated Remaining Exposure",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (result.remainingExposure > 0) Color(0xFFDC2626) else Color(0xFF0D9488)
                            )
                            Text(
                                text = if (result.remainingExposure > 0) "Out-of-pocket funding gap" else "Fully cushioned buffer",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color(0xFF64748B)
                            )
                        }
                        Text(
                            text = "₹${result.remainingExposure.formatWithCommas()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            ),
                            color = if (result.remainingExposure > 0) Color(0xFFDC2626) else Color(0xFF0D9488)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar showing absorption %
                    LinearProgressIndicator(
                        progress = { result.absorptionRatePercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF0D9488),
                        trackColor = Color(0xFFFCA5A5)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${result.absorptionRatePercent}% cushioned by savings & protection",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF475569),
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Illustrative calculation — actual hospital bills and claim admissibility depend on policy conditions, network empanelment, and doctor assessments.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun ShockBreakdownItem(
    title: String,
    amount: String,
    color: Color,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = Color(0xFF334155))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Color(0xFF64748B))
        }
        Text(text = amount, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = color)
    }
}

@Composable
fun BeforeAfterSimulationView(
    projection: ResilienceEngine.BeforeAfterProjection,
    onDailyChange: (Int) -> Unit,
    onDaysChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("before_after_simulation_view"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFDCFCE7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = "Before After Simulation",
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "What Happens If You Take Action?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Simulate how a small daily habit reduces your healthcare shock gap",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Sliders
            Text(
                text = "Daily Saving Habit: ₹${projection.dailySavingsRate} / day",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF334155)
            )
            Slider(
                value = projection.dailySavingsRate.toFloat(),
                onValueChange = { onDailyChange(it.toInt()) },
                valueRange = 25f..300f,
                steps = 10,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Time Horizon: ${projection.horizonDays} days (~${projection.horizonDays / 30} months)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF334155))
            }
            Slider(
                value = projection.horizonDays.toFloat(),
                onValueChange = { onDaysChange(it.toInt()) },
                valueRange = 30f..180f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Comparison Grid: Today vs Projected
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Today Card
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "CURRENT STATE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "₹${projection.currentSavings.formatWithCommas()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F172A))
                        Text(text = "Health Reserve", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Remaining Gap:", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFFDC2626))
                        Text(text = "₹${projection.currentGap.formatWithCommas()}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFFDC2626))
                    }
                }

                // Projected Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF86EFAC))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "IN ${projection.horizonDays} DAYS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = Color(0xFF166534))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "₹${projection.projectedSavings.formatWithCommas()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF15803D))
                        Text(text = "(+₹${projection.additionalSavings.formatWithCommas()} added)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Color(0xFF166534))
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0xFFDCFCE7))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Reduced Gap:", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF15803D))
                        Text(text = "₹${projection.projectedGap.formatWithCommas()}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF15803D))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎉 Saving ₹${projection.dailySavingsRate}/day reduces your family's healthcare shock exposure gap by ₹${(projection.currentGap - projection.projectedGap).formatWithCommas()} (${projection.reductionInGapPercent}% reduction).",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF166534),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun SafetyNetConnectionDiagram(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("safety_net_connection_diagram"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Your Family's Complete Health Safety Net",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0F172A)
            )
            Text(
                text = "Connecting health information directly to financial preparedness in 5 layers:",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            SafetyNetStepRow(
                stepNum = "1",
                title = "Health Needs",
                desc = "Who in this family needs medical attention? (Conditions, chronic medicines, reports)",
                color = Color(0xFF0284C7)
            )
            SafetyNetStepRow(
                stepNum = "2",
                title = "Protection",
                desc = "What insurance or government schemes may protect this family? (PM-JAY, State, Floater)",
                color = Color(0xFF0D9488)
            )
            SafetyNetStepRow(
                stepNum = "3",
                title = "Savings Buffer",
                desc = "How much can the family access immediately? (Dedicated Health Wallet buffer)",
                color = Color(0xFF15803D)
            )
            SafetyNetStepRow(
                stepNum = "4",
                title = "Shock Absorption",
                desc = "What happens if a ₹50,000 medical shock happens tomorrow? (Simulate exposure)",
                color = Color(0xFFD97706)
            )
            SafetyNetStepRow(
                stepNum = "5",
                title = "Practical Action",
                desc = "What is the single highest-impact action this family can take today? (Save ₹100/day)",
                color = Color(0xFF7C3AED),
                isLast = true
            )
        }
    }
}

@Composable
private fun SafetyNetStepRow(
    stepNum: String,
    title: String,
    desc: String,
    color: Color,
    isLast: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNum,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(30.dp)
                        .background(Color(0xFFCBD5E1))
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0F172A)
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = Color(0xFF475569)
            )
        }
    }
}

fun Int.formatWithCommas(): String {
    return "%,d".format(this)
}

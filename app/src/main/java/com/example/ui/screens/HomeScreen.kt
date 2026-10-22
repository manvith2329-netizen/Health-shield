package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HealthShieldViewModel
import com.example.ui.NavigationTab
import com.example.ui.components.BeforeAfterSimulationView
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.DominantVulnerabilityCard
import com.example.ui.components.ReadinessScoreCard
import com.example.ui.components.SafetyNetConnectionDiagram
import com.example.ui.components.ShockSimulatorWidget
import com.example.ui.components.formatWithCommas

@Composable
fun HomeScreen(
    viewModel: HealthShieldViewModel,
    modifier: Modifier = Modifier
) {
    val members by viewModel.members.collectAsState()
    val records by viewModel.records.collectAsState()
    val schemes by viewModel.schemes.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    val monthlySpending by viewModel.monthlySpending.collectAsState()
    val readiness by viewModel.readinessAssessment.collectAsState()
    val selectedShock by viewModel.selectedShockAmount.collectAsState()
    val shockResult by viewModel.shockResult.collectAsState()
    val beforeAfterProjection by viewModel.beforeAfterProjection.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Landing Header
        item {
            HeroLandingSection(
                onCheckReadiness = { viewModel.selectTab(NavigationTab.FAMILY) },
                onLaunchJudgeDemo = { viewModel.startJudgeDemo() },
                onSeeMethodology = { viewModel.setResearchMethodologyDialog(true) }
            )
        }

        // Family Health Snapshot Card (Section 12)
        item {
            FamilyHealthSnapshotCard(
                memberCount = members.size,
                recordsCount = records.size,
                savingsBalance = walletBalance,
                monthlySpending = monthlySpending,
                schemesCount = schemes.size,
                onViewFamily = { viewModel.selectTab(NavigationTab.FAMILY) },
                onViewRecords = { viewModel.selectTab(NavigationTab.RECORDS) },
                onViewWallet = { viewModel.selectTab(NavigationTab.WALLET) }
            )
        }

        // Core Differentiation: Readiness Score & Transparent Explainability
        item {
            ReadinessScoreCard(
                assessment = readiness,
                onWhyClick = { viewModel.setReadinessReportDialog(true) }
            )
        }

        // Dominant Vulnerability & Targeted Practical Action (Section 20 & 21)
        item {
            DominantVulnerabilityCard(
                vulnerability = readiness.dominantVulnerability,
                onTakeActionClick = { viewModel.selectTab(NavigationTab.WALLET) }
            )
        }

        // Healthcare Financial Shock Simulator (Section 18)
        item {
            ShockSimulatorWidget(
                selectedShock = selectedShock,
                accessibleSavings = walletBalance,
                result = shockResult,
                onSelectAmount = { viewModel.setShockAmount(it) }
            )
        }

        // Before & After Simulation Slider (Section 22)
        item {
            BeforeAfterSimulationView(
                projection = beforeAfterProjection,
                onDailyChange = { viewModel.setSimulationDailySaving(it) },
                onDaysChange = { viewModel.setSimulationDays(it) }
            )
        }

        // 5-Layer Connected Health Safety Net Diagram (Section 23)
        item {
            SafetyNetConnectionDiagram()
        }

        // Final Home Screen Philosophy Message (Section 46)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "“Being prepared is not about predicting illness. It is about reducing the financial damage when the unexpected happens.”",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        color = Color(0xFFF1F5F9),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.selectTab(NavigationTab.FAMILY) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Check Family Readiness", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { viewModel.startJudgeDemo() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Explore 2-Min Demo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Mandatory Disclaimer Banner
        item {
            DisclaimerBanner()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun HeroLandingSection(
    onCheckReadiness: () -> Unit,
    onLaunchJudgeDemo: () -> Unit,
    onSeeMethodology: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_landing_section"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2540)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            // App Badge & Tagline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = Color(0xFF1E3A8A),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF38BDF8), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HEALTHSHIELD RESEARCH PLATFORM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            ),
                            color = Color(0xFFE0F2FE)
                        )
                    }
                }

                Surface(
                    color = Color(0xFF0369A1),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "DEMO DATA ACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "“How prepared is your family for a healthcare emergency?”",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    lineHeight = 28.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Organize your family’s health records, understand healthcare protection, build a health savings buffer, and prepare for unexpected medical expenses — all in one place.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                ),
                color = Color(0xFFBAE6FD)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onCheckReadiness,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_check_readiness_button")
                ) {
                    Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Check Family Readiness", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(
                        onClick = onLaunchJudgeDemo,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFF0D9488),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("launch_2min_demo_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("2-Min Judge Demo", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onSeeMethodology,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE2E8F0)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("see_how_it_works_button")
                    ) {
                        Text("Research Mode", fontSize = 11.5.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyHealthSnapshotCard(
    memberCount: Int,
    recordsCount: Int,
    savingsBalance: Int,
    monthlySpending: Int,
    schemesCount: Int,
    onViewFamily: () -> Unit,
    onViewRecords: () -> Unit,
    onViewWallet: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("family_health_snapshot_card"),
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
                Column {
                    Text(
                        text = "Family Health Snapshot",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Your family has $memberCount members. 2 members have recurring care needs and active diagnostic records.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Snapshot Metric Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SnapshotPill(
                    title = "Family Members",
                    value = "$memberCount",
                    subtext = "Father, Mother, 2 Kids, Senior",
                    icon = Icons.Default.People,
                    color = Color(0xFF0284C7),
                    onClick = onViewFamily,
                    modifier = Modifier.weight(1f)
                )
                SnapshotPill(
                    title = "Health Records",
                    value = "$recordsCount Organized",
                    subtext = "Timeline & AI extracted",
                    icon = Icons.Default.Description,
                    color = Color(0xFF0D9488),
                    onClick = onViewRecords,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SnapshotPill(
                    title = "Healthcare Savings",
                    value = "₹${savingsBalance.formatWithCommas()}",
                    subtext = "Target ₹50k buffer",
                    icon = Icons.Default.Savings,
                    color = Color(0xFF15803D),
                    onClick = onViewWallet,
                    modifier = Modifier.weight(1f)
                )
                SnapshotPill(
                    title = "Monthly Spending",
                    value = "₹${monthlySpending.formatWithCommas()}",
                    subtext = "Pharmacy & Diagnostics",
                    icon = Icons.Default.MedicalInformation,
                    color = Color(0xFFD97706),
                    onClick = onViewWallet,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SnapshotPill(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF64748B)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color(0xFF94A3B8),
                maxLines = 1
            )
        }
    }
}

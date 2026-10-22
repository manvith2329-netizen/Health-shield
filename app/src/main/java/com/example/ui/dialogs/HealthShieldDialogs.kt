package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HealthShieldViewModel
import com.example.ui.components.formatWithCommas

/**
 * HealthShield Interactive Dialogs:
 * 1. HealthShieldAiDialog (Grounded Q&A assistant)
 * 2. JudgeDemoWalkthroughDialog (Fast 2-minute Expo demo)
 * 3. ReadinessReportDialog (Printable assessment report)
 * 4. ResearchMethodologyDialog (Hypothesis, ML roadmap & shock absorption curve)
 * 5. PrivacyConsentDialog (Sensitive data rights & consent checkboxes)
 */

@Composable
fun HealthShieldAiDialog(
    viewModel: HealthShieldViewModel,
    onDismiss: () -> Unit
) {
    val messages by viewModel.aiAssistantMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("healthshield_ai_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFFE0F2FE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("HealthShield AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Family Health & Resilience Assistant", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color(0xFF64748B))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                // Safety Banner
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Safety Guardrail: HealthShield AI answers based solely on your entered data. It does NOT diagnose diseases, prescribe medication, or replace a doctor.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = Color(0xFF78350F),
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Chat Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { (isUser, text) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                color = if (isUser) Color(0xFF0284C7) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.widthIn(max = 260.dp)
                            ) {
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = if (isUser) Color.White else Color(0xFF0F172A),
                                    modifier = Modifier.padding(10.dp),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Prompt Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextButton(
                        onClick = { viewModel.askAiAssistant("What happens if we face a ₹50,000 medical expense?") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("₹50k Shock?", fontSize = 10.sp, maxLines = 1)
                    }
                    TextButton(
                        onClick = { viewModel.askAiAssistant("How much did we spend on medicines last month?") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Meds Cost?", fontSize = 10.sp, maxLines = 1)
                    }
                    TextButton(
                        onClick = { viewModel.askAiAssistant("Show me medical records of my father") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Father Rx?", fontSize = 10.sp, maxLines = 1)
                    }
                }

                // Input Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask about records, expenses...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_assistant_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            val q = inputText
                            inputText = ""
                            viewModel.askAiAssistant(q)
                        },
                        modifier = Modifier
                            .background(Color(0xFF0284C7), CircleShape)
                            .size(42.dp)
                            .testTag("send_ai_query_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun JudgeDemoWalkthroughDialog(
    step: Int,
    onNext: () -> Unit,
    onDismiss: () -> Unit
) {
    val steps = listOf(
        Triple(
            "Step 1 of 5: The Family & Health Profile",
            "This demo family has 5 members (Father, Mother, 2 Children, Grandmother). Each person has different ages, recurring medicines, and chronic risk factors.",
            "Family Profiles"
        ),
        Triple(
            "Step 2 of 5: Structured Health Records",
            "Scattered reports and prescriptions are organized into a chronological family timeline with AI-assisted parameter extraction.",
            "Health Records"
        ),
        Triple(
            "Step 3 of 5: Mapped Health Protection",
            "HealthShield maps Ayushman Bharat PM-JAY, State Health Schemes, Corporate Insurance, and ABHA identity to assess potential coverage.",
            "Protection Schemes"
        ),
        Triple(
            "Step 4 of 5: The ₹50,000 Healthcare Shock",
            "Simulate what happens if an unexpected ₹50,000 medical bill arrives tomorrow. HealthShield immediately calculates the financial gap.",
            "Shock Simulator"
        ),
        Triple(
            "Step 5 of 5: Dominant Vulnerability & Targeted Action",
            "Instead of generic advice, HealthShield pinpoints Liquidity Vulnerability and proves how saving ₹100/day reduces the family's shock gap by ₹6,000 in 60 days!",
            "Action & Resilience"
        )
    )

    val current = steps.getOrElse(step) { steps.last() }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("judge_demo_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFF0D9488), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("2-Minute Judge Walkthrough", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(current.third, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0D9488), fontWeight = FontWeight.Bold))
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LinearProgressIndicator(
                    progress = { (step + 1) / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF0D9488),
                    trackColor = Color(0xFFE2E8F0)
                )

                Text(
                    text = current.first,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = current.second,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF334155),
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                modifier = Modifier.testTag("judge_demo_next_button")
            ) {
                Text(if (step < 4) "Next Step →" else "Complete Walkthrough")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Exit Demo") }
        }
    )
}

@Composable
fun ReadinessReportDialog(
    viewModel: HealthShieldViewModel,
    onDismiss: () -> Unit
) {
    val assessment by viewModel.readinessAssessment.collectAsState()
    val balance by viewModel.walletBalance.collectAsState()
    val shockResult by viewModel.shockResult.collectAsState()
    val members by viewModel.members.collectAsState()
    val records by viewModel.records.collectAsState()

    var showPrintedToast by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("readiness_report_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Family Health & Financial Report",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "EXECUTIVE SUMMARY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Family Readiness Score: ${assessment.overallScore} / 100 (${assessment.readinessLabel})", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F172A))
                            Text(text = "Family Structure: ${members.size} Members • ${records.size} Organized Records", style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
                            Text(text = "Accessible Health Savings: ₹${balance.formatWithCommas()}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF0D9488))
                        }
                    }
                }

                item {
                    Text(text = "Transparent Calculation Explainability (Why did I get this score?):", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F172A))
                }

                items(assessment.explainabilityFactors) { factor ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = factor.factor, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F172A))
                                Text(text = factor.scoreContribution, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0284C7))
                            }
                            Text(text = factor.detail, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color(0xFF64748B))
                        }
                    }
                }

                item {
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Dominant Vulnerability: ${assessment.dominantVulnerability.title}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFB45309))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = assessment.dominantVulnerability.explanation, style = MaterialTheme.typography.bodySmall, color = Color(0xFF78350F))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Recommended Action: ${assessment.dominantVulnerability.recommendedAction}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF92400E))
                        }
                    }
                }

                item {
                    if (showPrintedToast) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✅ Readiness Report compiled for print / PDF export.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF15803D),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { showPrintedToast = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier.testTag("print_save_report_button")
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Print / Save Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Composable
fun ResearchMethodologyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("research_methodology_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFFEDE9FE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF6D28D9), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Research Methodology", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Field-Validated Resilience Assessment", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color(0xFF64748B))
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Research Question:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "«Can a household-level healthcare financial resilience assessment identify meaningful vulnerabilities and produce more actionable interventions than generic healthcare-finance advice?»",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF334155),
                        lineHeight = 19.sp
                    )
                }

                item {
                    Divider(color = Color(0xFFE2E8F0))
                    Text(
                        text = "Future AI / Machine Learning Layer (Roadmap):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Household Data → Empirical Healthcare Financial Outcomes → Feature Engineering → Predictive ML (Logistic Regression, Random Forest, XGBoost) → Probability of requiring external/distress funding.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                }

                item {
                    Surface(
                        color = Color(0xFFF5F3FF),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Future Shock Absorption Curve:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF5B21B6)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "P(external funding | household characteristics, shock amount)\n" +
                                        "• ₹10k Shock: 8% probability\n" +
                                        "• ₹25k Shock: 22% probability\n" +
                                        "• ₹50k Shock: 54% probability\n" +
                                        "• ₹1L Shock: 78% probability\n" +
                                        "• ₹2L Shock: 93% probability\n\n" +
                                        "Marked as future research concept for field validation.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color(0xFF4C1D95)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9))) {
                Text("Close Methodology")
            }
        }
    )
}

@Composable
fun PrivacyConsentDialog(onDismiss: () -> Unit) {
    var consentSensitive by remember { mutableStateOf(true) }
    var consentAi by remember { mutableStateOf(true) }
    var consentVerify by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("privacy_consent_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFFDCFCE7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Privacy & Consent Architecture", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Health information is deeply personal and sensitive. In HealthShield:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569)
                )

                Text(
                    text = "• We DO NOT request real Aadhaar numbers, OTPs, or bank passwords.\n" +
                            "• We DO NOT connect to real bank accounts or transfer real money.\n" +
                            "• We DO NOT sell insurance or share medical data with third parties.\n" +
                            "• All calculations and AI extractions are user-verifiable prototype simulations.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = Color(0xFF334155),
                    lineHeight = 17.sp
                )

                Divider(color = Color(0xFFE2E8F0))

                ConsentRow(
                    checked = consentSensitive,
                    onCheckedChange = { consentSensitive = it },
                    text = "I understand that uploaded health information is sensitive and belongs to my family."
                )
                ConsentRow(
                    checked = consentAi,
                    onCheckedChange = { consentAi = it },
                    text = "I consent to using AI to organize my family's uploaded documents."
                )
                ConsentRow(
                    checked = consentVerify,
                    onCheckedChange = { consentVerify = it },
                    text = "I understand that AI-generated summaries should always be verified against original documents."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier.testTag("save_consent_button")
            ) {
                Text("Confirm Consent Settings")
            }
        }
    )
}

@Composable
private fun ConsentRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0284C7))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = Color(0xFF1E293B)
        )
    }
}

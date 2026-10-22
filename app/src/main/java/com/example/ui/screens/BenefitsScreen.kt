package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BenefitCategory
import com.example.model.BenefitStatus
import com.example.model.HealthBenefitScheme
import com.example.ui.HealthShieldViewModel
import com.example.ui.components.DisclaimerBanner

@Composable
fun BenefitsScreen(
    viewModel: HealthShieldViewModel,
    modifier: Modifier = Modifier
) {
    val schemes by viewModel.schemes.collectAsState()
    var selectedState by remember { mutableStateOf("All India / Central") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("benefits_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Government & Health Benefits",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "“Check what healthcare protection your family may be eligible for.”",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }
        }

        // Section 14: "My Health Protection" Visual Wallet
        item {
            HealthProtectionWalletHeader(schemes = schemes)
        }

        // Mandatory Disclaimer
        item {
            DisclaimerBanner()
        }

        // Scheme Cards List
        items(schemes, key = { it.id }) { scheme ->
            BenefitSchemeCard(scheme = scheme)
        }

        // Section 27: ABHA / ABDM Architecture Explanation
        item {
            AbdmArchitectureCard()
        }

        // Section 28: Empanelment & Coverage Reality Notice
        item {
            EmpanelmentNoticeCard()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun HealthProtectionWalletHeader(schemes: List<HealthBenefitScheme>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("my_health_protection_wallet"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2540)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF0284C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "My Health Protection Wallet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Surface(
                    color = Color(0xFF1E3A8A),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${schemes.size} PROTECTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = Color(0xFFBAE6FD),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick summary pills of the cards in wallet
            schemes.forEach { scheme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(scheme.status.chipColor), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = scheme.name,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFFE2E8F0)
                        )
                    }

                    Text(
                        text = scheme.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                        color = Color(scheme.status.chipColor)
                    )
                }
            }
        }
    }
}

@Composable
fun BenefitSchemeCard(scheme: HealthBenefitScheme) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("benefit_scheme_card_${scheme.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scheme.category.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = scheme.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                }

                Surface(
                    color = Color(scheme.status.chipColor).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = scheme.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = Color(scheme.status.chipColor),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Coverage Benefit:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                    Text(text = scheme.coverageAmount, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0284C7))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scheme.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF334155),
                lineHeight = 18.sp
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Eligibility Criteria:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = scheme.eligibilitySummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Covered Scope:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    scheme.coveredServices.forEach { service ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(14.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = service, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color(0xFF334155))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = scheme.disclaimer,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                            color = Color(0xFF78350F),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Show Less" else "View Eligibility & Details",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF0284C7)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AbdmArchitectureCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFE0E7FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lan, contentDescription = null, tint = Color(0xFF4338CA), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ABHA / ABDM Integration Architecture",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Future Authorized National Health Stack Integration",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "In a future production version, HealthShield could explore authorized integration with India's Ayushman Bharat Digital Mission (ABDM) to enable consent-based access to eligible digital health records. HealthShield does not replace ABHA; rather, it complements digital records by mapping them to healthcare financial resilience.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF334155),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun EmpanelmentNoticeCard() {
    Surface(
        color = Color(0xFFEFF6FF),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(20.dp).padding(top = 2.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Empanelled Facility & Co-Pay Realities",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1E40AF)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "A government card or private policy does not mean all medicines and procedures are 100% free everywhere. Actual out-of-pocket exposure depends on hospital empanelment tiers, surgical package caps, non-medical consumables, and deductible limits.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color(0xFF1E3A8A),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

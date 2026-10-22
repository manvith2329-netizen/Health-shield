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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DocumentCategory
import com.example.model.FamilyMember
import com.example.model.HealthRecord
import com.example.model.HealthStatus
import com.example.model.Relationship
import com.example.ui.HealthShieldViewModel
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.formatWithCommas

@Composable
fun FamilyScreen(
    viewModel: HealthShieldViewModel,
    modifier: Modifier = Modifier
) {
    val members by viewModel.members.collectAsState()
    val records by viewModel.records.collectAsState()
    val selectedMember by viewModel.selectedMemberForDetail.collectAsState()
    val showAddDialog by viewModel.showAddMemberDialog.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setAddMemberDialog(true) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Family Member", fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFF0284C7),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_family_member_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("family_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Your Family Health Overview",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Who are you preparing for? Dynamic health situations and protection mapping across all dependents.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            item {
                DisclaimerBanner()
            }

            items(members, key = { it.id }) { member ->
                val memberRecords = records.filter { it.memberId == member.id }
                FamilyMemberCard(
                    member = member,
                    recordCount = memberRecords.size,
                    onClick = { viewModel.openMemberDetail(member) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp)) // padding for FAB
            }
        }
    }

    // Individual Member Detail Dialog / Sheet
    selectedMember?.let { member ->
        val memberRecords = records.filter { it.memberId == member.id }
        MemberDetailDialog(
            member = member,
            records = memberRecords,
            onDismiss = { viewModel.closeMemberDetail() }
        )
    }

    // Add Family Member Dialog
    if (showAddDialog) {
        AddFamilyMemberDialog(
            onDismiss = { viewModel.setAddMemberDialog(false) },
            onAdd = { name, age, gender, rel, status, conditions, meds, allergies, hospitalizations, ins, expense ->
                viewModel.addFamilyMember(name, age, gender, rel, status, conditions, meds, allergies, hospitalizations, ins, expense)
            }
        )
    }
}

@Composable
fun FamilyMemberCard(
    member: FamilyMember,
    recordCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("family_member_card_${member.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(member.avatarColorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.name.take(1).uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${member.relationship.displayName} • ${member.age} years • ${member.gender}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Health Status Label (Section 8)
                HealthStatusBadge(status = member.healthStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Condition / Medicines highlights
            if (member.conditions.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Healing, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = member.conditions.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF334155)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (member.recurringMedicines.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocalPharmacy, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = member.recurringMedicines.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Summary info: Monthly expense + reports count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Est. Monthly Expense: ₹${member.estimatedMonthlyExpense.formatWithCommas()}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF0284C7)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$recordCount reports",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF64748B)
                    )
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun HealthStatusBadge(status: HealthStatus) {
    val (bgColor, textColor) = when (status) {
        HealthStatus.STABLE -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        HealthStatus.NEEDS_ATTENTION -> Color(0xFFFEF3C7) to Color(0xFFB45309)
        HealthStatus.RECURRING_CARE -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
        HealthStatus.HIGH_EXPOSURE -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun MemberDetailDialog(
    member: FamilyMember,
    records: List<HealthRecord>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("member_detail_dialog"),
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
                Text("Close Profile")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "${member.relationship.displayName} • ${member.age} yrs",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
                HealthStatusBadge(status = member.healthStatus)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section 1: Health Summary
                item {
                    DetailSectionCard(title = "Health Summary") {
                        DetailItem("Conditions", member.conditions.ifEmpty { listOf("None reported") }.joinToString(", "))
                        DetailItem("Recurring Medicines", member.recurringMedicines.ifEmpty { listOf("None") }.joinToString(", "))
                        DetailItem("Known Allergies", member.allergies.ifEmpty { listOf("No known allergies") }.joinToString(", "))
                        DetailItem("Past Hospitalization", member.hospitalizationHistory.ifEmpty { listOf("No major hospitalization") }.joinToString(", "))
                        DetailItem("Protection Status", member.insuranceStatus)
                        DetailItem("Est. Monthly Healthcare Expense", "₹${member.estimatedMonthlyExpense.formatWithCommas()} / month")
                    }
                }

                // Section 2: Medical Timeline (Section 9)
                item {
                    DetailSectionCard(title = "Medical Timeline & Recent Reports (${records.size})") {
                        if (records.isEmpty()) {
                            Text("No reports uploaded yet for this member.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        } else {
                            records.forEach { record ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = record.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F172A))
                                        Text(text = "${record.date} • ${record.category.displayName}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp), color = Color(0xFF64748B))
                                        record.keyMetric?.let {
                                            Text(text = it, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold), color = Color(0xFF0D9488))
                                        }
                                    }
                                }
                                Divider(color = Color(0xFFF1F5F9))
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Organizational record only. Does not constitute medical diagnosis.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    )
}

@Composable
fun DetailSectionCard(title: String, content: @Composable () -> Unit) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF64748B))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = Color(0xFF1E293B))
    }
}

@Composable
fun AddFamilyMemberDialog(
    onDismiss: () -> Unit,
    onAdd: (
        name: String,
        age: Int,
        gender: String,
        rel: Relationship,
        status: HealthStatus,
        conditions: List<String>,
        meds: List<String>,
        allergies: List<String>,
        hospitalizations: List<String>,
        insurance: String,
        expense: Int
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var rel by remember { mutableStateOf(Relationship.CHILD) }
    var conditionsStr by remember { mutableStateOf("") }
    var medsStr by remember { mutableStateOf("") }
    var expenseStr by remember { mutableStateOf("500") }
    var insurance by remember { mutableStateOf("Covered under Family Floater") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("add_family_member_dialog"),
        title = {
            Text("Add Family Member", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth().testTag("add_member_name_input")
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = ageStr,
                            onValueChange = { ageStr = it },
                            label = { Text("Age") },
                            modifier = Modifier.weight(1f).testTag("add_member_age_input")
                        )
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text("Gender") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = conditionsStr,
                        onValueChange = { conditionsStr = it },
                        label = { Text("Conditions (e.g. Asthma, Diabetes)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = medsStr,
                        onValueChange = { medsStr = it },
                        label = { Text("Recurring Medicines (e.g. Inhaler SOS)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = expenseStr,
                        onValueChange = { expenseStr = it },
                        label = { Text("Est. Monthly Medical Expense (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = insurance,
                        onValueChange = { insurance = it },
                        label = { Text("Insurance / Protection Status") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val age = ageStr.toIntOrNull() ?: 25
                    val exp = expenseStr.toIntOrNull() ?: 500
                    val conditionsList = conditionsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val medsList = medsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val healthStatus = when {
                        medsList.isNotEmpty() && conditionsList.isNotEmpty() -> HealthStatus.RECURRING_CARE
                        conditionsList.isNotEmpty() -> HealthStatus.NEEDS_ATTENTION
                        else -> HealthStatus.STABLE
                    }
                    onAdd(name, age, gender, rel, healthStatus, conditionsList, medsList, emptyList(), emptyList(), insurance, exp)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier.testTag("confirm_add_member_button")
            ) {
                Text("Add Member")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

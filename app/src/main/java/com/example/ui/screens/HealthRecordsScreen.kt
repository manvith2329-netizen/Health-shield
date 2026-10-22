package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DocumentCategory
import com.example.model.HealthRecord
import com.example.ui.HealthShieldViewModel
import com.example.ui.components.DisclaimerBanner

@Composable
fun HealthRecordsScreen(
    viewModel: HealthShieldViewModel,
    modifier: Modifier = Modifier
) {
    val records by viewModel.records.collectAsState()
    val members by viewModel.members.collectAsState()
    val filterMemberId by viewModel.filterMemberId.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showAiOrganizer by viewModel.showAiOrganizerDialog.collectAsState()
    val isSimulatingAi by viewModel.isSimulatingAiExtraction.collectAsState()
    val lastExtracted by viewModel.lastExtractedRecord.collectAsState()

    // Filtered records
    val filteredRecords = records.filter { record ->
        val matchesMember = filterMemberId == null || record.memberId == filterMemberId
        val matchesCategory = filterCategory == null || record.category == filterCategory
        val matchesSearch = searchQuery.isBlank() ||
            record.title.contains(searchQuery, ignoreCase = true) ||
            record.memberName.contains(searchQuery, ignoreCase = true) ||
            record.testOrProcedure.contains(searchQuery, ignoreCase = true) ||
            record.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesMember && matchesCategory && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setAiOrganizerDialog(true) },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                text = { Text("Organize with AI", fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFF0D9488),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("organize_with_ai_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("health_records_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Family Health Timeline",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Chronological repository connecting medical tests, prescriptions, and hospital discharge summaries.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Search Bar (Section 25)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search records: 'blood test', 'inhaler', '2026'...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_records_input")
                )
            }

            // Filter Chips by Family Member (Section 11)
            item {
                Column {
                    Text(text = "Filter by Family Member:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = filterMemberId == null,
                                onClick = { viewModel.setFilterMember(null) },
                                label = { Text("All Family") }
                            )
                        }
                        items(members, key = { it.id }) { m ->
                            FilterChip(
                                selected = filterMemberId == m.id,
                                onClick = {
                                    viewModel.setFilterMember(if (filterMemberId == m.id) null else m.id)
                                },
                                label = { Text(m.name.split(" ").first()) }
                            )
                        }
                    }
                }
            }

            // Filter Chips by Category (Section 9 & 11)
            item {
                Column {
                    Text(text = "Filter by Document Type:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = filterCategory == null,
                                onClick = { viewModel.setFilterCategory(null) },
                                label = { Text("All Types") }
                            )
                        }
                        items(DocumentCategory.values().toList(), key = { it.name }) { cat ->
                            FilterChip(
                                selected = filterCategory == cat,
                                onClick = {
                                    viewModel.setFilterCategory(if (filterCategory == cat) null else cat)
                                },
                                label = { Text(cat.displayName) }
                            )
                        }
                    }
                }
            }

            // Disclaimer Banner
            item {
                DisclaimerBanner()
            }

            // Records List
            if (filteredRecords.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No health records match your filters", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(onClick = {
                                viewModel.setFilterMember(null)
                                viewModel.setFilterCategory(null)
                                viewModel.setSearchQuery("")
                            }) {
                                Text("Clear all filters")
                            }
                        }
                    }
                }
            } else {
                items(filteredRecords, key = { it.id }) { record ->
                    HealthRecordTimelineCard(record = record)
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // AI Organizer Dialog (Section 10)
    if (showAiOrganizer) {
        AiOrganizerDialog(
            members = members,
            isSimulating = isSimulatingAi,
            lastExtracted = lastExtracted,
            onDismiss = { viewModel.setAiOrganizerDialog(false) },
            onSimulateUpload = { docType, memId, memName ->
                viewModel.simulateAiDocumentProcessing(docType, memId, memName)
            }
        )
    }
}

@Composable
fun HealthRecordTimelineCard(record: HealthRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("record_card_${record.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = record.category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = Color(0xFF0369A1),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${record.date} • ${record.memberName}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = record.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Provider / Facility: ${record.facilityOrDoctor}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF475569)
            )

            record.keyMetric?.let { metric ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Key Extracted Metric: $metric",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF15803D)
                        )
                        record.referenceRange?.let { ref ->
                            Text(
                                text = "Reference: $ref",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = Color(0xFF166534)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = record.summary,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF334155),
                lineHeight = 17.sp
            )

            if (record.isAiExtracted) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI-generated summary — verify against original report",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                        color = Color(0xFF0D9488)
                    )
                }
            }
        }
    }
}

@Composable
fun AiOrganizerDialog(
    members: List<com.example.model.FamilyMember>,
    isSimulating: Boolean,
    lastExtracted: HealthRecord?,
    onDismiss: () -> Unit,
    onSimulateUpload: (sampleType: String, memberId: String, memberName: String) -> Unit
) {
    var selectedSample by remember { mutableStateOf("Blood Test HbA1c") }
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("ai_organizer_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFCCFBF1), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Organize Health Document with AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Upload simulated medical report (PDF, JPG, prescription image). The AI organizer structures key dates, metrics, and categories for your family timeline.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569)
                )

                Text(text = "1. Select Family Member:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1E293B))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(members, key = { it.id }) { m ->
                        FilterChip(
                            selected = selectedMember?.id == m.id,
                            onClick = { selectedMember = m },
                            label = { Text(m.name.split(" ").first()) }
                        )
                    }
                }

                Text(text = "2. Select Document Sample to Ingest:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1E293B))
                val samples = listOf("Blood Test HbA1c", "Prescription Inhaler", "Discharge Summary")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    samples.forEach { sample ->
                        FilterChip(
                            selected = selectedSample == sample,
                            onClick = { selectedSample = sample },
                            label = { Text(sample.split(" ").first(), fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (isSimulating) {
                    Surface(
                        color = Color(0xFFF0FDFA),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF0D9488), strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Analyzing document…", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F766E))
                                Text("Extracting dates, parameters, and reference ranges", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Color(0xFF115E59))
                            }
                        }
                    }
                }

                lastExtracted?.let { extracted ->
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Extraction Successful!", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF166534))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Title: ${extracted.title}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFF0F172A))
                            extracted.keyMetric?.let {
                                Text("Metric: $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFF15803D))
                            }
                            Text("Date: ${extracted.date} • ${extracted.category.displayName}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Color(0xFF64748B))
                        }
                    }
                }

                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Safety Note: HealthShield AI extracts structured parameters and dates. It does NOT diagnose diseases, prescribe medications, or recommend treatment changes.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                        color = Color(0xFF78350F),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedMember?.let { m ->
                        onSimulateUpload(selectedSample, m.id, m.name)
                    }
                },
                enabled = !isSimulating && selectedMember != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                modifier = Modifier.testTag("process_document_ai_button")
            ) {
                Text(if (isSimulating) "Processing..." else "Process Document")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

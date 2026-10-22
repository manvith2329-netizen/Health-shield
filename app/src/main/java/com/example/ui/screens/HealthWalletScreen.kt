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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.model.HealthTransaction
import com.example.model.TransactionType
import com.example.ui.HealthShieldViewModel
import com.example.ui.components.BeforeAfterSimulationView
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.ShockSimulatorWidget
import com.example.ui.components.formatWithCommas

@Composable
fun HealthWalletScreen(
    viewModel: HealthShieldViewModel,
    modifier: Modifier = Modifier
) {
    val walletBalance by viewModel.walletBalance.collectAsState()
    val monthlyTarget by viewModel.monthlySavingsTarget.collectAsState()
    val dailyTarget by viewModel.dailySavingsTarget.collectAsState()
    val monthlySpending by viewModel.monthlySpending.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val selectedShock by viewModel.selectedShockAmount.collectAsState()
    val shockResult by viewModel.shockResult.collectAsState()
    val beforeAfterProjection by viewModel.beforeAfterProjection.collectAsState()
    val showAddTxDialog by viewModel.showAddTransactionDialog.collectAsState()

    var showSetTargetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("health_wallet_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 15: Health Savings Wallet Card
        item {
            HealthSavingsWalletCard(
                balance = walletBalance,
                monthlyTarget = monthlyTarget,
                dailyTarget = dailyTarget,
                suggestedBuffer = 50000,
                onAddQuickSaving = { viewModel.addDailySavingQuick() },
                onAddExpense = { viewModel.setAddTransactionDialog(true) },
                onSetTarget = { showSetTargetDialog = true }
            )
        }

        // Section 17: Monthly Healthcare Spending Breakdown
        item {
            MonthlySpendingBreakdownCard(
                totalSpending = monthlySpending,
                transactions = transactions
            )
        }

        // Section 18: Full Healthcare Financial Shock Simulator
        item {
            ShockSimulatorWidget(
                selectedShock = selectedShock,
                accessibleSavings = walletBalance,
                result = shockResult,
                onSelectAmount = { viewModel.setShockAmount(it) }
            )
        }

        // Section 22: Before & After Simulation
        item {
            BeforeAfterSimulationView(
                projection = beforeAfterProjection,
                onDailyChange = { viewModel.setSimulationDailySaving(it) },
                onDaysChange = { viewModel.setSimulationDays(it) }
            )
        }

        // Section 16: Transaction Ledger
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_ledger_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulated Wallet Transactions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PROTOTYPE LEDGER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    transactions.forEach { tx ->
                        TransactionRowItem(tx = tx)
                        Divider(color = Color(0xFFF8FAFC))
                    }
                }
            }
        }

        item {
            DisclaimerBanner()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Add Transaction Dialog
    if (showAddTxDialog) {
        AddExpenseOrSavingDialog(
            onDismiss = { viewModel.setAddTransactionDialog(false) },
            onAdd = { desc, amount, type, member, notes ->
                viewModel.addManualTransaction(desc, amount, type, member, "Health Wallet", notes)
            }
        )
    }

    // Set Target Dialog
    if (showSetTargetDialog) {
        SetTargetDialog(
            currentTarget = monthlyTarget,
            onDismiss = { showSetTargetDialog = false },
            onSave = {
                viewModel.setMonthlyTarget(it)
                showSetTargetDialog = false
            }
        )
    }
}

@Composable
fun HealthSavingsWalletCard(
    balance: Int,
    monthlyTarget: Int,
    dailyTarget: Int,
    suggestedBuffer: Int,
    onAddQuickSaving: () -> Unit,
    onAddExpense: () -> Unit,
    onSetTarget: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("health_savings_wallet_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF0284C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Health Savings Wallet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Dedicated healthcare safety buffer",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Illustrative calculation",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Balance Display
            Text(
                text = "Current Health Savings",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "₹${balance.formatWithCommas()}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 34.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "buffered",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4ADE80),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Buffer Goal Progress
            val progress = (balance.toFloat() / suggestedBuffer).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Safety Buffer Goal: ₹${suggestedBuffer.formatWithCommas()}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color(0xFFCBD5E1))
                Text(text = "${(progress * 100).toInt()}% funded", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF0284C7),
                trackColor = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Targets Info Row
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Monthly Target", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF94A3B8))
                        Text(text = "₹${monthlyTarget.formatWithCommas()}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                    Column {
                        Text(text = "Daily Equivalent", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF94A3B8))
                        Text(text = "₹$dailyTarget / day", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
                    }
                    IconButton(onClick = onSetTarget) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit target", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddQuickSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_daily_saving_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ ₹$dailyTarget Daily", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onAddExpense,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("record_healthcare_expense_button")
                ) {
                    Text("Record Expense", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Prototype wallet — no real money is transferred.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color(0xFF64748B),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MonthlySpendingBreakdownCard(
    totalSpending: Int,
    transactions: List<HealthTransaction>
) {
    val medicineSum = transactions.filter { it.type == TransactionType.MEDICINE }.sumOf { it.amount }
    val diagnosticSum = transactions.filter { it.type == TransactionType.DIAGNOSTIC_TEST }.sumOf { it.amount }
    val consultSum = transactions.filter { it.type == TransactionType.DOCTOR_CONSULTATION }.sumOf { it.amount }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("monthly_spending_breakdown_card"),
        shape = RoundedCornerShape(18.dp),
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
                        text = "Monthly Healthcare Spending",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Categorized family out-of-pocket medical cashflow",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
                Text(
                    text = "₹${totalSpending.formatWithCommas()}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SpendingCategoryRow(category = "Medicines & Pharmacy", amount = medicineSum, color = Color(0xFF0D9488))
            SpendingCategoryRow(category = "Diagnostics & Lab Tests", amount = diagnosticSum, color = Color(0xFF0284C7))
            SpendingCategoryRow(category = "Doctor Consultations", amount = consultSum.coerceAtLeast(800), color = Color(0xFFD97706))
        }
    }
}

@Composable
private fun SpendingCategoryRow(category: String, amount: Int, color: Color) {
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
                    .size(10.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = category, style = MaterialTheme.typography.bodySmall, color = Color(0xFF334155))
        }
        Text(text = "₹${amount.formatWithCommas()}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F172A))
    }
}

@Composable
fun TransactionRowItem(tx: HealthTransaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (tx.type.isCredit) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (tx.type.isCredit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (tx.type.isCredit) Color(0xFF15803D) else Color(0xFFDC2626),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = tx.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "${tx.date} • ${tx.type.displayName}${tx.memberName?.let { " ($it)" } ?: ""}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                    color = Color(0xFF64748B)
                )
            }
        }

        Text(
            text = (if (tx.type.isCredit) "+ ₹" else "− ₹") + tx.amount.formatWithCommas(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (tx.type.isCredit) Color(0xFF15803D) else Color(0xFFDC2626)
        )
    }
}

@Composable
fun AddExpenseOrSavingDialog(
    onDismiss: () -> Unit,
    onAdd: (desc: String, amount: Int, type: TransactionType, member: String?, notes: String) -> Unit
) {
    var desc by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("1200") }
    var type by remember { mutableStateOf(TransactionType.MEDICINE) }
    var member by remember { mutableStateOf("Rajesh Sharma") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Healthcare Transaction", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description (e.g. Pharmacy medicines)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = member,
                    onValueChange = { member = it },
                    label = { Text("Family Member") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toIntOrNull() ?: 500
                    onAdd(desc, amount, type, member, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
                Text("Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SetTargetDialog(
    currentTarget: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var targetStr by remember { mutableStateOf(currentTarget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Monthly Health Buffer Target", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Set how much this family plans to set aside each month for healthcare reserves.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Monthly Target (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newTarget = targetStr.toIntOrNull() ?: 3000
                    onSave(newTarget)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
                Text("Save Target")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

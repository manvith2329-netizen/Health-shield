package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HealthShieldViewModel
import com.example.ui.NavigationTab
import com.example.ui.dialogs.HealthShieldAiDialog
import com.example.ui.dialogs.JudgeDemoWalkthroughDialog
import com.example.ui.dialogs.PrivacyConsentDialog
import com.example.ui.dialogs.ReadinessReportDialog
import com.example.ui.dialogs.ResearchMethodologyDialog
import com.example.ui.screens.BenefitsScreen
import com.example.ui.screens.FamilyScreen
import com.example.ui.screens.HealthRecordsScreen
import com.example.ui.screens.HealthWalletScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.HealthShieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthShieldTheme {
                HealthShieldApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthShieldApp(
    viewModel: HealthShieldViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showJudgeDemo by viewModel.showJudgeDemoWalkthrough.collectAsState()
    val judgeStep by viewModel.judgeDemoStep.collectAsState()
    val showReport by viewModel.showReadinessReportDialog.collectAsState()
    val showResearch by viewModel.showResearchMethodologyDialog.collectAsState()
    val showPrivacy by viewModel.showPrivacyConsentDialog.collectAsState()
    val showAiAssistant by viewModel.showHealthShieldAiAssistant.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    // Handle system back navigation to return to Home
    BackHandler(enabled = currentTab != NavigationTab.HOME) {
        viewModel.selectTab(NavigationTab.HOME)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF0284C7), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "HealthShield",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Family Health & Financial Safety Net",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                actions = {
                    // Quick Judge Demo Button
                    Button(
                        onClick = { viewModel.startJudgeDemo() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("appbar_judge_demo_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("2-Min Demo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // HealthShield AI Assistant
                    IconButton(
                        onClick = { viewModel.setHealthShieldAiAssistant(true) },
                        modifier = Modifier.testTag("appbar_ai_assistant_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "HealthShield AI",
                            tint = Color(0xFF0284C7)
                        )
                    }

                    // Readiness Report
                    IconButton(
                        onClick = { viewModel.setReadinessReportDialog(true) },
                        modifier = Modifier.testTag("appbar_readiness_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Family Report",
                            tint = Color(0xFF475569)
                        )
                    }

                    // Overflow Menu
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color(0xFF475569))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Research Methodology") },
                                onClick = {
                                    showMenu = false
                                    viewModel.setResearchMethodologyDialog(true)
                                },
                                leadingIcon = { Icon(Icons.Default.Science, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Privacy & Consent") },
                                onClick = {
                                    showMenu = false
                                    viewModel.setPrivacyConsentDialog(true)
                                },
                                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Reset Demo Data") },
                                onClick = {
                                    showMenu = false
                                    viewModel.resetDemoData()
                                },
                                leadingIcon = { Icon(Icons.Default.RestartAlt, contentDescription = null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                val tabs = listOf(
                    Triple(NavigationTab.HOME, "Home", Icons.Default.Home),
                    Triple(NavigationTab.FAMILY, "Family", Icons.Default.People),
                    Triple(NavigationTab.RECORDS, "Records", Icons.Default.Description),
                    Triple(NavigationTab.WALLET, "Wallet", Icons.Default.AccountBalanceWallet),
                    Triple(NavigationTab.BENEFITS, "Benefits", Icons.Default.VerifiedUser)
                )

                tabs.forEach { (tab, title, icon) ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = title
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0284C7),
                            selectedTextColor = Color(0xFF0284C7),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B),
                            indicatorColor = Color(0xFFE0F2FE)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            when (currentTab) {
                NavigationTab.HOME -> HomeScreen(viewModel = viewModel)
                NavigationTab.FAMILY -> FamilyScreen(viewModel = viewModel)
                NavigationTab.RECORDS -> HealthRecordsScreen(viewModel = viewModel)
                NavigationTab.WALLET -> HealthWalletScreen(viewModel = viewModel)
                NavigationTab.BENEFITS -> BenefitsScreen(viewModel = viewModel)
            }
        }
    }

    // Interactive Dialog Overlays
    if (showJudgeDemo) {
        JudgeDemoWalkthroughDialog(
            step = judgeStep,
            onNext = { viewModel.nextJudgeDemoStep() },
            onDismiss = { viewModel.dismissJudgeDemo() }
        )
    }

    if (showReport) {
        ReadinessReportDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setReadinessReportDialog(false) }
        )
    }

    if (showResearch) {
        ResearchMethodologyDialog(
            onDismiss = { viewModel.setResearchMethodologyDialog(false) }
        )
    }

    if (showPrivacy) {
        PrivacyConsentDialog(
            onDismiss = { viewModel.setPrivacyConsentDialog(false) }
        )
    }

    if (showAiAssistant) {
        HealthShieldAiDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setHealthShieldAiAssistant(false) }
        )
    }
}

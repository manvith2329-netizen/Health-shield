package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculation.ResilienceEngine
import com.example.data.HealthShieldRepository
import com.example.model.DocumentCategory
import com.example.model.DominantVulnerabilityType
import com.example.model.FamilyMember
import com.example.model.HealthBenefitScheme
import com.example.model.HealthRecord
import com.example.model.HealthStatus
import com.example.model.HealthTransaction
import com.example.model.ReadinessAssessment
import com.example.model.Relationship
import com.example.model.TransactionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationTab(val title: String, val iconName: String) {
    HOME("Home", "Home"),
    FAMILY("Family", "People"),
    RECORDS("Records", "Description"),
    WALLET("Wallet", "AccountBalanceWallet"),
    BENEFITS("Benefits", "VerifiedUser")
}

class HealthShieldViewModel(
    private val repository: HealthShieldRepository = HealthShieldRepository()
) : ViewModel() {

    val members: StateFlow<List<FamilyMember>> = repository.members
    val records: StateFlow<List<HealthRecord>> = repository.records
    val transactions: StateFlow<List<HealthTransaction>> = repository.transactions
    val schemes: StateFlow<List<HealthBenefitScheme>> = repository.schemes
    val selectedShockAmount: StateFlow<Int> = repository.selectedShockAmount
    val monthlySavingsTarget: StateFlow<Int> = repository.monthlySavingsTarget
    val dailySavingsTarget: StateFlow<Int> = repository.dailySavingsTarget

    // Current navigation state
    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    // Dialog & overlay states
    private val _selectedMemberForDetail = MutableStateFlow<FamilyMember?>(null)
    val selectedMemberForDetail: StateFlow<FamilyMember?> = _selectedMemberForDetail.asStateFlow()

    private val _showAddMemberDialog = MutableStateFlow(false)
    val showAddMemberDialog: StateFlow<Boolean> = _showAddMemberDialog.asStateFlow()

    private val _showAiOrganizerDialog = MutableStateFlow(false)
    val showAiOrganizerDialog: StateFlow<Boolean> = _showAiOrganizerDialog.asStateFlow()

    private val _showAddTransactionDialog = MutableStateFlow(false)
    val showAddTransactionDialog: StateFlow<Boolean> = _showAddTransactionDialog.asStateFlow()

    private val _showJudgeDemoWalkthrough = MutableStateFlow(false)
    val showJudgeDemoWalkthrough: StateFlow<Boolean> = _showJudgeDemoWalkthrough.asStateFlow()

    private val _judgeDemoStep = MutableStateFlow(0)
    val judgeDemoStep: StateFlow<Int> = _judgeDemoStep.asStateFlow()

    private val _showReadinessReportDialog = MutableStateFlow(false)
    val showReadinessReportDialog: StateFlow<Boolean> = _showReadinessReportDialog.asStateFlow()

    private val _showResearchMethodologyDialog = MutableStateFlow(false)
    val showResearchMethodologyDialog: StateFlow<Boolean> = _showResearchMethodologyDialog.asStateFlow()

    private val _showPrivacyConsentDialog = MutableStateFlow(false)
    val showPrivacyConsentDialog: StateFlow<Boolean> = _showPrivacyConsentDialog.asStateFlow()

    private val _showHealthShieldAiAssistant = MutableStateFlow(false)
    val showHealthShieldAiAssistant: StateFlow<Boolean> = _showHealthShieldAiAssistant.asStateFlow()

    // Records filtering & search
    private val _filterMemberId = MutableStateFlow<String?>(null)
    val filterMemberId: StateFlow<String?> = _filterMemberId.asStateFlow()

    private val _filterCategory = MutableStateFlow<DocumentCategory?>(null)
    val filterCategory: StateFlow<DocumentCategory?> = _filterCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // AI Organizer simulated state
    private val _isSimulatingAiExtraction = MutableStateFlow(false)
    val isSimulatingAiExtraction: StateFlow<Boolean> = _isSimulatingAiExtraction.asStateFlow()

    private val _lastExtractedRecord = MutableStateFlow<HealthRecord?>(null)
    val lastExtractedRecord: StateFlow<HealthRecord?> = _lastExtractedRecord.asStateFlow()

    // Interactive Before/After projection sliders
    private val _customSimulationDailySaving = MutableStateFlow(100)
    val customSimulationDailySaving: StateFlow<Int> = _customSimulationDailySaving.asStateFlow()

    private val _customSimulationDays = MutableStateFlow(60)
    val customSimulationDays: StateFlow<Int> = _customSimulationDays.asStateFlow()

    // Private trigger to refresh computed balances
    private val _transactionsChangedTrigger = MutableStateFlow(0L)

    // Computed Wallet Balance
    val walletBalance: StateFlow<Int> = transactions.combine(_transactionsChangedTrigger) { txList, _ ->
        calculateWalletBalance(txList)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 18500)

    // Monthly Healthcare Spending
    val monthlySpending: StateFlow<Int> = transactions.combine(_transactionsChangedTrigger) { txList, _ ->
        txList.filter { !it.type.isCredit }
            .sumOf { (it.amount - it.insuranceReimbursement).coerceAtLeast(0) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 4700)

    // Readiness Assessment
    val readinessAssessment: StateFlow<ReadinessAssessment> = combine(
        members,
        walletBalance,
        schemes,
        monthlySpending
    ) { memberList, balance, schemeList, spending ->
        ResilienceEngine.assessReadiness(memberList, balance, schemeList, spending)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ResilienceEngine.assessReadiness(emptyList(), 18500, emptyList(), 4700)
    )

    // Shock Simulator Result
    val shockResult: StateFlow<ResilienceEngine.ShockCalculationResult> = combine(
        selectedShockAmount,
        walletBalance,
        schemes
    ) { shock, balance, schemeList ->
        // Protection availability estimate from schemes
        val availableCoverage = 20000 // In prototype: baseline eligible in-patient protection
        ResilienceEngine.calculateShock(shock, balance, availableCoverage)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ResilienceEngine.calculateShock(50000, 18500, 20000)
    )

    // Before/After Simulation Projection
    val beforeAfterProjection: StateFlow<ResilienceEngine.BeforeAfterProjection> = combine(
        walletBalance,
        customSimulationDailySaving,
        customSimulationDays,
        selectedShockAmount
    ) { balance, daily, days, shock ->
        ResilienceEngine.calculateBeforeAfter(
            currentSavings = balance,
            dailyRate = daily,
            days = days,
            shockAmount = shock,
            protectionAvailable = 20000
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ResilienceEngine.calculateBeforeAfter(18500, 100, 60, 50000, 20000)
    )

    // AI Assistant Grounded Q&A
    private val _aiAssistantMessages = MutableStateFlow<List<Pair<Boolean, String>>>(
        listOf(
            false to "Hello! I am HealthShield AI. I can answer questions about your family's uploaded health records, recurring medical expenses, protection status, and simulated healthcare shock exposure.\n\nNote: I do not provide medical diagnosis, prescriptions, or replace doctors."
        )
    )
    val aiAssistantMessages: StateFlow<List<Pair<Boolean, String>>> = _aiAssistantMessages.asStateFlow()

    // Navigation and tab selection
    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun openMemberDetail(member: FamilyMember) {
        _selectedMemberForDetail.value = member
    }

    fun closeMemberDetail() {
        _selectedMemberForDetail.value = null
    }

    fun setAddMemberDialog(show: Boolean) {
        _showAddMemberDialog.value = show
    }

    fun setAiOrganizerDialog(show: Boolean) {
        _showAiOrganizerDialog.value = show
        if (!show) {
            _lastExtractedRecord.value = null
        }
    }

    fun setAddTransactionDialog(show: Boolean) {
        _showAddTransactionDialog.value = show
    }

    fun setReadinessReportDialog(show: Boolean) {
        _showReadinessReportDialog.value = show
    }

    fun setResearchMethodologyDialog(show: Boolean) {
        _showResearchMethodologyDialog.value = show
    }

    fun setPrivacyConsentDialog(show: Boolean) {
        _showPrivacyConsentDialog.value = show
    }

    fun setHealthShieldAiAssistant(show: Boolean) {
        _showHealthShieldAiAssistant.value = show
    }

    fun setFilterMember(memberId: String?) {
        _filterMemberId.value = memberId
    }

    fun setFilterCategory(cat: DocumentCategory?) {
        _filterCategory.value = cat
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setShockAmount(amount: Int) {
        repository.setShockAmount(amount)
    }

    fun setSimulationDailySaving(amount: Int) {
        _customSimulationDailySaving.value = amount
    }

    fun setSimulationDays(days: Int) {
        _customSimulationDays.value = days
    }

    fun addFamilyMember(
        name: String,
        age: Int,
        gender: String,
        relationship: Relationship,
        healthStatus: HealthStatus,
        conditions: List<String>,
        medicines: List<String>,
        allergies: List<String>,
        hospitalizations: List<String>,
        insurance: String,
        monthlyExpense: Int
    ) {
        val newMember = FamilyMember(
            id = "m_" + UUID.randomUUID().toString().take(6),
            name = name.ifBlank { "Family Member" },
            age = age.coerceAtLeast(1),
            gender = gender,
            relationship = relationship,
            healthStatus = healthStatus,
            conditions = conditions,
            recurringMedicines = medicines,
            allergies = allergies,
            hospitalizationHistory = hospitalizations,
            insuranceStatus = insurance,
            estimatedMonthlyExpense = monthlyExpense,
            lastReportUpdate = "Recently added",
            avatarColorHex = 0xFF0284C7
        )
        repository.addFamilyMember(newMember)
        _showAddMemberDialog.value = false
    }

    fun addManualTransaction(
        description: String,
        amount: Int,
        type: TransactionType,
        memberName: String?,
        paidBy: String,
        notes: String
    ) {
        val tx = HealthTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(6),
            date = "21 Oct 2026",
            description = description.ifBlank { type.displayName },
            amount = amount.coerceAtLeast(1),
            type = type,
            memberName = memberName,
            paidBy = paidBy,
            insuranceReimbursement = 0,
            notes = notes
        )
        repository.addTransaction(tx)
        _transactionsChangedTrigger.value = System.currentTimeMillis()
        _showAddTransactionDialog.value = false
    }

    fun addDailySavingQuick() {
        val tx = HealthTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(6),
            date = "Today",
            description = "Daily Health Emergency Reserve",
            amount = dailySavingsTarget.value,
            type = TransactionType.DAILY_SAVING,
            paidBy = "Auto-save / UPI"
        )
        repository.addTransaction(tx)
        _transactionsChangedTrigger.value = System.currentTimeMillis()
    }

    fun simulateAiDocumentProcessing(
        sampleType: String,
        memberId: String,
        memberName: String
    ) {
        viewModelScope.launch {
            _isSimulatingAiExtraction.value = true
            _lastExtractedRecord.value = null
            delay(1500) // Realistic scanning feedback

            val extracted = when (sampleType) {
                "Blood Test HbA1c" -> HealthRecord(
                    id = "r_" + UUID.randomUUID().toString().take(6),
                    memberId = memberId,
                    memberName = memberName,
                    title = "Glycated Hemoglobin (HbA1c) Panel",
                    category = DocumentCategory.BLOOD_TESTS,
                    date = "21 Oct 2026",
                    year = 2026,
                    testOrProcedure = "HbA1c & Plasma Glucose",
                    keyMetric = "HbA1c: 6.9% (Fair Control)",
                    referenceRange = "< 5.7% (Normal), 5.7-6.4% (Pre-diabetic)",
                    facilityOrDoctor = "Apex Diagnostics Lab",
                    summary = "Extracted: HbA1c is 6.9%. Fasting blood sugar noted at 128 mg/dL. Down from 7.2% in August.",
                    isAiExtracted = true,
                    tags = listOf("HbA1c", "Diabetes", "Blood Test")
                )
                "Prescription Inhaler" -> HealthRecord(
                    id = "r_" + UUID.randomUUID().toString().take(6),
                    memberId = memberId,
                    memberName = memberName,
                    title = "Pediatric Pulmonology Renewal Rx",
                    category = DocumentCategory.PRESCRIPTIONS,
                    date = "20 Oct 2026",
                    year = 2026,
                    testOrProcedure = "Asthma Maintenance Review",
                    keyMetric = "Budesonide 200mcg BID",
                    referenceRange = null,
                    facilityOrDoctor = "Dr. V. Rao",
                    summary = "Extracted: Budesonide inhaler renewal for 60 days. SOS Salbutamol spacer instructed. Follow-up after winter.",
                    isAiExtracted = true,
                    tags = listOf("Prescription", "Inhaler", "Asthma")
                )
                "Discharge Summary" -> HealthRecord(
                    id = "r_" + UUID.randomUUID().toString().take(6),
                    memberId = memberId,
                    memberName = memberName,
                    title = "Hospital Observation & Discharge Summary",
                    category = DocumentCategory.DISCHARGE_SUMMARIES,
                    date = "19 Oct 2026",
                    year = 2026,
                    testOrProcedure = "24-Hr Observation for Acute Bronchospasm",
                    keyMetric = "Discharged Hemodynamically Stable",
                    referenceRange = null,
                    facilityOrDoctor = "City Care Hospital",
                    summary = "Extracted: Patient observed for wheezing flare-up. Nebulized and discharged with home management plan. Total bill ₹14,200.",
                    isAiExtracted = true,
                    tags = listOf("Discharge Summary", "Observation", "Hospitalization")
                )
                else -> HealthRecord(
                    id = "r_" + UUID.randomUUID().toString().take(6),
                    memberId = memberId,
                    memberName = memberName,
                    title = "Clinical Diagnostic Report",
                    category = DocumentCategory.OTHER,
                    date = "21 Oct 2026",
                    year = 2026,
                    testOrProcedure = "Routine Health Assessment",
                    keyMetric = "All Vititals Normal",
                    referenceRange = null,
                    facilityOrDoctor = "Community Health Clinic",
                    summary = "Extracted: Structured parameters recorded. No acute alerts detected. Document verified for family timeline.",
                    isAiExtracted = true,
                    tags = listOf("Health Report")
                )
            }

            _lastExtractedRecord.value = extracted
            repository.addHealthRecord(extracted)
            _isSimulatingAiExtraction.value = false
        }
    }

    // Judge 2-Minute Guided Demo Flow
    fun startJudgeDemo() {
        repository.resetToDemoFamily()
        _judgeDemoStep.value = 0
        _showJudgeDemoWalkthrough.value = true
        _currentTab.value = NavigationTab.HOME
    }

    fun nextJudgeDemoStep() {
        val next = _judgeDemoStep.value + 1
        if (next > 4) {
            _showJudgeDemoWalkthrough.value = false
        } else {
            _judgeDemoStep.value = next
            when (next) {
                1 -> _currentTab.value = NavigationTab.FAMILY
                2 -> _currentTab.value = NavigationTab.RECORDS
                3 -> _currentTab.value = NavigationTab.BENEFITS
                4 -> {
                    _currentTab.value = NavigationTab.WALLET
                    repository.setShockAmount(50000)
                }
            }
        }
    }

    fun dismissJudgeDemo() {
        _showJudgeDemoWalkthrough.value = false
    }

    fun resetDemoData() {
        repository.resetToDemoFamily()
        _transactionsChangedTrigger.value = System.currentTimeMillis()
    }

    // HealthShield AI Assistant Q&A
    fun askAiAssistant(question: String) {
        val userQ = question.trim()
        if (userQ.isBlank()) return

        val currentMsgs = _aiAssistantMessages.value.toMutableList()
        currentMsgs.add(true to userQ)
        _aiAssistantMessages.value = currentMsgs

        val lower = userQ.lowercase()
        val response = when {
            "father" in lower || "rajesh" in lower -> {
                "Father (Rajesh Sharma, 52) has 3 organized records, including an HbA1c test (7.2% on 12 Aug 2026) and a quarterly prescription for Metformin & Telmisartan. Estimated monthly medicine expense is ₹2,400."
            }
            "medicine" in lower || "spend" in lower || "expense" in lower -> {
                "Your family recorded ₹4,700 in healthcare expenses last month across pharmacy (₹2,400 for chronic medicines) and diagnostics (₹2,300 for knee X-rays and HbA1c panels)."
            }
            "recurring" in lower -> {
                "Based on the family profile, two members have active recurring needs: Rajesh Sharma (Type 2 Diabetes & Hypertension) and Savitri Devi (Osteoarthritis & Hypertension), totaling ~₹4,200/mo."
            }
            "50,000" in lower || "shock" in lower || "emergency" in lower -> {
                "If a ₹50,000 healthcare shock occurs: you have ₹${walletBalance.value} in immediately accessible health savings, and ~₹20,000 estimated eligible scheme/insurance protection, leaving an estimated out-of-pocket gap of ~₹11,500."
            }
            "action" in lower || "vulnerability" in lower || "do next" in lower -> {
                "Your family's dominant vulnerability is Liquidity Vulnerability. The most practical recommended action is saving ₹100/day toward your health safety reserve, which will accumulate ₹6,000 over 60 days to cushion unexpected medical costs."
            }
            else -> {
                "Based on your prototype family data (5 members, 18 records, ₹${walletBalance.value} savings, Family Readiness Score 72/100): your records are organized and protection is mapped. You can simulate shock amounts in the Health Wallet tab or view detailed profiles in the Family tab."
            }
        }

        viewModelScope.launch {
            delay(400)
            _aiAssistantMessages.value = _aiAssistantMessages.value + (false to response)
        }
    }

    fun setMonthlyTarget(amount: Int) {
        repository.setMonthlyTarget(amount)
        _transactionsChangedTrigger.value = System.currentTimeMillis()
    }

    fun setDailyTarget(amount: Int) {
        repository.setDailyTarget(amount)
        _transactionsChangedTrigger.value = System.currentTimeMillis()
    }

    private fun calculateWalletBalance(txList: List<HealthTransaction>): Int {
        var balance = 0
        for (tx in txList) {
            if (tx.type.isCredit) {
                balance += tx.amount
            } else {
                balance -= (tx.amount - tx.insuranceReimbursement).coerceAtLeast(0)
            }
        }
        return balance
    }
}

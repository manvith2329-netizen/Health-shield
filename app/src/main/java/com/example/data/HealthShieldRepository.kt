package com.example.data

import com.example.model.BenefitCategory
import com.example.model.BenefitStatus
import com.example.model.DocumentCategory
import com.example.model.FamilyMember
import com.example.model.HealthBenefitScheme
import com.example.model.HealthRecord
import com.example.model.HealthStatus
import com.example.model.HealthTransaction
import com.example.model.Relationship
import com.example.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class HealthShieldRepository {

    private val _members = MutableStateFlow<List<FamilyMember>>(createInitialMembers())
    val members: StateFlow<List<FamilyMember>> = _members.asStateFlow()

    private val _records = MutableStateFlow<List<HealthRecord>>(createInitialRecords())
    val records: StateFlow<List<HealthRecord>> = _records.asStateFlow()

    private val _transactions = MutableStateFlow<List<HealthTransaction>>(createInitialTransactions())
    val transactions: StateFlow<List<HealthTransaction>> = _transactions.asStateFlow()

    private val _schemes = MutableStateFlow<List<HealthBenefitScheme>>(createInitialSchemes())
    val schemes: StateFlow<List<HealthBenefitScheme>> = _schemes.asStateFlow()

    private val _monthlySavingsTarget = MutableStateFlow(3000)
    val monthlySavingsTarget: StateFlow<Int> = _monthlySavingsTarget.asStateFlow()

    private val _dailySavingsTarget = MutableStateFlow(100)
    val dailySavingsTarget: StateFlow<Int> = _dailySavingsTarget.asStateFlow()

    private val _selectedShockAmount = MutableStateFlow(50000)
    val selectedShockAmount: StateFlow<Int> = _selectedShockAmount.asStateFlow()

    fun setShockAmount(amount: Int) {
        _selectedShockAmount.value = amount
    }

    fun setMonthlyTarget(amount: Int) {
        _monthlySavingsTarget.value = amount
        _dailySavingsTarget.value = (amount / 30).coerceAtLeast(10)
    }

    fun setDailyTarget(amount: Int) {
        _dailySavingsTarget.value = amount
        _monthlySavingsTarget.value = amount * 30
    }

    fun addFamilyMember(member: FamilyMember) {
        _members.value = _members.value + member
    }

    fun updateFamilyMember(updated: FamilyMember) {
        _members.value = _members.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteFamilyMember(memberId: String) {
        _members.value = _members.value.filterNot { it.id == memberId }
        _records.value = _records.value.filterNot { it.memberId == memberId }
    }

    fun addHealthRecord(record: HealthRecord) {
        _records.value = listOf(record) + _records.value
        // Update member's last report date
        val member = _members.value.find { it.id == record.memberId }
        if (member != null) {
            updateFamilyMember(member.copy(lastReportUpdate = record.date))
        }
    }

    fun addTransaction(tx: HealthTransaction) {
        _transactions.value = listOf(tx) + _transactions.value
    }

    fun resetToDemoFamily() {
        _members.value = createInitialMembers()
        _records.value = createInitialRecords()
        _transactions.value = createInitialTransactions()
        _schemes.value = createInitialSchemes()
        _selectedShockAmount.value = 50000
    }

    fun getWalletBalance(): Int {
        var balance = 0
        for (tx in _transactions.value) {
            if (tx.type.isCredit) {
                balance += tx.amount
            } else {
                balance -= (tx.amount - tx.insuranceReimbursement).coerceAtLeast(0)
            }
        }
        return balance
    }

    fun getMonthlyHealthcareSpending(): Int {
        return _transactions.value
            .filter { !it.type.isCredit }
            .sumOf { (it.amount - it.insuranceReimbursement).coerceAtLeast(0) }
    }

    companion object {
        private fun createInitialMembers(): List<FamilyMember> = listOf(
            FamilyMember(
                id = "m1",
                name = "Rajesh Sharma",
                age = 52,
                gender = "Male",
                relationship = Relationship.PRIMARY,
                healthStatus = HealthStatus.RECURRING_CARE,
                conditions = listOf("Type 2 Diabetes", "Mild Hypertension"),
                recurringMedicines = listOf("Metformin 500mg (Daily)", "Telmisartan 40mg (Morning)"),
                allergies = listOf("Sulfa drugs"),
                hospitalizationHistory = listOf("Gallbladder procedure (Nov 2024)"),
                insuranceStatus = "Covered under Family Floater ₹5L",
                estimatedMonthlyExpense = 2400,
                lastReportUpdate = "12 Aug 2026",
                avatarColorHex = 0xFF0284C7
            ),
            FamilyMember(
                id = "m2",
                name = "Priya Sharma",
                age = 48,
                gender = "Female",
                relationship = Relationship.SPOUSE,
                healthStatus = HealthStatus.STABLE,
                conditions = emptyList(),
                recurringMedicines = listOf("Vitamin D3 & Calcium (Weekly)"),
                allergies = emptyList(),
                hospitalizationHistory = emptyList(),
                insuranceStatus = "Covered under Family Floater ₹5L",
                estimatedMonthlyExpense = 400,
                lastReportUpdate = "18 Jul 2026",
                avatarColorHex = 0xFF0D9488
            ),
            FamilyMember(
                id = "m3",
                name = "Aarav Sharma",
                age = 15,
                gender = "Male",
                relationship = Relationship.CHILD,
                healthStatus = HealthStatus.NEEDS_ATTENTION,
                conditions = listOf("Allergic Asthma"),
                recurringMedicines = listOf("Budesonide Inhaler (SOS/Seasonal)"),
                allergies = listOf("Dust mites", "Pollen"),
                hospitalizationHistory = listOf("Nebulizer observation (Oct 2025)"),
                insuranceStatus = "Covered under Family Floater ₹5L",
                estimatedMonthlyExpense = 600,
                lastReportUpdate = "04 Jun 2026",
                avatarColorHex = 0xFF6366F1
            ),
            FamilyMember(
                id = "m4",
                name = "Meera Sharma",
                age = 9,
                gender = "Female",
                relationship = Relationship.CHILD,
                healthStatus = HealthStatus.STABLE,
                conditions = emptyList(),
                recurringMedicines = emptyList(),
                allergies = emptyList(),
                hospitalizationHistory = emptyList(),
                insuranceStatus = "Covered under Family Floater ₹5L",
                estimatedMonthlyExpense = 200,
                lastReportUpdate = "22 Jan 2026",
                avatarColorHex = 0xFFEC4899
            ),
            FamilyMember(
                id = "m5",
                name = "Savitri Devi",
                age = 71,
                gender = "Female",
                relationship = Relationship.GRANDPARENT,
                healthStatus = HealthStatus.HIGH_EXPOSURE,
                conditions = listOf("Osteoarthritis (Knees)", "Essential Hypertension"),
                recurringMedicines = listOf("Amlodipine 5mg", "Pain Relief Gel", "Joint supplement"),
                allergies = listOf("Penicillin"),
                hospitalizationHistory = listOf("Cataract Surgery (Jan 2025)"),
                insuranceStatus = "Senior Citizen Separate Rider (Under Verification)",
                estimatedMonthlyExpense = 1800,
                lastReportUpdate = "30 Sep 2026",
                avatarColorHex = 0xFFF59E0B
            )
        )

        private fun createInitialRecords(): List<HealthRecord> = listOf(
            HealthRecord(
                id = "r1",
                memberId = "m1",
                memberName = "Rajesh Sharma",
                title = "Glycated Hemoglobin (HbA1c) Profile",
                category = DocumentCategory.BLOOD_TESTS,
                date = "12 Aug 2026",
                year = 2026,
                testOrProcedure = "HbA1c & Fasting Glucose",
                keyMetric = "HbA1c: 7.2%",
                referenceRange = "< 5.7% (Normal), 5.7-6.4% (Pre-diabetic), > 6.5% (Diabetic)",
                facilityOrDoctor = "Apex Diagnostics Lab",
                summary = "AI Extracted: HbA1c is 7.2% indicating moderate glycemic control. Fasting blood sugar noted at 134 mg/dL. Stable compared to May 2026.",
                isAiExtracted = true,
                tags = listOf("Diabetes", "HbA1c", "Blood Test")
            ),
            HealthRecord(
                id = "r2",
                memberId = "m1",
                memberName = "Rajesh Sharma",
                title = "Endocrinology Quarterly Prescription",
                category = DocumentCategory.PRESCRIPTIONS,
                date = "15 Aug 2026",
                year = 2026,
                testOrProcedure = "Physician Consultation & Rx",
                keyMetric = "Metformin 500mg BID",
                referenceRange = null,
                facilityOrDoctor = "Dr. K. Mehta (Endocrinologist)",
                summary = "AI Extracted: Continue Metformin 500mg twice daily with meals. Advised 30 mins brisk walking. Re-check lipid profile in November.",
                isAiExtracted = true,
                tags = listOf("Prescription", "Metformin")
            ),
            HealthRecord(
                id = "r3",
                memberId = "m2",
                memberName = "Priya Sharma",
                title = "Comprehensive Annual Health Checkup",
                category = DocumentCategory.BLOOD_TESTS,
                date = "18 Jul 2026",
                year = 2026,
                testOrProcedure = "Complete Hemogram & Thyroid Profile",
                keyMetric = "TSH: 2.4 uIU/mL (Normal)",
                referenceRange = "0.4 - 4.2 uIU/mL",
                facilityOrDoctor = "City Care Hospital",
                summary = "AI Extracted: Hemoglobin 12.8 g/dL, TSH 2.4 uIU/mL within reference limits. Mild Vitamin D insufficiency recommended oral supplementation.",
                isAiExtracted = true,
                tags = listOf("Annual Checkup", "Thyroid")
            ),
            HealthRecord(
                id = "r4",
                memberId = "m3",
                memberName = "Aarav Sharma",
                title = "Pulmonology Asthma Review & Spirometry",
                category = DocumentCategory.PRESCRIPTIONS,
                date = "04 Jun 2026",
                year = 2026,
                testOrProcedure = "Peak Flow & Inhaler Review",
                keyMetric = "PEFR: 380 L/min",
                referenceRange = "Predicted: 400 L/min",
                facilityOrDoctor = "Dr. V. Rao (Pediatric Pulmonologist)",
                summary = "AI Extracted: Budesonide 100mcg inhaler renewed. Instructed to keep emergency salbutamol inhaler in school bag during monsoon.",
                isAiExtracted = true,
                tags = listOf("Asthma", "Inhaler", "Pediatric")
            ),
            HealthRecord(
                id = "r5",
                memberId = "m5",
                memberName = "Savitri Devi",
                title = "Bilateral Knee Digital X-Ray",
                category = DocumentCategory.IMAGING,
                date = "30 Sep 2026",
                year = 2026,
                testOrProcedure = "X-Ray Both Knees (Standing AP/Lat)",
                keyMetric = "Grade II Osteoarthritis",
                referenceRange = "Kellgren-Lawrence Scale",
                facilityOrDoctor = "Metro Imaging Center",
                summary = "AI Extracted: Bilateral medial compartment joint space narrowing with marginal osteophytes. No acute fracture or dislocation.",
                isAiExtracted = true,
                tags = listOf("X-Ray", "Orthopedic", "Knees")
            ),
            HealthRecord(
                id = "r6",
                memberId = "m5",
                memberName = "Savitri Devi",
                title = "Cardiology BP & ECG Monitoring",
                category = DocumentCategory.HOSPITAL_REPORTS,
                date = "14 Sep 2026",
                year = 2026,
                testOrProcedure = "12-Lead ECG & BP Check",
                keyMetric = "BP: 136/84 mmHg",
                referenceRange = "< 130/80 mmHg",
                facilityOrDoctor = "Heart & Vascular Clinic",
                summary = "AI Extracted: Normal sinus rhythm. Blood pressure controlled on current Amlodipine regimen. Sodium restriction advised.",
                isAiExtracted = true,
                tags = listOf("Cardiology", "Hypertension", "ECG")
            ),
            HealthRecord(
                id = "r7",
                memberId = "m4",
                memberName = "Meera Sharma",
                title = "School Immunization & Dental Certificate",
                category = DocumentCategory.VACCINATION,
                date = "22 Jan 2026",
                year = 2026,
                testOrProcedure = "MMR Booster & Dental Check",
                keyMetric = "Vaccines Up to Date",
                referenceRange = null,
                facilityOrDoctor = "Dr. Shobha (Pediatrician)",
                summary = "AI Extracted: Age-appropriate vaccinations verified up to date. Mild dental plaque removed, good oral hygiene maintained.",
                isAiExtracted = true,
                tags = listOf("Vaccination", "Pediatric")
            ),
            HealthRecord(
                id = "r8",
                memberId = "m1",
                memberName = "Rajesh Sharma",
                title = "Gallbladder Laparoscopic Discharge Summary",
                category = DocumentCategory.DISCHARGE_SUMMARIES,
                date = "14 Nov 2024",
                year = 2024,
                testOrProcedure = "Laparoscopic Cholecystectomy",
                keyMetric = "Uneventful Recovery",
                referenceRange = null,
                facilityOrDoctor = "Apollo Specialty Hospital",
                summary = "AI Extracted: Discharged in stable condition following elective gallstone procedure. Cashless claim of ₹78,000 processed under corporate insurance.",
                isAiExtracted = true,
                tags = listOf("Hospitalization", "Discharge Summary", "Surgery")
            ),
            HealthRecord(
                id = "r9",
                memberId = "m1",
                memberName = "Rajesh Sharma",
                title = "Family Health Floater Policy Schedule",
                category = DocumentCategory.INSURANCE,
                date = "01 Jan 2026",
                year = 2026,
                testOrProcedure = "Policy Renewal (₹5,00,000 Sum Insured)",
                keyMetric = "Policy Active: Valid to Dec 2026",
                referenceRange = "No Claim Bonus: 20%",
                facilityOrDoctor = "Star Health Insurance",
                summary = "AI Extracted: Policy covers self, spouse and 2 dependent children. Base sum insured ₹5,00,000. Cumulative bonus ₹1,00,000. Pre-existing disease waiting period passed.",
                isAiExtracted = true,
                tags = listOf("Insurance", "Policy Schedule", "Floater")
            )
        )

        private fun createInitialTransactions(): List<HealthTransaction> = listOf(
            HealthTransaction(
                id = "tx1",
                date = "20 Oct 2026",
                description = "Daily Health Emergency Reserve",
                amount = 100,
                type = TransactionType.DAILY_SAVING,
                paidBy = "Auto-save / UPI"
            ),
            HealthTransaction(
                id = "tx2",
                date = "19 Oct 2026",
                description = "Daily Health Emergency Reserve",
                amount = 100,
                type = TransactionType.DAILY_SAVING,
                paidBy = "Auto-save / UPI"
            ),
            HealthTransaction(
                id = "tx3",
                date = "01 Oct 2026",
                description = "Monthly Family Healthcare Buffer",
                amount = 2500,
                type = TransactionType.MONTHLY_CONTRIBUTION,
                paidBy = "Bank Transfer"
            ),
            HealthTransaction(
                id = "tx4",
                date = "15 Oct 2026",
                description = "Father & Grandmother Chronic Medicines",
                amount = 2400,
                type = TransactionType.MEDICINE,
                memberId = "m1",
                memberName = "Rajesh & Savitri",
                paidBy = "Health Wallet",
                insuranceReimbursement = 0,
                notes = "Metformin, Telmisartan, Amlodipine monthly supply"
            ),
            HealthTransaction(
                id = "tx5",
                date = "30 Sep 2026",
                description = "Bilateral Knee X-Ray & Orthopedic Consult",
                amount = 1500,
                type = TransactionType.DIAGNOSTIC_TEST,
                memberId = "m5",
                memberName = "Savitri Devi",
                paidBy = "Health Wallet",
                insuranceReimbursement = 0
            ),
            HealthTransaction(
                id = "tx6",
                date = "12 Aug 2026",
                description = "HbA1c & Fasting Glucose Blood Panel",
                amount = 800,
                type = TransactionType.DIAGNOSTIC_TEST,
                memberId = "m1",
                memberName = "Rajesh Sharma",
                paidBy = "Health Wallet",
                insuranceReimbursement = 0
            )
        )

        private fun createInitialSchemes(): List<HealthBenefitScheme> = listOf(
            HealthBenefitScheme(
                id = "sch1",
                name = "Ayushman Bharat PM-JAY",
                category = BenefitCategory.CENTRAL_GOVERNMENT,
                coverageAmount = "₹5,00,000 / family / year",
                status = BenefitStatus.POTENTIALLY_APPLICABLE,
                description = "Secondary & tertiary secondary hospitalization coverage across empanelled public and private hospitals across India.",
                eligibilitySummary = "Eligibility determined by SECC socio-economic registry, ration card category, and verified state health inclusion list.",
                coveredServices = listOf(
                    "Medical examination, treatment and consultation",
                    "Pre and post hospitalization (up to 15 days)",
                    "Medicines and medical consumables during admission",
                    "ICU and high-dependency unit care"
                ),
                requiredDocuments = listOf("Aadhaar Card", "Ration Card / PM Letter", "Family Member ID Proof"),
                disclaimer = "HealthShield does not independently grant government benefits. Verification must occur at authorized hospital kiosk (Ayushman Mitra) or official PM-JAY portal."
            ),
            HealthBenefitScheme(
                id = "sch2",
                name = "State Chief Minister Health Assurance",
                category = BenefitCategory.STATE_GOVERNMENT,
                coverageAmount = "₹2,00,000 - ₹5,00,000",
                status = BenefitStatus.NEEDS_VERIFICATION,
                description = "State-level health scheme subsidizing high-end procedures and catastrophic treatments for resident households.",
                eligibilitySummary = "Requires active domicile proof, valid state ration card, or registration under local welfare boards.",
                coveredServices = listOf(
                    "Surgical packages for heart, kidney and cancer",
                    "Emergency trauma care",
                    "Specialized pediatric interventions"
                ),
                requiredDocuments = listOf("State Domicile Certificate", "Food Security Card", "Physician referral slip"),
                disclaimer = "Empanelled private hospital packages vary by tier. Scheme rules dictate exact co-payments."
            ),
            FamilyBenefitInsurance(),
            HealthBenefitScheme(
                id = "sch4",
                name = "Employer Corporate Health Plan",
                category = BenefitCategory.EMPLOYER_COVERAGE,
                coverageAmount = "₹3,00,000 Group Floater",
                status = BenefitStatus.AVAILABLE,
                description = "Corporate group medical insurance covering active employee, spouse, and up to 2 children.",
                eligibilitySummary = "Active corporate employment verified. Corporate TPA cashless card issued.",
                coveredServices = listOf(
                    "Cashless hospitalization at network hospitals",
                    "Daycare procedures",
                    "Maternity and newborn cover (with sub-limits)"
                ),
                requiredDocuments = listOf("Corporate Employee ID", "TPA E-Card", "Government Photo ID"),
                disclaimer = "Coverage ceases upon employment transition unless ported to an individual retail plan within 30 days."
            ),
            HealthBenefitScheme(
                id = "sch5",
                name = "ABHA / ABDM Digital Health Identity",
                category = BenefitCategory.DIGITAL_HEALTH_ID,
                coverageAmount = "Digital Health Records (Not Insurance)",
                status = BenefitStatus.FUTURE_INTEGRATION,
                description = "Ayushman Bharat Digital Mission (ABDM) standard consent-based repository for linking digital lab reports and prescriptions.",
                eligibilitySummary = "14-digit ABHA number linked with mobile and Aadhaar for health record sharing.",
                coveredServices = listOf(
                    "Consent-driven digital report access",
                    "Paperless hospital registration",
                    "Interoperable health timeline across clinics"
                ),
                requiredDocuments = listOf("Aadhaar number or Mobile OTP verification via official NHA ABDM gateway"),
                disclaimer = "Future authorized integration. Note: ABHA does not provide financial or insurance coverage on its own; it facilitates digital records access."
            )
        )

        private fun FamilyBenefitInsurance() = HealthBenefitScheme(
            id = "sch3",
            name = "Family Health Floater (Private Retail)",
            category = BenefitCategory.PRIVATE_INSURANCE,
            coverageAmount = "₹5,00,000 + ₹1,00,000 Bonus",
            status = BenefitStatus.AVAILABLE,
            description = "Private comprehensive health indemnity floater covering hospitalization, room rent, ICU, and day care treatments.",
            eligibilitySummary = "Active premium paid. Continuous coverage with waiting periods completed.",
            coveredServices = listOf(
                "In-patient hospitalization > 24 hours",
                "Pre-hospitalization (60 days) & Post-hospitalization (90 days)",
                "Ambulance charges up to ₹2,000 per admission",
                "Annual health checkup voucher"
            ),
            requiredDocuments = listOf("Policy schedule", "TPA cashless card", "Doctor prescription and admission advice"),
            disclaimer = "Subject to policy terms, non-medical deductibles, room rent caps, and proportional expense clauses."
        )
    }
}

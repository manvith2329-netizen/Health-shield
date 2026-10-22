package com.example.model

/**
 * HealthShield Data Models
 * Represents family structure, health status, structured records,
 * healthcare transactions, protection schemes, and financial shock states.
 */

enum class Relationship(val displayName: String) {
    PRIMARY("Primary User"),
    SPOUSE("Spouse"),
    CHILD("Child"),
    PARENT("Parent"),
    GRANDPARENT("Grandparent"),
    OTHER("Dependent")
}

enum class HealthStatus(val label: String, val description: String) {
    STABLE("Stable", "Routine monitoring, no immediate high-risk alerts"),
    NEEDS_ATTENTION("Needs Attention", "Recent diagnostic follow-up or pending review required"),
    RECURRING_CARE("Recurring Care", "Active chronic management with monthly medical expenses"),
    HIGH_EXPOSURE("High Healthcare Exposure", "Multiple conditions or frequent hospitalization risk")
}

data class FamilyMember(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val relationship: Relationship,
    val healthStatus: HealthStatus,
    val conditions: List<String> = emptyList(),
    val recurringMedicines: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val hospitalizationHistory: List<String> = emptyList(),
    val insuranceStatus: String = "Not Covered",
    val estimatedMonthlyExpense: Int = 0,
    val lastReportUpdate: String = "No reports uploaded",
    val avatarColorHex: Long = 0xFF0284C7
)

enum class DocumentCategory(val displayName: String) {
    BLOOD_TESTS("Blood Tests"),
    IMAGING("Imaging & Scans"),
    PRESCRIPTIONS("Prescriptions"),
    HOSPITAL_REPORTS("Hospital Reports"),
    DISCHARGE_SUMMARIES("Discharge Summaries"),
    VACCINATION("Vaccination"),
    INSURANCE("Insurance Docs"),
    OTHER("Other")
}

data class HealthRecord(
    val id: String,
    val memberId: String,
    val memberName: String,
    val title: String,
    val category: DocumentCategory,
    val date: String,
    val year: Int,
    val testOrProcedure: String,
    val keyMetric: String? = null,
    val referenceRange: String? = null,
    val facilityOrDoctor: String,
    val summary: String,
    val isAiExtracted: Boolean = true,
    val tags: List<String> = emptyList()
)

enum class TransactionType(val displayName: String, val isCredit: Boolean) {
    DAILY_SAVING("Daily Health Saving", true),
    MONTHLY_CONTRIBUTION("Monthly Health Buffer", true),
    DOCTOR_CONSULTATION("Doctor Consultation", false),
    MEDICINE("Pharmacy & Medicines", false),
    DIAGNOSTIC_TEST("Diagnostic Test / Lab", false),
    HOSPITALIZATION("Hospitalization Care", false),
    SURGERY("Surgery / Procedure", false),
    INSURANCE_PREMIUM("Health Insurance Premium", false),
    OTHER_HEALTHCARE("Other Healthcare Expense", false)
}

data class HealthTransaction(
    val id: String,
    val date: String,
    val description: String,
    val amount: Int, // absolute amount
    val type: TransactionType,
    val memberId: String? = null,
    val memberName: String? = null,
    val paidBy: String = "Health Wallet",
    val insuranceReimbursement: Int = 0,
    val notes: String = ""
)

enum class BenefitCategory(val label: String) {
    CENTRAL_GOVERNMENT("Central Government Scheme"),
    STATE_GOVERNMENT("State Health Scheme"),
    PRIVATE_INSURANCE("Private Health Insurance"),
    EMPLOYER_COVERAGE("Employer Group Health"),
    DIGITAL_HEALTH_ID("Digital Health Infrastructure")
}

enum class BenefitStatus(val label: String, val chipColor: Long) {
    POTENTIALLY_APPLICABLE("Potentially Applicable", 0xFF0284C7),
    NEEDS_VERIFICATION("Needs Verification", 0xFFD97706),
    AVAILABLE("Available / Active", 0xFF0D9488),
    CONNECTED("Connected", 0xFF10B981),
    FUTURE_INTEGRATION("Future Authorized Integration", 0xFF64748B)
}

data class HealthBenefitScheme(
    val id: String,
    val name: String,
    val category: BenefitCategory,
    val coverageAmount: String,
    val status: BenefitStatus,
    val description: String,
    val eligibilitySummary: String,
    val coveredServices: List<String>,
    val requiredDocuments: List<String>,
    val disclaimer: String
)

enum class DominantVulnerabilityType(
    val title: String,
    val explanation: String,
    val recommendedAction: String,
    val primaryIconName: String
) {
    LIQUIDITY(
        title = "Liquidity Vulnerability",
        explanation = "Your family may have healthcare protection, but the amount of immediately accessible savings is insufficient for a moderate unexpected medical expense.",
        recommendedAction = "Build a dedicated healthcare savings reserve gradually (e.g. ₹50–₹100/day).",
        primaryIconName = "Savings"
    ),
    COVERAGE_UNCERTAINTY(
        title = "Coverage Uncertainty",
        explanation = "Your family has healthcare protection, but the exact coverage available for specific procedures, deductibles, and empanelled hospitals is unverified.",
        recommendedAction = "Review policy/scheme inclusions, claim conditions, and verify active eligibility.",
        primaryIconName = "VerifiedUser"
    ),
    RECURRING_BURDEN(
        title = "Recurring Healthcare Burden",
        explanation = "Recurring monthly medicines and chronic care expenses consume a high share of your household healthcare cashflow, depleting rainy-day buffers.",
        recommendedAction = "Track recurring expenses, explore generic medicine alternatives, and check chronic care government subsidies.",
        primaryIconName = "MedicalServices"
    ),
    DEBT_PRESSURE(
        title = "Debt & Out-of-Pocket Exposure",
        explanation = "High fixed obligations paired with a wide out-of-pocket gap increase the probability of high-cost emergency distress borrowing.",
        recommendedAction = "Prioritize liquid healthcare buffer contributions before discretionary commitments.",
        primaryIconName = "AccountBalance"
    )
}

data class ReadinessAssessment(
    val overallScore: Int, // 0 - 100
    val readinessLabel: String, // "Strongly Prepared", "Moderately Prepared", "Vulnerable"
    val emergencyLiquidityScore: Int, // %
    val healthcareProtectionScore: Int, // %
    val recurringBurdenScore: Int, // %
    val coverageUnderstandingScore: Int, // %
    val healthSavingsScore: Int, // %
    val dominantVulnerability: DominantVulnerabilityType,
    val explainabilityFactors: List<ExplainabilityFactor>
)

data class ExplainabilityFactor(
    val factor: String,
    val impact: String,
    val scoreContribution: String,
    val detail: String
)

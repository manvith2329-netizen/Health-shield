package com.example.calculation

import com.example.model.DominantVulnerabilityType
import com.example.model.ExplainabilityFactor
import com.example.model.FamilyMember
import com.example.model.HealthBenefitScheme
import com.example.model.HealthTransaction
import com.example.model.ReadinessAssessment
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * ResilienceEngine - Deterministic Calculation Engine
 * 
 * Research Prototype Methodology:
 * Computes household healthcare resilience across five empirical dimensions:
 * 1. Emergency Liquidity: Immediately available dedicated health cash buffer relative to shock baselines.
 * 2. Healthcare Protection: Extent and reliability of active private/government scheme coverage.
 * 3. Recurring Burden: Chronic medication and doctor consultation strain on disposable healthcare budget.
 * 4. Coverage Understanding: Verification clarity and empanelment awareness of policies.
 * 5. Savings Momentum: Discipline of regular health buffer accumulation (daily/monthly).
 * 
 * Note: Prototype methodology — subject to ongoing public health & health economics validation.
 */
object ResilienceEngine {

    const val DEFAULT_TARGET_BUFFER = 50000

    data class ShockCalculationResult(
        val shockAmount: Int,
        val accessibleSavings: Int,
        val potentialProtection: Int,
        val remainingExposure: Int,
        val absorptionRatePercent: Int,
        val notes: String
    )

    data class BeforeAfterProjection(
        val currentSavings: Int,
        val dailySavingsRate: Int,
        val horizonDays: Int,
        val shockAmount: Int,
        val potentialProtection: Int,
        val currentGap: Int,
        val additionalSavings: Int,
        val projectedSavings: Int,
        val projectedGap: Int,
        val reductionInGapPercent: Int
    )

    fun calculateShock(
        shockAmount: Int,
        accessibleSavings: Int,
        protectionAvailable: Int
    ): ShockCalculationResult {
        // Protection usually absorbs up to a deductible / cap or proportion of in-patient care
        // In prototype: protection applies up to 60-80% of eligible in-patient costs or up to protection policy limit
        val applicableProtection = min(protectionAvailable, (shockAmount * 0.70).roundToInt())
        val totalBuffer = accessibleSavings + applicableProtection
        val remainingExposure = max(0, shockAmount - totalBuffer)
        val absorbed = min(shockAmount, totalBuffer)
        val absorptionRate = if (shockAmount > 0) ((absorbed.toDouble() / shockAmount) * 100).roundToInt() else 100

        val notes = when {
            remainingExposure == 0 -> "Fully buffered! Existing health savings and protection cover this simulated shock amount."
            remainingExposure <= 15000 -> "Manageable gap. Can be absorbed with short-term cash flow adjustments."
            else -> "High financial shock exposure. Risk of emergency debt or distress liquidation."
        }

        return ShockCalculationResult(
            shockAmount = shockAmount,
            accessibleSavings = accessibleSavings,
            potentialProtection = applicableProtection,
            remainingExposure = remainingExposure,
            absorptionRatePercent = absorptionRate,
            notes = notes
        )
    }

    fun calculateBeforeAfter(
        currentSavings: Int,
        dailyRate: Int,
        days: Int,
        shockAmount: Int,
        protectionAvailable: Int
    ): BeforeAfterProjection {
        val applicableProtection = min(protectionAvailable, (shockAmount * 0.70).roundToInt())
        val currentTotal = currentSavings + applicableProtection
        val currentGap = max(0, shockAmount - currentTotal)

        val additionalSavings = dailyRate * days
        val projectedSavings = currentSavings + additionalSavings
        val projectedTotal = projectedSavings + applicableProtection
        val projectedGap = max(0, shockAmount - projectedTotal)

        val gapDiff = currentGap - projectedGap
        val reductionPercent = if (currentGap > 0) ((gapDiff.toDouble() / currentGap) * 100).roundToInt() else 100

        return BeforeAfterProjection(
            currentSavings = currentSavings,
            dailySavingsRate = dailyRate,
            horizonDays = days,
            shockAmount = shockAmount,
            potentialProtection = applicableProtection,
            currentGap = currentGap,
            additionalSavings = additionalSavings,
            projectedSavings = projectedSavings,
            projectedGap = projectedGap,
            reductionInGapPercent = reductionPercent
        )
    }

    fun assessReadiness(
        members: List<FamilyMember>,
        accessibleSavings: Int,
        schemes: List<HealthBenefitScheme>,
        monthlySpending: Int
    ): ReadinessAssessment {
        val familySize = members.size
        val elderlyCount = members.count { it.age >= 60 }
        val chronicCareCount = members.count { it.recurringMedicines.isNotEmpty() || it.conditions.isNotEmpty() }
        val totalMonthlyRecurring = members.sumOf { it.estimatedMonthlyExpense }

        // 1. Emergency Liquidity Score (30% weight)
        // Ratio of current savings vs recommended baseline (₹50,000 for average family)
        val liquidityRatio = accessibleSavings.toDouble() / DEFAULT_TARGET_BUFFER
        val liquidityScore = min(100, max(15, (liquidityRatio * 75).roundToInt() + 15))

        // 2. Healthcare Protection Score (25% weight)
        // Active schemes + verified private insurance
        val activeSchemes = schemes.count { 
            it.status == com.example.model.BenefitStatus.AVAILABLE || 
            it.status == com.example.model.BenefitStatus.CONNECTED 
        }
        val potentialSchemes = schemes.count { 
            it.status == com.example.model.BenefitStatus.POTENTIALLY_APPLICABLE 
        }
        val protectionScore = min(100, (activeSchemes * 40) + (potentialSchemes * 20) + 20)

        // 3. Recurring Burden Score (15% weight) - higher is healthier (lower burden)
        // If monthly spending is high relative to buffer, burden score drops
        val burdenScore = when {
            totalMonthlyRecurring == 0 -> 90
            totalMonthlyRecurring < 2500 -> 75
            totalMonthlyRecurring < 5000 -> 55
            totalMonthlyRecurring < 8000 -> 40
            else -> 25
        }

        // 4. Coverage Understanding Score (15% weight)
        val verifiedRatio = if (schemes.isNotEmpty()) {
            schemes.count { it.status == com.example.model.BenefitStatus.AVAILABLE }.toDouble() / schemes.size
        } else 0.4
        val coverageUnderstandingScore = min(95, max(30, (verifiedRatio * 60 + 35).roundToInt()))

        // 5. Health Savings Discipline Score (15% weight)
        val savingsScore = min(100, max(20, (accessibleSavings / 300).coerceAtMost(85) + 10))

        // Composite Weighted Score
        val compositeScore = (
            (liquidityScore * 0.30) +
            (protectionScore * 0.25) +
            (burdenScore * 0.15) +
            (coverageUnderstandingScore * 0.15) +
            (savingsScore * 0.15)
        ).roundToInt()

        val label = when {
            compositeScore >= 80 -> "Strongly Prepared"
            compositeScore >= 60 -> "Moderately Prepared"
            compositeScore >= 45 -> "At-Risk Preparedness"
            else -> "Financially Vulnerable"
        }

        // Determine Dominant Vulnerability
        // Transparent rules: find lowest scoring or highest risk dimension
        val dominantVulnerability = when {
            liquidityScore <= 60 && accessibleSavings < 25000 -> DominantVulnerabilityType.LIQUIDITY
            coverageUnderstandingScore <= 55 || potentialSchemes > activeSchemes -> DominantVulnerabilityType.COVERAGE_UNCERTAINTY
            burdenScore <= 45 || totalMonthlyRecurring > 4000 -> DominantVulnerabilityType.RECURRING_BURDEN
            else -> DominantVulnerabilityType.LIQUIDITY
        }

        val explainabilityFactors = listOf(
            ExplainabilityFactor(
                factor = "Dedicated Health Savings",
                impact = "Positive",
                scoreContribution = "+$liquidityScore%",
                detail = "₹${accessibleSavings} immediately accessible buffer covers routine OPD & initial hospital deposits."
            ),
            ExplainabilityFactor(
                factor = "Protection Policies",
                impact = "Positive",
                scoreContribution = "+$protectionScore%",
                detail = "${activeSchemes} active/verified schemes plus ${potentialSchemes} potentially applicable safety nets."
            ),
            ExplainabilityFactor(
                factor = "Recurring Monthly Burden",
                impact = if (burdenScore < 60) "Negative" else "Neutral",
                scoreContribution = "$burdenScore%",
                detail = "₹${totalMonthlyRecurring}/mo for ongoing medicines ($chronicCareCount member${if (chronicCareCount > 1) "s" else ""} with active therapy)."
            ),
            ExplainabilityFactor(
                factor = "Family Age & Vulnerability Profile",
                impact = if (elderlyCount > 0) "Elevated Risk" else "Stable",
                scoreContribution = "$familySize members",
                detail = "$elderlyCount senior dependent(s) increase expected healthcare frequency."
            )
        )

        return ReadinessAssessment(
            overallScore = compositeScore,
            readinessLabel = label,
            emergencyLiquidityScore = liquidityScore,
            healthcareProtectionScore = protectionScore,
            recurringBurdenScore = burdenScore,
            coverageUnderstandingScore = coverageUnderstandingScore,
            healthSavingsScore = savingsScore,
            dominantVulnerability = dominantVulnerability,
            explainabilityFactors = explainabilityFactors
        )
    }
}

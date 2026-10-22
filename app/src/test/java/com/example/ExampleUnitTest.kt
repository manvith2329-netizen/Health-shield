package com.example

import com.example.calculation.ResilienceEngine
import com.example.data.HealthShieldRepository
import com.example.model.DominantVulnerabilityType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testShockCalculation() {
        val result = ResilienceEngine.calculateShock(
            shockAmount = 50000,
            accessibleSavings = 18500,
            protectionAvailable = 20000
        )
        // With 50k shock, 70% protection ceiling = 20,000.
        // Total buffer = 18,500 + 20,000 = 38,500.
        // Remaining exposure = 50,000 - 38,500 = 11,500.
        assertEquals(50000, result.shockAmount)
        assertEquals(18500, result.accessibleSavings)
        assertEquals(20000, result.potentialProtection)
        assertEquals(11500, result.remainingExposure)
        assertTrue(result.absorptionRatePercent > 70)
    }

    @Test
    fun testBeforeAfterProjection() {
        val projection = ResilienceEngine.calculateBeforeAfter(
            currentSavings = 18500,
            dailyRate = 100,
            days = 60,
            shockAmount = 50000,
            protectionAvailable = 20000
        )
        // Additional savings: 100 * 60 = 6,000
        // Projected savings: 18,500 + 6,000 = 24,500
        // Current gap: 11,500
        // Projected gap: 50,000 - (24,500 + 20,000) = 5,500
        assertEquals(6000, projection.additionalSavings)
        assertEquals(24500, projection.projectedSavings)
        assertEquals(11500, projection.currentGap)
        assertEquals(5500, projection.projectedGap)
        assertTrue(projection.reductionInGapPercent > 50)
    }

    @Test
    fun testReadinessAssessmentWithInitialDemo() {
        val repo = HealthShieldRepository()
        val assessment = ResilienceEngine.assessReadiness(
            members = repo.members.value,
            accessibleSavings = 18500,
            schemes = repo.schemes.value,
            monthlySpending = 4700
        )
        assertTrue("Readiness overall score should be between 60 and 80", assessment.overallScore in 60..80)
        assertNotNull(assessment.dominantVulnerability)
        assertEquals(4, assessment.explainabilityFactors.size)
    }
}

package com.example.manjunathtask.test

import com.example.manjunathtask.data.model.Holding
import com.example.manjunathtask.domain.PortfolioCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class PortfolioCalculatorTest {

    @Test
    fun calculateSummary_isCorrect() {
        val holdings = listOf(
            Holding("AAA",  quantity = 10, avgPrice = 100.0, ltp = 110.0, close = 108.0),
            Holding("BBB", quantity = 5, avgPrice = 200.0, ltp = 195.0, close = 198.0)
        )

        val summary = PortfolioCalculator.calculate(holdings)
        // CurrentValue = 10*110 + 5*195 = 1100 + 975 = 2075
        assertEquals(2075.0, summary.currentValue, 0.001)
        // TotalInvestment = 10*100 + 5*200 = 1000 + 1000 = 2000
        assertEquals(2000.0, summary.totalInvestment, 0.001)
        // totalPNL = 75
        assertEquals(75.0, summary.totalPNL, 0.001)
        // todaysPNL = sum((close - ltp) * qty) = (108-110)*10 + (198-195)*5 = (-2)*10 + 3*5 = -20 + 15 = -5
        assertEquals(-5.0, summary.todayPNL, 0.001)
    }
}

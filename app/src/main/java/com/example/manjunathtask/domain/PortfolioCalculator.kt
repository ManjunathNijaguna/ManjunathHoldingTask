package com.example.manjunathtask.domain

import com.example.manjunathtask.domain.model.Holding

data class PortfolioSummary(
    val currentValue: Double,
    val totalInvestment: Double,
    val totalPNL: Double,
    val todayPNL: Double,
    val totalPNLPercentage: Double
)

object PortfolioCalculator {
    private fun safeD(x: Double?): Double =
        if (x != null && x.isFinite()) x else 0.0

    private fun safeQty(q: Int?): Int =
        (q ?: 0).coerceAtLeast(0)

    fun calculate(holdings: List<Holding>): PortfolioSummary {
        // 1) Current value = Σ (ltp * qty)
        val currentValue = holdings.sumOf { safeD(it.ltp) * safeQty(it.quantity) }

        // 2) Total investment = Σ (avgPrice * qty)
        val totalInvestment = holdings.sumOf { safeD(it.avgPrice) * safeQty(it.quantity) }

        // 3) Total P&L = Current - Investment
        val totalPNL = currentValue - totalInvestment

        // 4) Today's P&L = Σ ((close - ltp) * qty)
        val todayPNL = holdings.sumOf { (safeD(it.close) - safeD(it.ltp)) * safeQty(it.quantity) }

        // 5) P&L %
        val pnlPercentage = if (totalInvestment > 0.0) {
            (totalPNL / totalInvestment) * 100.0
        } else 0.0

        return PortfolioSummary(
            currentValue = currentValue,
            totalInvestment = totalInvestment,
            totalPNL = totalPNL,
            todayPNL = todayPNL,
            totalPNLPercentage = pnlPercentage
        )
    }

}
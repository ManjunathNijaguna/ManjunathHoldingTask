package com.example.manjunathtask.data.model

data class Holding(
    val symbol: String = "",
    val ltp: Double = 0.0,
    val avgPrice: Double = 0.0,
    val close: Double = 0.0,
    val quantity: Int = 0,
)
 {
    val pnl: Double
        get() = (ltp - avgPrice) * quantity
}
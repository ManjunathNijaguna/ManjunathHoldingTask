package com.example.manjunathtask.data.api

import com.google.gson.annotations.SerializedName

data class HoldingsResponse(
    @SerializedName("data") val data: HoldingsData
)

data class HoldingsData(
    @SerializedName("userHolding") val userHolding: List<HoldingDTO>
)

data class HoldingDTO(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("ltp") val ltp: Double,
    @SerializedName("avgPrice") val avgPrice: Double,
    @SerializedName("close") val close: Double
)


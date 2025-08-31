package com.example.manjunathtask.data.api

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET(".")
    suspend fun getHoldings(): Response<HoldingsResponse>
}

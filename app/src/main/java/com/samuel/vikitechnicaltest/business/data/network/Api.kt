package com.samuel.vikitechnicaltest.business.data.network

import com.samuel.vikitechnicaltest.BuildConfig
import com.samuel.vikitechnicaltest.business.domain.models.ExchangeRate
import retrofit2.Response
import retrofit2.http.GET

interface Api {

    @GET("${BuildConfig.API_KEY}/latest/SGD")
    suspend fun getExchangeRates() : Response<ExchangeRate>
}
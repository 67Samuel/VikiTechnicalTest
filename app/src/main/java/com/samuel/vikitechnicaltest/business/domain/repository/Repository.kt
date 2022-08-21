package com.samuel.vikitechnicaltest.business.domain.repository

import com.samuel.vikitechnicaltest.business.domain.models.ExchangeRate
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET

interface Repository {

    suspend fun getExchangeRates(): Response<ExchangeRate>
}
package com.samuel.vikitechnicaltest.business.data.repository

import android.util.Log
import com.samuel.vikitechnicaltest.business.data.network.Api
import com.samuel.vikitechnicaltest.business.domain.models.ExchangeRate
import com.samuel.vikitechnicaltest.business.domain.repository.Repository
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    val api: Api
) : Repository
{
    private val TAG: String = "RepositoryImplDebug"

    override suspend fun getExchangeRates(): Response<ExchangeRate> {
        return api.getExchangeRates()
    }


}
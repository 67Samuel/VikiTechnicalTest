package com.samuel.vikitechnicaltest.presentation.home

import com.samuel.vikitechnicaltest.business.domain.models.Country

data class HomeState(
    val toCountry: Country? = null,
    val fromCountry: Country? = null,
) {
    override fun toString(): String {
        return "MainState(toCurrency='$toCountry', fromCurrency='$fromCountry')"
    }
}


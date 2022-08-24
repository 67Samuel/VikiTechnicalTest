package com.samuel.vikitechnicaltest.presentation.selectcountry

import com.samuel.vikitechnicaltest.business.domain.models.Country

sealed class SelectCountryEvents {

    enum class CountryDirection {
        TO, FROM
    }

    object InitCountryList : SelectCountryEvents()

    data class SelectCountry(
        val direction: CountryDirection,
        val country: Country
    ) : SelectCountryEvents()
}

package com.samuel.vikitechnicaltest.presentation.selectcountry

import com.samuel.vikitechnicaltest.business.domain.models.Country
import com.samuel.vikitechnicaltest.presentation.home.HomeEvents

sealed class SelectCountryEvents {

    enum class CountryDirection {
        TO, FROM;

        override fun toString(): String {
            return this.name.lowercase().replaceFirstChar { c -> c.uppercaseChar() }
        }
    }

    object InitCountryList : SelectCountryEvents()

    data class NetworkConnectionChanged(val connected: Boolean): SelectCountryEvents()

    data class SelectCountry(
        val direction: CountryDirection,
        val country: Country
    ) : SelectCountryEvents()
}

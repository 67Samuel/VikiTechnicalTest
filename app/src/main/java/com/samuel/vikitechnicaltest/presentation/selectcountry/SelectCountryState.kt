package com.samuel.vikitechnicaltest.presentation.selectcountry

import com.samuel.vikitechnicaltest.business.domain.models.Country

data class SelectCountryState(
    val countryList: List<Country> = emptyList(),
    val toastMessage: String? = null
) {
    override fun toString(): String {
        return "SelectCountryState(countryList=${countryList.isNotEmpty()}, toastMessage=$toastMessage)"
    }
}

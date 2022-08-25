package com.samuel.vikitechnicaltest.presentation.selectcountry

import android.text.BoringLayout
import com.samuel.vikitechnicaltest.business.domain.models.Country

data class SelectCountryState(
    val countryList: List<Country> = emptyList(),
    val toastMessage: String? = null,
    val query: String = ""
) {
    override fun toString(): String {
        return "SelectCountryState(countryList=$countryList, toastMessage=$toastMessage, query='$query')"
    }
}

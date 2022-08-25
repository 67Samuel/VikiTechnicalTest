package com.samuel.vikitechnicaltest.business.domain.models

data class Country(
    val name: String,
    val imageId: Int,
    val currencyCode: String,
    var rate: Float
) {
    override fun toString(): String {
        return "Country('$currencyCode' -> $rate)"
    }
}

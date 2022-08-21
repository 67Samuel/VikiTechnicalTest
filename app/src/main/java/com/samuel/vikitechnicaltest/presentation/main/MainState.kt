package com.samuel.vikitechnicaltest.presentation.main

data class MainState(
    val toCurrency: String = "SGD",
    val fromCurrency: String = "SGD",
    val toAmount: Double = .0,
    val fromAmount: Double = .0
) {
    override fun toString(): String {
        return "MainState(toCurrency='$toCurrency', fromCurrency='$fromCurrency', toAmount=$toAmount, fromAmount=$fromAmount)"
    }
}


package com.samuel.vikitechnicaltest.presentation

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.vikitechnicaltest.business.data.datastore.AppDataStore
import com.samuel.vikitechnicaltest.business.domain.models.ConversionRates
import com.samuel.vikitechnicaltest.business.domain.models.ConversionRates.Companion.getCountryCodes
import com.samuel.vikitechnicaltest.business.domain.models.Country
import com.samuel.vikitechnicaltest.business.domain.repository.Repository
import com.samuel.vikitechnicaltest.presentation.home.HomeEvents
import com.samuel.vikitechnicaltest.presentation.home.HomeEvents.GetExchangeRates
import com.samuel.vikitechnicaltest.presentation.home.HomeState
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryEvents
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryEvents.CountryDirection
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryState
import com.samuel.vikitechnicaltest.presentation.util.toCountry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.reflect.full.declaredMemberProperties

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val datastoreManager: AppDataStore
) : ViewModel() {

    private val TAG: String = "MainViewModelDebug"

    private val _homeState: MutableLiveData<HomeState> = MutableLiveData(HomeState())
    val homeState: LiveData<HomeState> = _homeState

    private val _selectCountryState: MutableLiveData<SelectCountryState> = MutableLiveData(SelectCountryState())
    val selectCountryState: LiveData<SelectCountryState> = _selectCountryState

    init {
        onTriggerMainEvent(GetExchangeRates)
    }

    fun onTriggerMainEvent(event: HomeEvents) {
        when(event) {
            is GetExchangeRates -> {
                getExchangeRates()
            }
        }
    }

    fun onTriggerMainEvent(event: SelectCountryEvents) {
        when(event) {
            is SelectCountryEvents.InitCountryList -> {
                initCountryList()
            }
            is SelectCountryEvents.SelectCountry -> {
                selectCountry(event.direction, event.country)
            }
        }
    }

    private fun selectCountry(direction: CountryDirection, country: Country) {
        Log.d(TAG, "selectCountry: called")
        homeState.value?.let {
            viewModelScope.launch {
                _homeState.value = homeState.value!!.let {
                    if (direction == CountryDirection.TO) it.copy(toCountry = country) else it.copy(fromCountry = country)
                }
            }
        }
    }

    private fun initCountryList() {
        Log.d(TAG, "initCountryList: called")
        selectCountryState.value?.let {
            viewModelScope.launch {
                withTimeoutOrNull(5_000) {
                    flow<Unit> {
                        // create country list from data in datastore
                        val countryList = ArrayList<Country>()
                        for (countryCode in getCountryCodes()) {
                            val country = countryCode.toCountry()
                            countryList.add(country)
                        }
                        _selectCountryState.value = selectCountryState.value!!.copy(countryList = countryList)
                    }.collect {}
                } ?: displayToast("Data could not be retrieved, please check your network connection.")
            }
        }
    }

    private fun displayToast(message: String) {
        _selectCountryState.value = selectCountryState.value!!.copy(toastMessage = message)
    }


    private fun getExchangeRates() {
        homeState.value?.let {
            viewModelScope.launch {
                val response = try {
                    repository.getExchangeRates()
                } catch (e: IOException) {
                    Log.e(TAG, "getExchangeRates: ", e)
                    return@launch
                } catch (e: HttpException) {
                    Log.e(TAG, "getExchangeRates: ", e)
                    return@launch
                }
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let { body ->
                        Log.d(TAG, "getExchangeRates: number of countries (getCountryCodeRateList): ${body.conversion_rates.getCountryCodeRateList().size}")
                        Log.d(TAG, "getExchangeRates: number of countries (getRatesList): ${body.conversion_rates.getRatesList().size}")
                        Log.d(TAG, "getExchangeRates: number of countries (getCountryCodes): ${getCountryCodes().size}")
                        for ((countryCode, rate) in body.conversion_rates.getCountryCodeRateList()) {
                            // save to datastore
                            datastoreManager.setValue(countryCode, rate)
                        }
                    }
                } else {
                    Log.d(TAG, "getExchangeRates: response is not successful")
                }
            }
        }
    }
}
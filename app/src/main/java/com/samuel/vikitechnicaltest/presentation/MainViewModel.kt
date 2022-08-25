package com.samuel.vikitechnicaltest.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.vikitechnicaltest.business.data.datastore.AppDataStore
import com.samuel.vikitechnicaltest.business.domain.models.ConversionRates.Companion.getCountryCodes
import com.samuel.vikitechnicaltest.business.domain.models.Country
import com.samuel.vikitechnicaltest.business.domain.repository.Repository
import com.samuel.vikitechnicaltest.presentation.home.HomeEvents
import com.samuel.vikitechnicaltest.presentation.home.HomeEvents.RetrieveExchangeRates
import com.samuel.vikitechnicaltest.presentation.home.HomeState
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryEvents
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryEvents.CountryDirection
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryState
import com.samuel.vikitechnicaltest.presentation.util.toCountry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.streams.toList

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
        onTriggerHomeEvent(RetrieveExchangeRates)
    }

    fun onTriggerHomeEvent(event: HomeEvents) {
        when(event) {
            is RetrieveExchangeRates -> {
                retrieveExchangeRates()
            }
        }
    }

    fun onTriggerSelectCountryEvent(event: SelectCountryEvents) {
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
                            val country = countryCode.toCountry(1f)
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


    /**
     * Retrieves exchange rate data from network.
     * Data is then stored in DataStore for offline use and in a list for current use.
     * If no data is returned, app should search for data in DataStore.
     */
    private fun retrieveExchangeRates() {
        selectCountryState.value?.let {
            viewModelScope.launch {
                val response = try {
                    repository.getExchangeRates()
                } catch (e: IOException) {
                    Log.e(TAG, "retrieveExchangeRates: ", e)
                    return@launch
                } catch (e: HttpException) {
                    Log.e(TAG, "retrieveExchangeRates: ", e)
                    return@launch
                }
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let { body ->
                        val countriesList = body.conversion_rates.getCountryCodeRateList().stream()
                                // save to datastore for offline use
                            .peek { v -> datastoreManager.setValue(v.first, v.second) }
                                // save to list for current use (this violates the single source of truth principle, so maybe I shouldn't do it, but let's see the performance difference first)
                            .map { v -> v.first.toCountry(v.second) }
                                // for debugging
//                            .limit(10)
                            .toList()
                        Log.d(TAG, "retrieveExchangeRates: countriesList size: ${countriesList.size}")
                        // selectCountryState.value should not be null because we checked it earlier and the state should persist beyond Android lifecycles
                        _selectCountryState.value = selectCountryState.value!!.copy(countryList = countriesList)
                    }
                } else {
                    Log.d(TAG, "retrieveExchangeRates: response is not successful")
                }
            }
        }
    }
}
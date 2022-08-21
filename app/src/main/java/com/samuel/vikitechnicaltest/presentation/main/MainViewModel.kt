package com.samuel.vikitechnicaltest.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.vikitechnicaltest.business.data.datastore.AppDataStore
import com.samuel.vikitechnicaltest.business.domain.repository.Repository
import com.samuel.vikitechnicaltest.presentation.main.MainEvents.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val datastoreManager: AppDataStore
) : ViewModel() {

    private val TAG: String = "MainViewModelDebug"

    private val _state: MutableLiveData<MainState> = MutableLiveData(MainState())
    val state: LiveData<MainState> = _state

    init {
        onTriggerMainEvent(GetExchangeRates)
    }

    fun onTriggerMainEvent(event: MainEvents) {
        when(event) {
            is GetExchangeRates -> {
                getExchangeRates()
            }
        }
    }

    private fun getExchangeRates() {
        state.value?.let {
            viewModelScope.launch {
                val response = try {
                    repository.getExchangeRates()
                } catch (e: IOException) {
                    Log.d(TAG, "getExchangeRates: $e")
                    return@launch
                } catch (e: HttpException) {
                    Log.d(TAG, "getExchangeRates: $e")
                    return@launch
                }
//                response.enqueue(object: Callback<ResponseBody> {
//                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        Log.d(TAG, "onFailure: "+t.message)
//                    }
//
//                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                        //your raw string response
//                        val stringResponse = response.body()?.string()
//                    }
//                })

                if (response.isSuccessful) {
                    response.body().let { body ->
                        Log.d(TAG, "ConversionRates Obj: ${body?.conversion_rates}")
                        body?.conversion_rates.apply {

                        }
                    }
                } else {
                    Log.d(TAG, "getExchangeRates: response is not successful")
                }

            }
        }
    }
}
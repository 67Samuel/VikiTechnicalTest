package com.samuel.vikitechnicaltest.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.samuel.vikitechnicaltest.R
import com.samuel.vikitechnicaltest.business.data.datastore.AppDataStore
import com.samuel.vikitechnicaltest.databinding.FragmentHomeBinding
import com.samuel.vikitechnicaltest.presentation.MainViewModel
import com.samuel.vikitechnicaltest.presentation.selectcountry.SelectCountryEvents
import com.samuel.vikitechnicaltest.presentation.util.onSubmit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment() : Fragment(com.samuel.vikitechnicaltest.R.layout.fragment_home){
    private val TAG: String = "HomeFragmentDebug"
    
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var datastoreManager: AppDataStore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: called")
        binding = FragmentHomeBinding.bind(view)
        setListeners()
        subscribeObservers()

        // bring up the soft keyboard on start
        binding.inputCard.editText.requestFocus()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun setListeners() {
        binding.apply{
            fromCountry.root.setOnClickListener {
                val bundle = bundleOf("direction" to SelectCountryEvents.CountryDirection.FROM.name)
                findNavController().navigate(com.samuel.vikitechnicaltest.R.id.action_homeFragment_to_selectCountryFragment, bundle)
            }
            toCountry.root.setOnClickListener {
                val bundle = bundleOf("direction" to SelectCountryEvents.CountryDirection.TO.name)
                findNavController().navigate(com.samuel.vikitechnicaltest.R.id.action_homeFragment_to_selectCountryFragment, bundle)
            }
            inputCard.editText.onSubmit {
                CoroutineScope(Main).launch {
                    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                    calculateAndShowConversion()
                }
            }
            resetBtn.setOnClickListener {
                resultCard.fromAmount.text = ""
                resultCard.toAmount.text = ""
                inputCard.root.visibility = VISIBLE
                resultInfo.visibility = GONE
                inputCard.editText.requestFocus()
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
        }
    }

    private suspend fun calculateAndShowConversion() {
        Log.d(TAG, "calculateAndShowConversion: called")
        val number = binding.inputCard.editText.text.toString().toFloatOrNull()
        val toCountryCode = binding.toCountry.currencyCode.text.toString()
        val fromCountryCode = binding.fromCountry.currencyCode.text.toString()

        withContext(IO) {
            number?.let {
                datastoreManager.apply {
                    readValue(fromCountryCode)?.let { fromExchangeRate ->
                        readValue(toCountryCode)?.let { toExchangeRate ->
                            val fromAmount = (number * 100f).roundToInt() / 100f
                            val toAmount = ((number / fromExchangeRate) * toExchangeRate * 100f).roundToInt() / 100f
                            withContext(Main) {
                                binding.inputCard.root.visibility = GONE
                                binding.resultInfo.visibility = VISIBLE
                                val fromAmountString = "$fromAmount $fromCountryCode"
                                binding.resultCard.fromAmount.text = fromAmountString
                                val toAmountString = "$toAmount $toCountryCode"
                                binding.resultCard.toAmount.text = toAmountString
                            }
                        } ?: run { Log.d(TAG, "calculateAndShowConversion: toExchangeRate is null")}
                    } ?: run { Log.d(TAG, "calculateAndShowConversion: fromExchangeRate is null") }
                }
            }
        }
    }

    private fun subscribeObservers() {
        viewModel.homeState.observe(viewLifecycleOwner) { state ->
            Log.d(TAG, "subscribeObservers: $state")

            state.fromCountry?.let {
                Log.d(TAG, "subscribeObservers: detected from country")
                binding.fromCountry.currencyCode.text = it.currencyCode
                binding.fromCountry.image.setImageResource(it.imageId)
            }

            state.toCountry?.let {
                Log.d(TAG, "subscribeObservers: detected to country")
                binding.toCountry.currencyCode.text = it.currencyCode
                binding.toCountry.image.setImageResource(it.imageId)
            }
        }
    }
}
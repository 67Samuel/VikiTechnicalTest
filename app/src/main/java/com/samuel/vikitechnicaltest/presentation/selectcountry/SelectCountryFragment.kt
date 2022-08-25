package com.samuel.vikitechnicaltest.presentation.selectcountry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.samuel.vikitechnicaltest.R
import com.samuel.vikitechnicaltest.business.data.datastore.AppDataStore
import com.samuel.vikitechnicaltest.business.domain.models.ConversionRates.Companion.getCountryCodes
import com.samuel.vikitechnicaltest.business.domain.models.Country
import com.samuel.vikitechnicaltest.databinding.FragmentSelectCountryBinding
import com.samuel.vikitechnicaltest.presentation.MainViewModel
import com.samuel.vikitechnicaltest.presentation.home.HomeEvents
import com.samuel.vikitechnicaltest.presentation.util.toCountry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.streams.toList


@AndroidEntryPoint
class SelectCountryFragment : Fragment(R.layout.fragment_select_country),
    SelectCountryAdapter.Interaction {
    private val TAG: String = "SelectCountryFragmentDebug"

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentSelectCountryBinding

    @Inject
    lateinit var datastoreManager: AppDataStore

    private var recyclerAdapter: SelectCountryAdapter? = null // can leak memory so need to null
    private var countries = emptyList<Country>()
    private lateinit var direction: SelectCountryEvents.CountryDirection

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        arguments?.getString("direction")?.let {
            /**
             * If direction is not able to be set, program crashes.
             * User should restart app because it means that the app or environment has a problem.
             */
            direction = SelectCountryEvents.CountryDirection.valueOf(it)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSelectCountryBinding.bind(view)
        val directionText = "Convert $direction"
        binding.direction.text = directionText
        subscribeObservers()
        initSearchView()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.countrySelectRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@SelectCountryFragment.context)
            recyclerAdapter = SelectCountryAdapter(this@SelectCountryFragment)
            adapter = recyclerAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.selectCountryState.observe(viewLifecycleOwner) { state ->
            state.toastMessage?.let {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_LONG).show()
            }

            if (state.countryList.isNotEmpty()) {
                Log.d(TAG, "subscribeObservers: state.countryList is not empty")
                recyclerAdapter?.apply {
                    countries = state.countryList
                    Log.d(TAG, "subscribeObservers: setting state.countryList to adapter")
                    submitList(countries)
                }
            }

        }
    }

    private fun initSearchView() {
        Log.d(TAG, "initSearchView: called")
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val filteredList = countries.stream()
                        .filter { c -> (query.lowercase() in c.currencyCode.lowercase() || query.lowercase() in c.name.lowercase()) }
                        .toList()
                    recyclerAdapter?.apply {
                        submitList(filteredList)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = countries.stream()
                        .filter { c -> (newText.lowercase() in c.currencyCode.lowercase() || newText.lowercase() in c.name.lowercase()) }
                        .toList()
                    recyclerAdapter?.apply {
                        submitList(filteredList)
                    }
                }
                return false
            }
        })
    }

    override fun onItemSelected(position: Int, item: Country) {
        // set selected country and navigate to home fragment
        Log.d(TAG, "onItemSelected: selected item: $item")
        viewModel.onTriggerSelectCountryEvent(SelectCountryEvents.SelectCountry(direction, item))
        findNavController().popBackStack(R.id.homeFragment, false)
    }

    override fun onDestroy() {
        // prevent data leak
        recyclerAdapter = null
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        // handle changes in network connection
        lifecycleScope.launch {
            viewModel.networkMonitor.isConnected.collect {
                when (it) {
                    true -> {
                        // try to retrieve exchange rates if we don't have them yet
                        if (countries.isEmpty()) {
                            Log.d(TAG, "onResume: trying to retrieve exchange rates again")
                            viewModel.onTriggerHomeEvent(HomeEvents.RetrieveExchangeRates)
                        }
                    }
                    false -> {
                        // retrieve list of Country from DataStore
                        val countryList = arrayListOf<Country>()
                        for (code in getCountryCodes()) {
                            val rate = datastoreManager.readValue(code)
                            rate?.let {
                                countryList.add(code.toCountry(rate))
                            }
                            Log.d(TAG, "subscribeObservers: read rate: $rate")
                        }

                        // if data is successfully retrieved, set list to the recyclerview
                        if (countryList.isNotEmpty()) {
                            countries = countryList
                            recyclerAdapter?.apply {
                                Log.d(TAG, "onQueryTextSubmit: submitting offline list: $countries")
                                submitList(countries)
                            }
                            withContext(Main) {
                                Toast.makeText(requireActivity(),
                                    "Internet connection not found\nData may be outdated",
                                    Toast.LENGTH_LONG).show()
                            }
                        } else {
                            // if there is no data in DataStore, notify the user
                            Snackbar.make(
                                binding.root,
                                "No local data found",
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction("Noted") {
                            }.show()
                        }
                    }
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
    }

}
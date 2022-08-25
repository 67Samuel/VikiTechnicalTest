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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.samuel.vikitechnicaltest.R
import com.samuel.vikitechnicaltest.business.domain.models.Country
import com.samuel.vikitechnicaltest.databinding.FragmentSelectCountryBinding
import com.samuel.vikitechnicaltest.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList
import kotlin.streams.toList

@AndroidEntryPoint
class SelectCountryFragment : Fragment(R.layout.fragment_select_country),
    SelectCountryAdapter.Interaction {
    private val TAG: String = "SelectCountryFragmentDebug"

    private lateinit var direction: SelectCountryEvents.CountryDirection

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentSelectCountryBinding

    private var recyclerAdapter: SelectCountryAdapter? = null // can leak memory so need to null
    private var countries = emptyList<Country>()

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
            Log.d(TAG, "subscribeObservers: state: $state")
            state.toastMessage?.let {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_LONG).show()
            }

            if (state.countryList.isNotEmpty()) {
                Log.d(TAG, "subscribeObservers: countrylist is not empty")
                recyclerAdapter?.apply {
                    Log.d(TAG, "subscribeObservers: setting countrylist to adapter")
                    submitList(state.countryList)
                }
                Log.d(TAG, "subscribeObservers: saving countrylist to local variable")
                countries = state.countryList
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
                        .peek { c -> Log.d(TAG, "onQueryTextSubmit: stream: $c") }
                        .filter { c -> (query.lowercase() in c.currencyCode.lowercase() || query.lowercase() in c.name.lowercase()) }
                        .peek { c -> Log.d(TAG, "onQueryTextSubmit: filter: $c") }
                        .toList()
                    recyclerAdapter?.apply {
                        Log.d(TAG, "onQueryTextSubmit: submitting list: $filteredList")
                        submitList(filteredList)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = countries.stream()
                        .peek { c -> Log.d(TAG, "onQueryTextChange: stream: $c") }
                        .filter { c -> (newText.lowercase() in c.currencyCode.lowercase() || newText.lowercase() in c.name.lowercase()) }
                        .peek { c -> Log.d(TAG, "onQueryTextChange: filter: $c") }
                        .toList()
                    recyclerAdapter?.apply {
                        Log.d(TAG, "onQueryTextChange: submitting list: $filteredList")
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
}
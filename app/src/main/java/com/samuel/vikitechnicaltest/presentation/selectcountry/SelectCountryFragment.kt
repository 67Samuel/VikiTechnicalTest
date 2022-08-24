package com.samuel.vikitechnicaltest.presentation.selectcountry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

@AndroidEntryPoint
class SelectCountryFragment : Fragment(R.layout.fragment_select_country),
    SelectCountryAdapter.Interaction {
    private val TAG: String = "SelectCountryFragmentDebug"

    private lateinit var direction: SelectCountryEvents.CountryDirection

    private val viewModel: MainViewModel by activityViewModels()

    private var recyclerAdapter: SelectCountryAdapter? = null // can leak memory so need to null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        arguments?.getString("direction")?.let {
            direction = SelectCountryEvents.CountryDirection.valueOf(it)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSelectCountryBinding.bind(view)
        subscribeObservers()
        initRecyclerView(binding)
        viewModel.onTriggerMainEvent(SelectCountryEvents.InitCountryList)
    }

    private fun initRecyclerView(binding: FragmentSelectCountryBinding) {
        binding.countrySelectRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@SelectCountryFragment.context)
            recyclerAdapter = SelectCountryAdapter(this@SelectCountryFragment)
            adapter = recyclerAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.selectCountryState.observe(viewLifecycleOwner) { state ->
            Log.d(TAG, "subscribeObservers: $state")
            state.toastMessage?.let {
                Toast.makeText(requireActivity(), it+"2", Toast.LENGTH_LONG).show()
            }

            recyclerAdapter?.apply {
                submitList(state.countryList)
            }
        }
    }

    override fun onItemSelected(position: Int, item: Country) {
        // set selected country and navigate to home fragment
        Log.d(TAG, "onItemSelected: selected item: $item")
        viewModel.onTriggerMainEvent(SelectCountryEvents.SelectCountry(direction, item))
        findNavController().popBackStack(R.id.homeFragment, false)
    }

    override fun onDestroy() {
        recyclerAdapter = null
        super.onDestroy()
    }
}
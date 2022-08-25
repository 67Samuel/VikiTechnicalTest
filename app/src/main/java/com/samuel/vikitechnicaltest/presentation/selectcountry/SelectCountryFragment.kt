package com.samuel.vikitechnicaltest.presentation.selectcountry

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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
import com.samuel.vikitechnicaltest.business.data.datastore.AppDataStore
import com.samuel.vikitechnicaltest.business.domain.models.ConversionRates.Companion.getCountryCodes
import com.samuel.vikitechnicaltest.business.domain.models.Country
import com.samuel.vikitechnicaltest.databinding.FragmentSelectCountryBinding
import com.samuel.vikitechnicaltest.presentation.MainViewModel
import com.samuel.vikitechnicaltest.presentation.util.toCountry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.streams.toList


@AndroidEntryPoint
class SelectCountryFragment : Fragment(R.layout.fragment_select_country),
    SelectCountryAdapter.Interaction {
    private val TAG: String = "SelectCountryFragmentDebug"

    private lateinit var direction: SelectCountryEvents.CountryDirection

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentSelectCountryBinding

    @Inject
    lateinit var datastoreManager: AppDataStore

//    @Inject
//    lateinit var networkUtils: NetworkUtils
    private var mConnectivityManager: ConnectivityManager? = null
    private var mNetworkCallback: NetworkCallback? = null

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
            Log.d(TAG, "subscribeObservers: state: $state")
            state.toastMessage?.let {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_LONG).show()
            }

//            if (networkUtils.isOnline()) {
//                if (state.countryList.isNotEmpty()) {
//                    Log.d(TAG, "subscribeObservers: state.countryList is not empty")
//                    recyclerAdapter?.apply {
//                        countries = state.countryList
//                        Log.d(TAG, "subscribeObservers: setting state.countryList to adapter")
//                        submitList(countries)
//                    }
//                    Log.d(TAG, "subscribeObservers: saving state.countryList to local variable")
//                }
//            } else {
//                Log.d(TAG, "subscribeObservers: we are offline")
//                CoroutineScope(IO).launch {
//                    val countryList = arrayListOf<Country>()
//                    for (code in getCountryCodes()) {
//                        val rate = datastoreManager.readValue(code)
//                        rate?.let {
//                            countryList.add(code.toCountry(it))
//                        }
//                        Log.d(TAG, "subscribeObservers: read rate: $rate")
//                    }
//                    if (countryList.isNotEmpty()) {
//                        countries = countryList
//                        recyclerAdapter?.apply {
//                            Log.d(TAG, "onQueryTextSubmit: submitting offline list: $countries")
//                            submitList(countries)
//                        }
//                        withContext(Main) {
//                            Toast.makeText(requireActivity(),
//                                "Internet connection not found.\nData may be outdated.",
//                                Toast.LENGTH_LONG).show()
//                        }
//                    } else {
//                        withContext(Main) {
//                            Toast.makeText(requireActivity(),
//                                "Internet connection not found.\nNo local data found.",
//                                Toast.LENGTH_LONG).show()
//                        }
//                    }
//                }
//            }
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

    override fun onResume() {
        super.onResume()
        requestNetwork()
//        networkUtils.registerNetworkCallback(viewModel::onTriggerSelectCountryEvent)
    }

    private fun requestNetwork() {
        unregisterNetworkCallback()
        Log.d(TAG, "requestHighBandwidthNetwork: called")

        // Requesting an unmetered network may prevent you from connecting to the cellular
        // network on the user's watch or phone; however, unless you explicitly ask for permission
        // to a access the user's cellular network, you should request an unmetered network.
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        mNetworkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModel.onTriggerSelectCountryEvent(SelectCountryEvents.NetworkConnectionChanged(true))
            }

            override fun onLost(network: Network) {
                viewModel.onTriggerSelectCountryEvent(SelectCountryEvents.NetworkConnectionChanged(false))
                CoroutineScope(Main).launch {
                    Toast.makeText(requireActivity(), "Internet connection not found.\nData may be outdated.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // requires android.permission.CHANGE_NETWORK_STATE
        mConnectivityManager?.apply {
            requestNetwork(request, mNetworkCallback as NetworkCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterNetworkCallback()
    }

    private fun unregisterNetworkCallback() {
        mNetworkCallback?.let { networkCallback ->
            Log.d(TAG, "unregisterNetworkCallback: going to unregister now")
            mConnectivityManager?.apply {
                unregisterNetworkCallback(networkCallback)
                mNetworkCallback = null
            }
        }
    }
}
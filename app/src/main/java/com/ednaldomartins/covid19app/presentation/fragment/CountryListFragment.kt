package com.ednaldomartins.covid19app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.ednaldomartins.covid19app.R
import com.ednaldomartins.covid19app.domain.viewmodel.CountryListApiViewModel
import com.ednaldomartins.covid19app.domain.viewmodel.ViewModelFactory
import com.ednaldomartins.covid19app.presentation.component.CountryListAdapter
import com.ednaldomartins.covid19app.presentation.component.CountryViewHolder


class CountryListFragment :
    Fragment(),
    SwipeRefreshLayout.OnRefreshListener,
    CountryViewHolder.OnCardViewClickListener
{
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mListProgressBar: ProgressBar
    private lateinit var mCountryRecyclerView: RecyclerView

    private lateinit var countryViewModelFactory: ViewModelFactory
    private lateinit var countryListAdapter: CountryListAdapter
    private lateinit var countryListApiViewModel: CountryListApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_country_list, container, false)

        initViews(view)

        // setup RecyclerView
        val layoutManager = LinearLayoutManager(activity)
        mCountryRecyclerView.layoutManager = layoutManager
        mCountryRecyclerView.setHasFixedSize(true)

        // Request Country List
        countryListApiViewModel.responseCountryList.observe(this.viewLifecycleOwner, Observer {
            mListProgressBar.visibility = View.GONE
            it?.let { list ->
                countryListAdapter = CountryListAdapter(this.activity, list.countries!!, this)
                mCountryRecyclerView.adapter = countryListAdapter
                mCountryRecyclerView.visibility = View.VISIBLE
            }
        })

        return view
    }

    private fun initViews(v: View) {
        mSwipeRefreshLayout = v.findViewById(R.id.list_fragment_swipe_refresh_layout)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mListProgressBar = v.findViewById(R.id.fragment_list_progress_bar)
        mCountryRecyclerView = v.findViewById(R.id.country_list_recycle_view)
    }

    private fun initViewModel() {
        // Get Instance ViewModel
        val application = requireNotNull(this.activity).application
        countryViewModelFactory = ViewModelFactory(application)
        countryListApiViewModel = ViewModelProvider(activity!!, countryViewModelFactory).get(CountryListApiViewModel::class.java)
    }

    override fun onRefresh() {
        mListProgressBar.visibility = View.VISIBLE
        countryListApiViewModel.requestCountryList()
        mSwipeRefreshLayout.isRefreshing = false
        mCountryRecyclerView.visibility = View.GONE
    }

    override fun onCountryClick(countryName: String) {
        countryListApiViewModel.requestCountryStatus(countryName, "deaths") //teste com deaths
        view!!.findNavController().navigate(R.id.action_countryListFragment_to_countryStatusFragment)
    }

    override fun onStop() {
        mCountryRecyclerView.layoutManager?.let {
            countryListApiViewModel.setRecyclerViewState(it.onSaveInstanceState())
        }
        super.onStop()
    }

    override fun onResume() {
        countryListApiViewModel.recyclerViewState?.let {
            mCountryRecyclerView.layoutManager?.onRestoreInstanceState(it)
        }
        super.onResume()
    }

}

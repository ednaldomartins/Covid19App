package com.ednaldomartins.covid19app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ednaldomartins.covid19app.R
import com.ednaldomartins.covid19app.domain.viewmodel.CountryListApiViewModel
import com.ednaldomartins.covid19app.domain.viewmodel.ViewModelFactory
import com.ednaldomartins.covid19app.presentation.component.CountryListAdapter


class CountryListFragment : Fragment() {

    private lateinit var mCountryRecyclerView: RecyclerView

    private lateinit var countryViewModelFactory: ViewModelFactory
    private lateinit var countryListAdapter: CountryListAdapter
    private lateinit var countryListApiViewModel: CountryListApiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_country_list, container, false)

        // Init ViewModel
        val application = requireNotNull(this.activity).application
        countryViewModelFactory = ViewModelFactory(application)
        countryListApiViewModel = ViewModelProvider(activity!!, countryViewModelFactory).get(CountryListApiViewModel::class.java)

        // Init Views
        initViews(view)

        // setup RecyclerView
        val layoutManager = LinearLayoutManager(activity)
        mCountryRecyclerView.layoutManager = layoutManager
        mCountryRecyclerView.setHasFixedSize(true)

        // Request Country List
        countryListApiViewModel.requestCountryList()
        countryListApiViewModel.responseCountryList.observe(this.viewLifecycleOwner, Observer {
            it?.let { list ->
                countryListAdapter = CountryListAdapter(this.activity, list.countries!!)
                mCountryRecyclerView.adapter = countryListAdapter
            }
        })

        return view
    }


    private fun initViews(v: View) {
        mCountryRecyclerView = v.findViewById(R.id.country_list_recycle_view)
    }

}

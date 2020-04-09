package com.ednaldomartins.covid19app.presentation.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.ednaldomartins.covid19app.R
import com.ednaldomartins.covid19app.domain.viewmodel.CountryListApiViewModel
import com.ednaldomartins.covid19app.domain.viewmodel.ViewModelFactory
import com.ednaldomartins.covid19app.presentation.component.CountryListAdapter
import com.ednaldomartins.covid19app.presentation.component.CountryViewHolder


class CountryListFragment :
    BaseListFragment(),
    SwipeRefreshLayout.OnRefreshListener,
    CountryViewHolder.OnCardViewClickListener,
    SearchView.OnQueryTextListener {

    // viewmodel
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
        val view = super.onCreateView(inflater, container, savedInstanceState)

        countryListApiViewModel.presentationCountryList.observe(this.viewLifecycleOwner, Observer {
            mListProgressBar.visibility = View.GONE
            it?.let { list ->
                refreshPageButton(
                    countryListApiViewModel.actualPage,
                    countryListApiViewModel.totalPages
                )
                countryListAdapter = CountryListAdapter(this.activity, list, this)
                mCountryRecyclerView.adapter = countryListAdapter
                mCountryRecyclerView.visibility = View.VISIBLE
            }
        })

        return view
    }

    private fun initViewModel() {
        // Get Instance ViewModel
        val application = requireNotNull(this.activity).application
        countryViewModelFactory = ViewModelFactory(application)
        countryListApiViewModel = ViewModelProvider(activity!!, countryViewModelFactory)
            .get(CountryListApiViewModel::class.java)
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.list_button_first_page -> {
                    countryListApiViewModel.setPresentationList(1)
                }
                R.id.list_button_before_page -> {
                    countryListApiViewModel.setPresentationList(countryListApiViewModel.actualPage - 1)
                }
                R.id.list_button_next_page -> {
                    countryListApiViewModel.setPresentationList(countryListApiViewModel.actualPage + 1)
                }
                R.id.list_button_last_page -> {
                    countryListApiViewModel.setPresentationList(countryListApiViewModel.totalPages)
                }
            }
        }
    }

    override fun onRefresh() {
        countryListApiViewModel.requestCountryList()
        super.onRefresh()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        return false
    }

    override fun onCountryClick(countryName: String) {
        countryListApiViewModel.requestCountryStatus(countryName, "deaths") //teste com deaths
        view!!.findNavController()
            .navigate(R.id.action_countryListFragment_to_countryStatusFragment)
    }

    override fun onResume() {
        //  se estado do recyclerView nao for nulo recupera-lo
        if (countryListApiViewModel.recyclerViewState != null) {
            mCountryRecyclerView.layoutManager?.onRestoreInstanceState(countryListApiViewModel.recyclerViewState)
        } else {
            //  se a lista recuperada da API nao for nula, setar a lista de apresentacao
            if (countryListApiViewModel.responseCountryList.value != null)
                countryListApiViewModel.setPresentationList()
            //  se a lista recuperada da API for nula, entao recupera-la antes na API e att apresentacao
            else
                countryListApiViewModel.requestCountryList()
        }
        super.onResume()
    }

    override fun onStop() {
        mCountryRecyclerView.layoutManager?.let {
            countryListApiViewModel.setRecyclerViewState(it.onSaveInstanceState())
        }
        super.onStop()
    }

}

package com.ednaldomartins.covid19app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ednaldomartins.covid19app.R
import com.ednaldomartins.covid19app.domain.viewmodel.CountryListApiViewModel
import com.ednaldomartins.covid19app.domain.viewmodel.ViewModelFactory
import com.ednaldomartins.covid19app.presentation.component.CountryListAdapter
import com.ednaldomartins.covid19app.presentation.component.CountryViewHolder


class CountryListFragment :
    BaseListFragment(),
    CountryViewHolder.OnCardViewClickListener
{
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
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    private fun initViewModel() {
        // Get Instance ViewModel
        val application = requireNotNull(this.activity).application
        countryViewModelFactory = ViewModelFactory(application)
        countryListApiViewModel = ViewModelProvider(activity!!, countryViewModelFactory)
            .get(CountryListApiViewModel::class.java)
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
        // esconder teclado
        mSearchView.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != "")
            countryListApiViewModel.searchCountry(newText!!)
        else
            countryListApiViewModel.resetPresentationList()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_by_name -> {
                countryListApiViewModel.sortPresentationList(1)
                true
            }
            R.id.sort_by_confirmed -> {
                countryListApiViewModel.sortPresentationList(2)
                true
            }
            R.id.sort_by_deaths -> {
                countryListApiViewModel.sortPresentationList(3)
                true
            }
            R.id.sort_by_recovered -> {
                countryListApiViewModel.sortPresentationList(4)
                true
            }
            R.id.menu_help -> {
                // todo
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCountryClick(countryName: String) {
        //  TODO
    }

}

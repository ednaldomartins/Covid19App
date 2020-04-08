package com.ednaldomartins.covid19app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
    CountryViewHolder.OnCardViewClickListener,
    View.OnClickListener
{
    //  layout
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mListProgressBar: ProgressBar
    private lateinit var mCountryRecyclerView: RecyclerView

    //  view footer
    private lateinit var mButtonFirstPage: Button
    private lateinit var mButtonBeforePage: Button
    private lateinit var mButtonNextPage: Button
    private lateinit var mButtonLastPage: Button
    private lateinit var mNumberPage: TextView

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
        val view = inflater.inflate(R.layout.fragment_country_list, container, false)

        initViews(view)

        // setup RecyclerView
        val layoutManager = LinearLayoutManager(activity)
        mCountryRecyclerView.layoutManager = layoutManager
        mCountryRecyclerView.setHasFixedSize(true)

        // Request Country List
        countryListApiViewModel.presentationCountryList.observe(this.viewLifecycleOwner, Observer {
            mListProgressBar.visibility = View.GONE
            it?.let { list ->
                refreshPageButton(countryListApiViewModel.actualPage, countryListApiViewModel.totalPages)
                countryListAdapter = CountryListAdapter(this.activity, list, this)
                mCountryRecyclerView.adapter = countryListAdapter
                mCountryRecyclerView.visibility = View.VISIBLE
            }
        })

        return view
    }

    private fun initViews(v: View) {
        //  setup layout
        mSwipeRefreshLayout = v.findViewById(R.id.list_fragment_swipe_refresh_layout)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mListProgressBar = v.findViewById(R.id.fragment_list_progress_bar)
        mCountryRecyclerView = v.findViewById(R.id.country_list_recycle_view)

        //  setup toolbar
        val toolbar: Toolbar = v.findViewById(R.id.country_list_toolbar)
        toolbar.title = ""
        val toobarTitle: TextView = v.findViewById(R.id.toolbar_title)
        toobarTitle.text = resources.getString(R.string.app_name)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        // inicializacao dos botoes de navegacao de paginas
        mButtonFirstPage = v.findViewById(R.id.list_button_first_page)
        mButtonFirstPage.setOnClickListener(this)
        mButtonBeforePage = v.findViewById(R.id.list_button_before_page)
        mButtonBeforePage.setOnClickListener(this)
        mButtonNextPage = v.findViewById(R.id.list_button_next_page)
        mButtonNextPage.setOnClickListener(this)
        mButtonLastPage = v.findViewById(R.id.list_button_last_page)
        mButtonLastPage.setOnClickListener(this)
        mNumberPage = v.findViewById(R.id.list_number_page)
    }

    private fun initViewModel() {
        // Get Instance ViewModel
        val application = requireNotNull(this.activity).application
        countryViewModelFactory = ViewModelFactory(application)
        countryListApiViewModel = ViewModelProvider(activity!!, countryViewModelFactory).get(CountryListApiViewModel::class.java)
    }

    //  configuracao dos botoes de controle de pagina
    private fun refreshPageButton(actualPage: Int, totalPages: Int) {
        mNumberPage.text = ("$actualPage / $totalPages")
        if (mButtonFirstPage.visibility != View.VISIBLE) {
            mButtonFirstPage.visibility = View.VISIBLE
            mButtonBeforePage.visibility = View.VISIBLE
        }
        if (mButtonLastPage.visibility != View.VISIBLE) {
            mButtonLastPage.visibility = View.VISIBLE
            mButtonNextPage.visibility = View.VISIBLE
        }
        if (actualPage == 1 || actualPage == 0) {
            mButtonFirstPage.visibility = View.INVISIBLE
            mButtonBeforePage.visibility = View.INVISIBLE
        }
        if (actualPage == totalPages || totalPages == 0) {
            mButtonLastPage.visibility = View.INVISIBLE
            mButtonNextPage.visibility = View.INVISIBLE
        }
    }


    override fun onClick(v: View?) {
        v?.let{
            when(it.id) {
                R.id.list_button_first_page -> {
                    countryListApiViewModel.setPresentationList(1)
                }
                R.id.list_button_before_page -> {
                    countryListApiViewModel.setPresentationList( countryListApiViewModel.actualPage - 1 )
                }
                R.id.list_button_next_page -> {
                    countryListApiViewModel.setPresentationList( countryListApiViewModel.actualPage + 1 )
                }
                R.id.list_button_last_page -> {
                    countryListApiViewModel.setPresentationList( countryListApiViewModel.totalPages )
                }
            }
        }
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

    override fun onResume() {
        //  se estado do recyclerView nao for nulo recupera-lo
        if (countryListApiViewModel.recyclerViewState != null) {
            mCountryRecyclerView.layoutManager?.onRestoreInstanceState(countryListApiViewModel.recyclerViewState)
        }
        else {
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

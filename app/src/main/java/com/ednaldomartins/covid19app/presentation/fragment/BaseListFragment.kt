package com.ednaldomartins.covid19app.presentation.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ednaldomartins.covid19app.R
import kotlin.coroutines.Continuation

open class BaseListFragment :
    Fragment(),
    SearchView.OnQueryTextListener,
    SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener
{

    //  layout
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    protected lateinit var mListProgressBar: ProgressBar
    protected lateinit var mCountryRecyclerView: RecyclerView

    //  appbar
    private lateinit var appbar: Toolbar

    //  toolbar
    private lateinit var toolbar: Toolbar
    protected lateinit var mSearchView: SearchView
    protected lateinit var mImageSortButton: MenuItem

    //  view footer
    private lateinit var mButtonFirstPage: Button
    private lateinit var mButtonBeforePage: Button
    private lateinit var mButtonNextPage: Button
    private lateinit var mButtonLastPage: Button
    private lateinit var mNumberPage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_country_list, container, false)

        initViews(view)
        setHasOptionsMenu(true)

        // setup RecyclerView// configurando RecyclerView
        val layoutManager: RecyclerView.LayoutManager =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                LinearLayoutManager(activity)
            else
                GridLayoutManager(activity, 2)

        mCountryRecyclerView.layoutManager = layoutManager
        mCountryRecyclerView.setHasFixedSize(true)

        return view
    }

    private fun initViews(v: View) {
        //  setup layout
        mSwipeRefreshLayout = v.findViewById(R.id.list_fragment_swipe_refresh_layout)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mListProgressBar = v.findViewById(R.id.fragment_list_progress_bar)
        mCountryRecyclerView = v.findViewById(R.id.country_list_recycle_view)

        //  setup appbar
        appbar = v.findViewById(R.id.country_list_appbar)
        appbar.title = ""

        //  setup toolbar
        toolbar = v.findViewById(R.id.country_list_toolbar)
        toolbar.title = ""
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

    //  criacao do menu pelo fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
        val mMenuSearchItem: MenuItem? = menu.findItem(R.id.menu_search)
        mSearchView = mMenuSearchItem?.actionView as SearchView
        mSearchView.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onRefresh() {
        mListProgressBar.visibility = View.VISIBLE
        mSwipeRefreshLayout.isRefreshing = false
        mCountryRecyclerView.visibility = View.GONE
    }

    //  configuracao dos botoes de controle de pagina
    protected fun refreshPageButton(actualPage: Int, totalPages: Int) {
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
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

}

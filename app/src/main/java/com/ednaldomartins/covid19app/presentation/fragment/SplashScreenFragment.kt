package com.ednaldomartins.covid19app.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

import com.ednaldomartins.covid19app.R
import com.ednaldomartins.covid19app.domain.viewmodel.CountryListApiViewModel
import com.ednaldomartins.covid19app.domain.viewmodel.ViewModelFactory

class SplashScreenFragment : Fragment() {

    private lateinit var countryViewModelFactory: ViewModelFactory
    private lateinit var countryListApiViewModel: CountryListApiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)

        val application = requireNotNull(this.activity).application
        countryViewModelFactory = ViewModelFactory(application)
        countryListApiViewModel = ViewModelProvider(activity!!, countryViewModelFactory).get(CountryListApiViewModel::class.java)

        //  chamar a lista da API enquanto apresenta telas de boas vindas
        countryListApiViewModel.requestCountryList()
        countryListApiViewModel.presentationCountryList.observe(this.viewLifecycleOwner, Observer {
            view.findNavController().navigate(R.id.action_splashScreenFragment_to_countryListFragment)
        })

        return view
    }

}

package com.ednaldomartins.covid19app.domain.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory (val app: Application) : ViewModelProvider.Factory {

    @Throws(IllegalArgumentException::class)
    @Suppress("cast sem checagem")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if ( modelClass.isAssignableFrom(CountryListApiViewModel::class.java) ) {
            return CountryListApiViewModel(app) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }

}
package com.ednaldomartins.covid19app.domain.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ednaldomartins.covid19app.R
import com.ednaldomartins.covid19app.database.api.CovidApi
import com.ednaldomartins.covid19app.domain.entity.Country
import com.ednaldomartins.covid19app.domain.entity.CountryJson
import com.ednaldomartins.covid19app.util.CountryCode
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import kotlinx.coroutines.*
import okio.BufferedSource
import java.util.*

class CountryListApiViewModel (var app: Application) : AndroidViewModel(app) {

    // Coroutines
    private var viewModelJob = Job()
    private val uiCoroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _responseCountry = MutableLiveData<Country>()
    val responseCountryJson: LiveData<Country> get() = _responseCountry

    private var _requestCountryList = MutableLiveData<CountryJson>()
    val responseCountryList: LiveData<CountryJson> get() = _requestCountryList

    fun requestCountryList() {
        uiCoroutineScope.launch {
            val getCallDeferred = CovidApi.retrofitService.callCountryList()
            setRequestResult(getCallDeferred)
        }
    }

    private suspend fun setRequestResult(callDeferred: Deferred<CountryJson>) {
        try {
            // pegar lista de paises da API
            val resultList = callDeferred.await()
            _requestCountryList.value = resultList

            if (resultList.countries.isNullOrEmpty()) {
                Toast.makeText(getApplication(), "A busca não encontrou países.",Toast.LENGTH_LONG).show()
            }
            //  pegar bandeiras dos países
            else {
                _requestCountryList.value?.countries.let {
                    var i = 1   //  0 = vazio na api
                    while (i < it!!.size) {
                        val a = it[i].country.toLowerCase(Locale.ENGLISH)
                        var j = 0
                        while (j < CountryCode.code.size) {
                            val b = CountryCode.code[j][1].toLowerCase(Locale.ENGLISH)
                            val c = a.contains(b)
                            if (a == b) {
                                it[i].flagImage = CountryCode.URL_ROOT_FLAG + CountryCode.code[j][0] + CountryCode.URL_TYPE_FLAG
                                j = CountryCode.code.size
                            }
                            j++
                        }
                        i++
                    }
                }
            }

        }
        catch(t: JsonDataException) {
            Toast.makeText(getApplication(), "ERRO: problema com os dados dos países recuperados.",Toast.LENGTH_LONG).show()
            _requestCountryList.value = ( CountryJson(  ) )
        }
    }

}
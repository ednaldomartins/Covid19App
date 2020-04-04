package com.ednaldomartins.covid19app.domain.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ednaldomartins.covid19app.database.api.CovidApi
import com.ednaldomartins.covid19app.domain.entity.Country
import com.ednaldomartins.covid19app.domain.entity.CountryJson
import com.ednaldomartins.covid19app.util.CountryCode
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.*

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

            if (resultList.countries.isNullOrEmpty())
                requestErro(_requestCountryList, "A busca não encontrou países.")
            //  atualizar lista e pegar bandeiras dos países
            else {
                _requestCountryList.value = resultList
                requestFlagImage( _requestCountryList.value!!.countries!! )
            }

        }
        catch(t: JsonDataException) {
            requestErro(_requestCountryList, "ERRO: Problema com os dados dos países recuperados.")
        }
        catch (t: Throwable) {
            requestErro(_requestCountryList, "ERRO: A lista de países não foi recuperada.")
        }
    }

    private fun requestFlagImage(coutryList: List<Country>?) {
        for (i in coutryList!!.indices)
            coutryList[i].flagImage = CountryCode.URL_ROOT_FLAG + coutryList[i].countryCode + CountryCode.URL_TYPE_FLAG
    }

    private fun requestErro(countryList: MutableLiveData<CountryJson>, s: String) {
        Toast.makeText(getApplication(), s,Toast.LENGTH_LONG).show()
        val countiesJson = CountryJson(  )
        countiesJson.countries = listOfNotNull( Country(countryName = s) )
        countryList.value = ( countiesJson )
    }

}
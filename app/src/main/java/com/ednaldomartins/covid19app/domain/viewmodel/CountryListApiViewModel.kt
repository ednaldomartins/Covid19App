package com.ednaldomartins.covid19app.domain.viewmodel

import android.app.Application
import android.os.Parcelable
import android.widget.Toast
import androidx.lifecycle.*
import com.ednaldomartins.covid19app.database.api.CovidApi
import com.ednaldomartins.covid19app.domain.entity.CountryJson
import com.ednaldomartins.covid19app.domain.entity.CountryListJson
import com.ednaldomartins.covid19app.domain.entity.CountryStatusJson
import com.ednaldomartins.covid19app.util.CountryInfo
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.*

class CountryListApiViewModel (var app: Application) : AndroidViewModel(app), LifecycleObserver {

    // Coroutines
    private var viewModelJob = Job()
    private val uiCoroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _responseCountry = MutableLiveData<CountryJson>()
    val responseCountryJson: LiveData<CountryJson> get() = _responseCountry

    private var _responseCountryStatus = MutableLiveData<List<CountryStatusJson>>()
    val responseCountryStatusJson: LiveData<List<CountryStatusJson>> get() = _responseCountryStatus

    private var _requestCountryList = MutableLiveData<CountryListJson>()
    val responseCountryList: LiveData<CountryListJson> get() = _requestCountryList

    //  RecyclerViewState
    private var _recyclerViewState: Parcelable? = null
    val recyclerViewState: Parcelable? get() = _recyclerViewState
    fun setRecyclerViewState(recyclerViewState: Parcelable?) {
        this._recyclerViewState = recyclerViewState
    }

//    //  LifeCycle
//    private var _lifecycle: Lifecycle? = null
//    val lifecycle: Lifecycle? get() = _lifecycle
//    fun setLifecycle(lf: Lifecycle) {
//        _lifecycle = lf
//        _lifecycle?.addObserver(this)
//    }

    /**
     *  Funcao para chamar lista de paises
     */
    fun requestCountryList() {
        uiCoroutineScope.launch {
            val getCallDeferred = CovidApi.retrofitService.callCountryListAsync()
            setRequestResult(getCallDeferred)
        }
    }

    private suspend fun setRequestResult(callDeferred: Deferred<CountryListJson>) {
        try {
            // pegar lista de paises da API
            val resultList = callDeferred.await()

            if (resultList.countries.isNullOrEmpty())
                requestErro(_requestCountryList, "A busca não encontrou países.")
            //  atualizar lista e pegar bandeiras dos países
            else {
                _requestCountryList.value = resultList
                requestFlagImage( _requestCountryList.value!!.countries!! )
                setPtBrLanguage( _requestCountryList.value!!.countries!! )
            }

        }
        catch(t: JsonDataException) {
            requestErro(_requestCountryList, "ERRO: Problema com os dados dos países recuperados.")
        }
        catch (t: Throwable) {
            requestErro(_requestCountryList, "ERRO: A lista de países não foi recuperada.")
        }
    }

    /**
     *  Funcao para chamar pais por status
     */
    fun requestCountryStatus(country: String, status: String) {
        uiCoroutineScope.launch {
            val getCallDeferred = CovidApi.retrofitService.callCountryStatusAsync(country, status)
            setRequestResultStatus(getCallDeferred)
        }
    }

    private suspend fun setRequestResultStatus(callDeferred: Deferred<List<CountryStatusJson>>) {
        try {
            // pegar lista de status do pais na API
            val resultList = callDeferred.await()

            if (resultList.isNotEmpty()) {
                // realizar transacao
            }
            else {
                // nao tem nenhum dados sobre as datas
            }

        }
        catch(t: JsonDataException) {
            //
        }
        catch (t: Throwable) {
            //
        }
    }

    private fun requestFlagImage(coutryList: List<CountryJson>) {
        for (i in coutryList.indices)
            coutryList[i].flagImage = CountryInfo.URL_ROOT_FLAG + coutryList[i].countryCode + CountryInfo.URL_TYPE_FLAG
    }

    private fun setPtBrLanguage(coutryList: List<CountryJson>) {
        for (i in coutryList.indices) {
            for (j in CountryInfo.list.indices) {
                //  list[j][3] -> codigo do pais
                //  list[j][1] -> nome do pais em PtBR
                if (coutryList[i].countryCode == CountryInfo.list[j][3]) {
                    coutryList[i].countryName = CountryInfo.list[j][1]
                    break   //  break for J
                }
            }
        }
    }

    private fun requestErro(countryList: MutableLiveData<CountryListJson>, s: String) {
        Toast.makeText(getApplication(), s,Toast.LENGTH_LONG).show()
        val countiesJson = CountryListJson(  )
        countiesJson.countries = listOfNotNull( CountryJson(countryName = s) )
        countryList.value = ( countiesJson )
    }

}
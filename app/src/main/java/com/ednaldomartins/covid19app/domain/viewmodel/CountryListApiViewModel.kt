package com.ednaldomartins.covid19app.domain.viewmodel

import android.app.Application
import android.os.Parcelable
import android.widget.Toast
import androidx.lifecycle.*
import com.ednaldomartins.covid19app.database.api.CovidApi
import com.ednaldomartins.covid19app.domain.entity.CountryJson
import com.ednaldomartins.covid19app.domain.entity.CountryListJson
import com.ednaldomartins.covid19app.domain.entity.CountryStatusJson
import com.ednaldomartins.covid19app.domain.model.Sort
import com.ednaldomartins.covid19app.util.CountryInfo
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.*
import java.util.*

class CountryListApiViewModel (var app: Application) : AndroidViewModel(app), LifecycleObserver {

    // Coroutines
    private var viewModelJob = Job()
    private val uiCoroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _responseCountry = MutableLiveData<CountryJson>()
    val responseCountryJson: LiveData<CountryJson> get() = _responseCountry

    private var _responseCountryStatus = MutableLiveData<List<CountryStatusJson>>()
    val responseCountryStatusJson: LiveData<List<CountryStatusJson>> get() = _responseCountryStatus

    private var _mementoCountryList = MutableLiveData<List<CountryJson>>()
    private var _requestCountryList = MutableLiveData<CountryListJson>()
    val responseCountryList: LiveData<CountryListJson> get() = _requestCountryList

    private var _presentationCountryList = MutableLiveData<List<CountryJson>>()
    val presentationCountryList: LiveData<List<CountryJson>> get() = _presentationCountryList

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

    companion object {
        private const val PRESENTATION_LIST_SIZE: Int = 20
    }

    private var _actualPage: Int = 1
    val actualPage: Int get() = _actualPage
    private var _totalPages: Int = 1
    val totalPages: Int get() = _totalPages

    /**
     *  Funcoes para chamar lista de paises apresentar
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
                requestError(_requestCountryList, "A busca não encontrou países.")
            //  atualizar lista e pegar bandeira dos países
            else {
                _requestCountryList.value = resultList
                requestFlagImage( _requestCountryList.value!!.countries!! )
                setPtBrLanguage( _requestCountryList.value!!.countries!! )
                resetPresentationList() // esse metodo configura a lista de apresentacao para o estado inicial
            }

        }
        catch(t: JsonDataException) {
            requestError(_requestCountryList, "ERRO: Problema com os dados dos países recuperados.")
        }
        catch (t: Throwable) {
            requestError(_requestCountryList, "ERRO: A lista de países não foi recuperada.")
        }
    }

    //  buscar paises na lista recebida e aplica-los na lista de apresentacao
    fun searchCountry (query: String) {
        uiCoroutineScope.launch {
            _requestCountryList.value?.countries.let {
                val newList: MutableList<CountryJson>? = mutableListOf()
                for (i in it!!.indices) {
                    if (it[i].countryName.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                        newList!!.add( it[i] )
                }
                _mementoCountryList.value = newList
                _totalPages = _mementoCountryList.value!!.size / PRESENTATION_LIST_SIZE +1
                setPresentationList( )
            }
        }
    }

    fun setPresentationList (page: Int = _actualPage) {
        _actualPage = validatePage(page)
        uiCoroutineScope.launch {
            _mementoCountryList.value?.let {
                val sizeSubList: Int =
                    if (_actualPage != _totalPages) PRESENTATION_LIST_SIZE *_actualPage
                    else it.size

                _presentationCountryList.postValue( it.subList( (_actualPage-1)* PRESENTATION_LIST_SIZE, sizeSubList) )
            }
        }
    }

    private var _optionSort: Int = 1

    private fun validatePage(page: Int) =  when {
        (page < 1) -> 1
        (page > _totalPages) -> _totalPages
        else -> page
    }

    fun resetPresentationList() {
        _mementoCountryList.value = _requestCountryList.value?.countries
        _totalPages = _requestCountryList.value!!.countries!!.size / PRESENTATION_LIST_SIZE +1
        sortPresentationList()
    }

    fun sortPresentationList(option: Int = _optionSort) {
        _mementoCountryList.value?.let {
            _optionSort = option
            val sort = Sort()
            when (option) {
                1 -> _mementoCountryList.postValue( sort.sortByName(it) )
                2 -> _mementoCountryList.postValue( sort.sortByConfirmedCases(it) )
                3 -> _mementoCountryList.postValue( sort.sortByDeathCases(it) )
                4 -> _mementoCountryList.postValue( sort.sortByRecoveredCases(it) )
            }
            setPresentationList()
        }
    }

    /**
     *  Funcao para chamar dados dos status por pais
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

    /**
     * Funcoes para tratar e adaptar dados da apresentacao
     */
    private fun requestFlagImage(countryList: List<CountryJson>) {
        for (i in countryList.indices)
            countryList[i].flagImage = CountryInfo.URL_ROOT_FLAG + countryList[i].countryCode + CountryInfo.URL_TYPE_FLAG
    }

    private fun setPtBrLanguage(countryList: List<CountryJson>) {
        for (i in countryList.indices) {
            for (j in CountryInfo.list.indices) {
                //  list[j][3] -> codigo do pais
                //  list[j][1] -> nome do pais em PtBR
                if (countryList[i].countryCode == CountryInfo.list[j][3]) {
                    countryList[i].countryName = CountryInfo.list[j][1]
                    break   //  break for J
                }
            }
        }
    }

    /**
     * funcoes para retornar mensagem de erro
     */
    private fun requestError(countryList: MutableLiveData<CountryListJson>, s: String) {
        Toast.makeText(getApplication(), s,Toast.LENGTH_LONG).show()
        val countiesJson = CountryListJson(  )
        countiesJson.countries = listOfNotNull( CountryJson(countryName = s) )
        countryList.value = ( countiesJson )
        resetPresentationList()
    }

}
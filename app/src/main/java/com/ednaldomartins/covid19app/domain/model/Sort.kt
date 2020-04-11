package com.ednaldomartins.covid19app.domain.model

import com.ednaldomartins.covid19app.domain.entity.CountryJson

class Sort {

    // versão teste
    fun sortByName(list: List<CountryJson>) = list.sortedBy {
        it.countryName.replace("[\\Á]".toRegex(), "A")
    }
    fun sortByConfirmedCases(list: List<CountryJson>) = list.sortedByDescending { it.totalConfirmed }
    fun sortByDeathCases(list: List<CountryJson>) = list.sortedByDescending { it.totalDeaths }
    fun sortByRecoveredCases(list: List<CountryJson>) = list.sortedByDescending { it.totalRecovered }
    fun sortByNewConfirmedCases(list: List<CountryJson>) = list.sortedByDescending { it.newConfirmed }
    fun sortByNewDeathCases(list: List<CountryJson>) = list.sortedByDescending { it.newDeaths }
    fun sortByNewRecoveredCases(list: List<CountryJson>) = list.sortedByDescending { it.newRecovered }

}
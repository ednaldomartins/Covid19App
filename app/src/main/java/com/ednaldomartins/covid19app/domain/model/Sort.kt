package com.ednaldomartins.covid19app.domain.model

import com.ednaldomartins.covid19app.domain.entity.CountryJson
import java.text.Normalizer

class Sort {

    fun sortByName(list: List<CountryJson>) =
        list.sortedBy {
            Normalizer.normalize(it.countryName, Normalizer.Form.NFD)
                .replace("\\p{InCombiningDiacriticalMarks}+", "")
        }
    fun sortByConfirmedCases(list: List<CountryJson>) = list.sortedByDescending { it.totalConfirmed }
    fun sortByDeathCases(list: List<CountryJson>) = list.sortedByDescending { it.totalDeaths }
    fun sortByRecoveredCases(list: List<CountryJson>) = list.sortedByDescending { it.totalRecovered }
    fun sortByNewConfirmedCases(list: List<CountryJson>) = list.sortedByDescending { it.newConfirmed }
    fun sortByNewDeathCases(list: List<CountryJson>) = list.sortedByDescending { it.newDeaths }
    fun sortByNewRecoveredCases(list: List<CountryJson>) = list.sortedByDescending { it.newRecovered }

}
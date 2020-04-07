package com.ednaldomartins.covid19app.domain.entity

import com.squareup.moshi.Json

class CountryListJson {
    @Json(name = "Global")
    var global: CountryJson? = null

    @Json(name = "Countries")
    var countries: List<CountryJson>? = null
}
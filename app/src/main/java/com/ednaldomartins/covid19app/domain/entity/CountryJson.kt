package com.ednaldomartins.covid19app.domain.entity

import com.squareup.moshi.Json

class CountryJson {
    @Json(name = "Countries")
    var countries: List<Country>? = null
}
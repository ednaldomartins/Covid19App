package com.ednaldomartins.covid19app.domain.entity

import com.squareup.moshi.Json

class CountryStatusJson (
    @Json(name = "Country")
    var countryName: String = "",

    @Json(name = "Cases")
    var cases: Int = 0,

    @Json(name = "Status")
    var status: String = "",

    @Json(name = "Date")
    var date: String = ""
)
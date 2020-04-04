package com.ednaldomartins.covid19app.domain.entity

import java.io.Serializable
import com.squareup.moshi.Json

class Country (
    @Json(name = "Country")
    var countryName: String = "",

    @Json(name = "CountryCode")
    var countryCode: String = "",

    @Json(name = "Slug")
    var slug: String = "",

    @Json(name = "NewConfirmed")
    var newConfirmed: Int = 0,

    @Json(name = "TotalConfirmed")
    var totalConfirmed: Int = 0,

    @Json(name = "NewDeaths")
    var newDeaths: Int = 0,

    @Json(name = "TotalDeaths")
    var totalDeaths: Int = 0,

    @Json(name = "NewRecovered")
    var newRecovered: Int = 0,

    @Json(name = "TotalRecovered")
    var totalRecovered: Int = 0,

    var flagImage: String = ""
): Serializable
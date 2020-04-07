package com.ednaldomartins.covid19app.database.api

import com.ednaldomartins.covid19app.domain.entity.CountryListJson
import com.ednaldomartins.covid19app.domain.entity.CountryStatusJson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val URL_API: String = "https://api.covid19api.com/"

private val  moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val  retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(URL_API)
    .build()

interface CovidApiService {

    // chamar lista de paises
    @GET("summary")
    fun callCountryListAsync (): Deferred<CountryListJson>

    @GET("total/country/{country}/status/{status}")
    fun callCountryStatusAsync (
        @Path("country") country : String,
        @Path("status") status : String
    ): Deferred<List<CountryStatusJson>>

}

object CovidApi {
    val retrofitService: CovidApiService by lazy {
        retrofit.create(CovidApiService::class.java)
    }
}
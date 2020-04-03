package com.ednaldomartins.covid19app.database.api

import com.ednaldomartins.covid19app.domain.entity.CountryJson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private val URL_API: String = "https://api.covid19api.com/"

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
    fun callCountryList (): Deferred<CountryJson>

}

object CovidApi {
    val retrofitService: CovidApiService by lazy {
        retrofit.create(CovidApiService::class.java)
    }
}
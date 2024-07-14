package com.alpharishi.cityweatherpro.interfaceApi

import com.alpharishi.cityweatherpro.api.weatherApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface InterfaceAPI {

    @GET("weather")
    fun getWeatherData(
        @Query("q") city : String,
        @Query("appid") appid : String,
        @Query("units") units : String
    ):Call<weatherApi>
}
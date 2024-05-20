package com.example.compasstestapp.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface CompassApi {
    @GET("about")
    fun getAboutSection(): Call<ResponseBody>
}
package com.fei.pavement

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ThingsService  {
    @GET("Myproject/getLatitudeLongitude")
    fun searchPage(@Query("pageNum")pageNum:String,@Query("pageSize")pageSize:String):Call<SearchPage>
}

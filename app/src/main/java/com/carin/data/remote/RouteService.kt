package com.carin.data.remote

import com.carin.data.remote.dto.RouteDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RouteService {
    @GET("/routes")
    fun getRoutes(
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null,
        @Query("perPage") perPage: Int? = null
    ): Call<List<RouteDto>>
}
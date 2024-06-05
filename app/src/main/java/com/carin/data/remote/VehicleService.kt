package com.carin.data.remote

import com.carin.data.remote.dto.VehicleDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VehicleService {
    @GET("/vehicles")
    fun getVehicles(
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): Call<List<VehicleDto>>
}
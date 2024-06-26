package com.carin.data.remote

import com.carin.data.remote.dto.VehicleCreationRequest
import com.carin.data.remote.dto.VehicleDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VehicleService {
    @GET("/vehicles")
    fun getVehicles(
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null,
        @Query("perPage") perPage: Int? = null
    ): Call<List<VehicleDto>>

    @GET("/vehicles/{id}")
    fun getVehicleById(@Path("id") id: String): Call<VehicleDto>

    @POST("/vehicles")
    fun createVehicle(@Body vehicleCreationRequest: VehicleCreationRequest): Call<VehicleDto>
}
package com.carin.data.remote

import com.carin.data.remote.dto.RouteCreationRequest
import com.carin.data.remote.dto.RouteDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RouteService {
    @GET("/routes")
    fun getRoutes(
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null,
        @Query("perPage") perPage: Int? = null,
        @Query("userId") userId: Int? = null,
        @Query("vehicleId") vehicleId: String? = null,
    ): Call<List<RouteDto>>

    @GET("/routes/{id}")
    fun getRouteById(@Path("id") id: String): Call<RouteDto>

    @POST("/routes")
    fun createRoute(@Body routeCreationRequest: RouteCreationRequest): Call<RouteDto>
}
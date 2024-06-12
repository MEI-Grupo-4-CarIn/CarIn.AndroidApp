package com.carin.data.remote

import com.carin.data.remote.dto.UserDto
import com.carin.data.remote.dto.UserUpdateRequest
import com.carin.domain.enums.Role
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("/users")
    fun getUsers(
        @Query("search") search: String? = null,
        @Query("role") role: Role? = null,
        @Query("page") page: Int? = null,
        @Query("perPage") perPage: Int? = null
    ): Call<List<UserDto>>

    @GET("/users/waiting-for-approval")
    fun getWaitingForApprovalUsers(
        @Query("page") page: Int? = null,
        @Query("perPage") perPage: Int? = null
    ): Call<List<UserDto>>

    @GET("/users/{id}")
    fun getUserById(@Path("id") id: Int): Call<UserDto>

    @PATCH("/users/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Body userUpdateRequest: UserUpdateRequest
    ): Call<ResponseBody>

    @POST("/users/{id}/approval")
    fun approveUser(
        @Path("id") id: Int,
        @Query("roleId") roleId: Int? = null
    ): Call<ResponseBody>

    @DELETE("/users/{id}")
    fun deleteUser(@Path("id") id: Int): Call<ResponseBody>
}
package com.carin.data.remote

import com.carin.data.remote.dto.UserDto
import com.carin.domain.enums.Role
import retrofit2.Call
import retrofit2.http.GET
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

    @GET("/users/{id}")
    fun getUserById(@Path("id") id: Int): Call<UserDto>
}
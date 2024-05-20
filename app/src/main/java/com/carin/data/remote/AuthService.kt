package com.carin.data.remote

import com.carin.data.models.request.AuthLoginRequest
import com.carin.data.models.response.AuthLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/login")
    fun login(@Body loginRequest: AuthLoginRequest): Call<AuthLoginResponse>
}
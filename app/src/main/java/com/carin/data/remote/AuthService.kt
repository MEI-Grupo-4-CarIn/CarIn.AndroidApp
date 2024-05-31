package com.carin.data.remote

import com.carin.data.remote.dto.auth.AuthLoginRequest
import com.carin.data.remote.dto.auth.AuthRefreshTokenRequest
import com.carin.data.remote.dto.auth.AuthTokenDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/login")
    fun login(@Body loginRequest: AuthLoginRequest): Call<AuthTokenDto>

    @POST("api/auth/refreshToken")
    fun refreshToken(@Body refreshTokenRequest: AuthRefreshTokenRequest): Call<AuthTokenDto>

}
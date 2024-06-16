package com.carin.data.remote

import com.carin.data.remote.dto.TokenRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {
    @POST("/tokens/add-token")
    fun addToken(@Body request: TokenRequest): Call<ResponseBody>
}
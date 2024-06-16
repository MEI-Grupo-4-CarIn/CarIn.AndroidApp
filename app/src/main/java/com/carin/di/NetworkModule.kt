package com.carin.di

import android.content.Context
import com.carin.data.remote.AuthService
import com.carin.data.remote.RouteService
import com.carin.data.remote.TokenService
import com.carin.data.remote.UserService
import com.carin.data.remote.VehicleService
import com.carin.utils.AuthInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val GATEWAY_URL = "http://2.80.118.111:9000/"
    private const val TOKEN_URL = "http://2.80.118.111:3002/"
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create()
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

    private fun createRetrofit(context: Context, isToken: Boolean = false): Retrofit {
        return Retrofit.Builder()
            .baseUrl((if (isToken) TOKEN_URL else GATEWAY_URL))
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun provideAuthService(context: Context): AuthService {
        return createRetrofit(context).create(AuthService::class.java)
    }

    fun provideUserService(context: Context): UserService {
        return createRetrofit(context).create(UserService::class.java)
    }

    fun provideRouteService(context: Context): RouteService {
        return createRetrofit(context).create(RouteService::class.java)
    }

    fun provideVehicleService(context: Context): VehicleService {
        return createRetrofit(context).create(VehicleService::class.java)
    }

    fun provideTokenService(context: Context): TokenService {
        return createRetrofit(context, true).create(TokenService::class.java)
    }

}
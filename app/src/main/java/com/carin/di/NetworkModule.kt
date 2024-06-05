package com.carin.di

import android.content.Context
import com.carin.data.remote.AuthService
import com.carin.data.remote.RouteService
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

    private fun createRetrofit(baseUrl: String, context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun provideAuthService(context: Context): AuthService {
        return createRetrofit("http://13.50.63.152:9000/", context).create(AuthService::class.java)
    }

    fun provideUserService(context: Context): UserService {
        return createRetrofit("http://13.50.63.152:9000/", context).create(UserService::class.java)
    }

    fun provideRouteService(context: Context): RouteService {
        return createRetrofit("http://13.50.63.152:9000/", context).create(RouteService::class.java)
    }

    fun provideVehicleService(context: Context): VehicleService {
        return createRetrofit("http://13.50.63.152:9000/", context).create(VehicleService::class.java)
    }

}
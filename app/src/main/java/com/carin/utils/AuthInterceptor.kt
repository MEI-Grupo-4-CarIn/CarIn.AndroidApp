package com.carin.utils

import android.content.Context
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        var userAuth = AuthUtils.getUserAuth(context)
        if (userAuth == null) {
            return chain.proceed(originalRequest)
        }

        if (!AuthUtils.isAuthenticated(context)) {
            runBlocking {
                AuthUtils.refreshTokenBlocking(context, userAuth!!.refreshToken)
            }

            userAuth = AuthUtils.getUserAuth(context)
            if (userAuth == null) {
                return chain.proceed(originalRequest)
            }
        }

        // Attach the token to the request
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer ${userAuth.token}")
            .build()

        // Make the request
        val response = chain.proceed(authenticatedRequest)

        return response
    }
}

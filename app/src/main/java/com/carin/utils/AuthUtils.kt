package com.carin.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.carin.R
import com.carin.data.remote.dto.auth.AuthLoginRequest
import com.carin.data.remote.dto.auth.AuthRefreshTokenRequest
import com.carin.data.remote.dto.auth.AuthTokenDto
import com.carin.di.NetworkModule
import com.carin.domain.enums.Role
import com.carin.domain.models.UserAuth
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import io.jsonwebtoken.impl.DefaultClaims
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.Date

object AuthUtils {
    private val gson = Gson()

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    private fun saveUserOnSharedPreferences(context: Context, loginResponse: AuthTokenDto) {
        val objectMapper = jacksonObjectMapper()
        val parts = loginResponse.token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token")
        }

        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
        val mappedClaims: Map<String, Any> = objectMapper.readValue(payload, object : TypeReference<Map<String, Any>>() {})

        val claims = DefaultClaims(mappedClaims)

        val role = Role.fromId(claims["role"].toString().toInt())
            ?: throw IllegalArgumentException("Invalid role")

        val userAuth = UserAuth(
            claims["id"].toString().toInt(),
            claims["email"].toString(),
            claims["firstName"].toString(),
            claims["lastName"].toString(),
            role,
            loginResponse.token,
            loginResponse.refreshToken,
            Date(claims["exp"].toString().toLong() * 1000)
        )

        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        val userJson = gson.toJson(userAuth)
        editor.putString("user", userJson)
        editor.apply()
    }

    suspend fun refreshToken(
        context: Context,
        refreshToken: String
    ): Flow<Boolean>  {
        return flow {
            val authService = NetworkModule.provideAuthService(context)

            try {
                val response = authService.refreshToken(AuthRefreshTokenRequest(refreshToken)).execute()
                if (response.isSuccessful) {
                    val authTokenDto = response.body()
                    if (authTokenDto != null) {
                        saveUserOnSharedPreferences(context, authTokenDto)
                        emit(true)
                    } else {
                        emit(false)
                    }
                } else {
                    emit(false)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                emit(false)
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(false)
            }
        }.flowOn(Dispatchers.IO)
    }

    fun isAuthenticated(context: Context): Boolean {
        val userAuth = getUserAuth(context)
        val isAuthenticated = userAuth != null
        val isOnline = NetworkUtils.isNetworkAvailable(context)

        if (isAuthenticated) {
            if (!isOnline)
                return true

            val currentDate = Date()
            if (currentDate > userAuth?.expiresOn) {
                clearUserOnSharedPreferences(context)
                return false
            }

            return true
        }

        return false
    }

    suspend fun authenticateUser(
        context: Context,
        email: String,
        password: String
    ): Flow<Resource<Boolean>> {
        return flow {
            val authService = NetworkModule.provideAuthService(context)

            try {
                val response = authService.login(AuthLoginRequest(email, password)).execute()
                if (response.isSuccessful) {
                    val authLoginResponse = response.body()
                    if (authLoginResponse != null) {
                        saveUserOnSharedPreferences(context, authLoginResponse)
                        emit(Resource.Success(true))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let {
                        try {
                            JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            response.message()
                        }
                    } ?: response.message()
                    emit(Resource.Error("Login failed: $errorMessage"))
                }
            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("An error occurred"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("An error occurred"))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getUserAuth(context: Context): UserAuth? {
        val sharedPreferences = getSharedPreferences(context)
        val userJson = sharedPreferences.getString("user", "")

        if (userJson.isNullOrEmpty()) {
            return null
        }

        val userAuth = gson.fromJson(userJson, UserAuth::class.java)
        return userAuth
    }

    fun updateUserAuth(context: Context, firstName: String?, lastName: String?, email: String?) {
        val userAuth = getUserAuth(context)

        val updatedUserAuth = userAuth?.copy(
            firstName = firstName?: userAuth.firstName,
            lastName = lastName?: userAuth.lastName,
            email = email?: userAuth.email
        )

        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        val userJson = gson.toJson(updatedUserAuth)
        editor.putString("user", userJson)
        editor.apply()
    }

    fun clearUserOnSharedPreferences(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.remove("user")
        editor.apply()
    }

    suspend fun refreshTokenBlocking(context: Context, refreshToken: String): Boolean {
        return refreshToken(context, refreshToken).single()
    }
}
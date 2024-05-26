package com.carin.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.carin.R
import com.carin.data.models.auth.UserAuth
import com.carin.data.models.response.AuthLoginResponse
import com.carin.domain.enums.Role
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultClaims
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.jackson.io.JacksonDeserializer
import java.util.Date

object AuthUtils {
    private val gson = Gson()

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    fun isAuthenticated(context: Context): Boolean {
        getUserAuth(context) ?: return false

        return true
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

    fun saveUserOnSharedPreferences(context: Context, loginResponse: AuthLoginResponse) {
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

    fun clearUserOnSharedPreferences(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.remove("user")
        editor.apply()
    }
}
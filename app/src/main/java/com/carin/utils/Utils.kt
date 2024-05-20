package com.carin.utils

import android.content.Context
import android.content.SharedPreferences
import com.carin.R

object Utils {
    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
    }
}
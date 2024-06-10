package com.carin.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ConvertersUtils {
    fun convertBirthDateToCSharpFormat(birthDate: String): String? {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val date: Date? = inputFormat.parse(birthDate)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return date?.let { outputFormat.format(it) }
    }
}
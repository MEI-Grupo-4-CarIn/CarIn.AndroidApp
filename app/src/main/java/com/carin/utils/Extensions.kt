package com.carin.utils

import android.content.Context

fun Context.getStringResourceByName(name: String): String {
    val resId = resources.getIdentifier(name, "string", packageName)
    return if (resId != 0) getString(resId) else name
}

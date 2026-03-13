package com.ambica.auto.app.utils.ext

import okhttp3.ResponseBody
import org.json.JSONObject

fun ResponseBody?.extractError(): String {
    return try {
        if (this != null) JSONObject(this.charStream().readText()).optString("message", "Something went wrong") else "Something went wrong"
    } catch (e: Exception) {
        this?.string() ?: "Something went wrong"
    }
}

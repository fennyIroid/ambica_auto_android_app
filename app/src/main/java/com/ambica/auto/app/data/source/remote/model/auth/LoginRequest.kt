package com.ambica.auto.app.data.source.remote.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Request body for POST /auth/login/
 * role: 1=Owner, 2=System Admin, 3=Staff
 */
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: Int,
)

package com.ambica.auto.app.data.source.remote.model.auth

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String,
)

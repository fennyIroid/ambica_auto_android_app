package com.ambica.auto.app.data.source.remote.model.auth

import com.google.gson.annotations.SerializedName

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String,
)

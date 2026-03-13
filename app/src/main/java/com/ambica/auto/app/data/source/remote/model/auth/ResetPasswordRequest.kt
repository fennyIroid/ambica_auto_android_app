package com.ambica.auto.app.data.source.remote.model.auth

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String,
    @SerializedName("new_password") val newPassword: String,
    @SerializedName("confirm_password") val confirmPassword: String,
)

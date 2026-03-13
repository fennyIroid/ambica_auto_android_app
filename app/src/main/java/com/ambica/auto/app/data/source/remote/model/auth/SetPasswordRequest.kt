package com.ambica.auto.app.data.source.remote.model.auth

import com.google.gson.annotations.SerializedName

data class SetPasswordRequest(
    @SerializedName("new_password") val newPassword: String,
    @SerializedName("confirm_password") val confirmPassword: String,
)

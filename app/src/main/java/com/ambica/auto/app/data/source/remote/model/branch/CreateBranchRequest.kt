package com.ambica.auto.app.data.source.remote.model.branch

import com.google.gson.annotations.SerializedName

data class CreateBranchRequest(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("location") val location: String,
    @SerializedName("tagline") val tagline: String? = null,
    @SerializedName("address") val address: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("status") val status: Int? = null,
)

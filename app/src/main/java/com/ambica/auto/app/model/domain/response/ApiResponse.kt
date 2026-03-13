package com.ambica.auto.app.model.domain.response

import com.google.gson.annotations.SerializedName

open class ApiResponse<T>(
    @SerializedName("data") var data: T? = null,
    @SerializedName("message") val message: String = "",
    @SerializedName("error") val errorMsg: String = "",
    @SerializedName("errors") var apiErrors: ApiErrors? = null,
    @SerializedName("status") val status: Boolean = false,
)

data class ApiErrors(
    val email: ArrayList<String> = ArrayList(),
    val password: ArrayList<String> = ArrayList(),
)

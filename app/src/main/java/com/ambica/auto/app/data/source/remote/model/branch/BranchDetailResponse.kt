package com.ambica.auto.app.data.source.remote.model.branch

import com.google.gson.annotations.SerializedName

data class BranchDetailResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: BranchItemResponse?,
)

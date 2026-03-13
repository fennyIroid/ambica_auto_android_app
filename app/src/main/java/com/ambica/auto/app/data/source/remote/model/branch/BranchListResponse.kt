package com.ambica.auto.app.data.source.remote.model.branch

import com.google.gson.annotations.SerializedName

data class BranchListResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<BranchItemResponse>?,
)

data class BranchItemResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("status_display") val statusDisplay: String?,
    @SerializedName("manager_name") val managerName: String?,
    @SerializedName("manager_names") val managerNames: String?,
    @SerializedName("total_staff") val totalStaff: Int?,
    @SerializedName("staff_count") val staffCount: Int?,
    @SerializedName("created_at") val createdAt: Any?,
)

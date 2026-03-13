package com.ambica.auto.app.data.source.remote.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Request body for PATCH /auth/profile/
 * All fields are optional - send only what you want to change.
 * Gender values: 1=Male, 2=Female, 3=Other
 */
data class UpdateProfileRequest(
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("date_of_birth") val dateOfBirth: String? = null,
    @SerializedName("gender") val gender: Int? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("emergency_contact_name") val emergencyContactName: String? = null,
    @SerializedName("emergency_contact_phone") val emergencyContactPhone: String? = null,
    @SerializedName("emergency_contact_relationship") val emergencyContactRelationship: String? = null,
)

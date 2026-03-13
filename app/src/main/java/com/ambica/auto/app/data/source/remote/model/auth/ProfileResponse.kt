package com.ambica.auto.app.data.source.remote.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Response for GET/PATCH /auth/profile/
 *
 * The backend wraps the actual user profile inside:
 * {
 *   "message": "...",
 *   "data": { "user_profile": { ... } }
 * }
 */
data class ProfileResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: ProfileDataResponse?,
)

data class ProfileDataResponse(
    @SerializedName("user_profile") val userProfile: UserProfileResponse?,
)

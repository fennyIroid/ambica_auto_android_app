package com.ambica.auto.app.data.source.remote.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Response for POST /auth/login/
 * 200: Returns message + data (user_profile + access_token)
 */
data class LoginResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: LoginDataResponse?,
)

data class LoginDataResponse(
    @SerializedName("user_profile") val userProfile: UserProfileResponse?,
    @SerializedName("access_token") val accessToken: AccessTokenResponse?,
    @SerializedName("password_change_required") val passwordChangeRequired: Boolean?,
)

data class AccessTokenResponse(
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("expires_in") val expiresIn: Long?,
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
)

/**
 * role: 1=Owner, 2=System Admin, 3=Staff
 * staff_type: 1=Security, 2=Supervisor, 3=Manager, 4=Billing, 5=Spare Parts
 * gender: 1=Male, 2=Female, 3=Other
 */
data class UserProfileResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("role") val role: Int?,
    @SerializedName("role_display") val roleDisplay: String?,
    @SerializedName("staff_type") val staffType: Int?,
    @SerializedName("staff_type_display") val staffTypeDisplay: String?,
    @SerializedName("invite_status") val inviteStatus: Int?,
    @SerializedName("invite_status_display") val inviteStatusDisplay: String?,
    @SerializedName("has_changed_password") val hasChangedPassword: Boolean?,
    @SerializedName("profile_image") val profileImage: String?,
    @SerializedName("gender") val gender: Int?,
    @SerializedName("gender_display") val genderDisplay: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("branch") val branch: String?,
    @SerializedName("owner_branches") val ownerBranches: List<String>?,
    @SerializedName("is_global_owner") val isGlobalOwner: Boolean?,
    @SerializedName("is_active") val isActive: Boolean?,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("emergency_contact_name") val emergencyContactName: String?,
    @SerializedName("emergency_contact_phone") val emergencyContactPhone: String?,
    @SerializedName("emergency_contact_relationship") val emergencyContactRelationship: String?,
    @SerializedName("date_joined") val dateJoined: Long?,
)

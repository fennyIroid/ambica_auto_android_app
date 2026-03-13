package com.ambica.auto.app.data.source.remote.repository

import com.ambica.auto.app.data.source.remote.EndPoints
import com.ambica.auto.app.data.source.remote.model.auth.ChangePasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.ForgotPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.ResetPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.SetPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.VerifyOtpRequest
import com.ambica.auto.app.data.source.remote.model.auth.LoginRequest
import com.ambica.auto.app.data.source.remote.model.auth.LoginResponse
import com.ambica.auto.app.data.source.remote.model.auth.ProfileResponse
import com.ambica.auto.app.data.source.remote.model.auth.UpdateProfileRequest
import com.ambica.auto.app.data.source.remote.model.branch.BranchDetailResponse
import com.ambica.auto.app.data.source.remote.model.branch.BranchListResponse
import com.ambica.auto.app.data.source.remote.model.branch.CreateBranchRequest
import com.ambica.auto.app.data.source.remote.model.document_checklist.DocumentChecklistListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServices {

    @GET(EndPoints.Misc.PING)
    suspend fun ping(): Response<Unit>

    @POST(EndPoints.Auth.LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET(EndPoints.Auth.PROFILE)
    suspend fun getProfile(): Response<ProfileResponse>

    @PATCH(EndPoints.Auth.PROFILE)
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ProfileResponse>

    @POST(EndPoints.Auth.LOGOUT)
    suspend fun logout(): Response<Unit>

    @POST(EndPoints.Auth.CHANGE_PASSWORD)
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Unit>

    @POST(EndPoints.Auth.FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST(EndPoints.Auth.RESET_PASSWORD)
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

    @POST(EndPoints.Auth.SET_PASSWORD)
    suspend fun setPassword(@Body request: SetPasswordRequest): Response<Unit>

    @POST(EndPoints.Auth.VERIFY_OTP)
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<Unit>

    @GET(EndPoints.Branches.LIST)
    suspend fun getBranches(
        @Query("search") search: String? = null,
        @Query("status") status: Int? = null,
    ): Response<BranchListResponse>

    @POST(EndPoints.Branches.CREATE)
    suspend fun createBranch(@Body request: CreateBranchRequest): Response<Unit>

    @PATCH(EndPoints.Branches.UPDATE)
    suspend fun updateBranch(
        @Query("branch_id") branchId: String,
        @Body request: CreateBranchRequest,
    ): Response<Unit>

    @DELETE(EndPoints.Branches.DELETE)
    suspend fun deleteBranch(@Query("branch_id") branchId: String): Response<Unit>

    @GET(EndPoints.Branches.DETAIL)
    suspend fun getBranchDetail(@Query("branch_id") branchId: String): Response<BranchDetailResponse>

    @GET(EndPoints.DocumentChecklists.LIST)
    suspend fun getDocumentChecklists(
        @Query("search") search: String? = null,
        @Query("document_category") documentCategory: Int? = null,
        @Query("applicable_for") applicableFor: Int? = null,
        @Query("is_mandatory") isMandatory: Boolean? = null,
        @Query("ordering") ordering: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
    ): Response<DocumentChecklistListResponse>
}

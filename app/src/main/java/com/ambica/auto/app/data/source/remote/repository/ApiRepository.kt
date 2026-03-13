package com.ambica.auto.app.data.source.remote.repository

import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.model.auth.ChangePasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.ForgotPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.ResetPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.SetPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.VerifyOtpRequest
import com.ambica.auto.app.data.source.remote.model.auth.LoginResponse
import com.ambica.auto.app.data.source.remote.model.branch.BranchDetailResponse
import com.ambica.auto.app.data.source.remote.model.branch.BranchListResponse
import com.ambica.auto.app.data.source.remote.model.branch.CreateBranchRequest
import com.ambica.auto.app.data.source.remote.model.document_checklist.DocumentChecklistListResponse
import com.ambica.auto.app.data.source.remote.model.auth.UpdateProfileRequest
import com.ambica.auto.app.data.source.remote.model.auth.UserProfileResponse
import kotlinx.coroutines.flow.Flow

interface ApiRepository {

    suspend fun ping(): Flow<NetworkResult<Unit>>

    suspend fun login(email: String, password: String, role: Int): Flow<NetworkResult<LoginResponse>>

    suspend fun getProfile(): Flow<NetworkResult<UserProfileResponse>>

    suspend fun updateProfile(request: UpdateProfileRequest): Flow<NetworkResult<UserProfileResponse>>

    suspend fun logout(): Flow<NetworkResult<Unit>>

    suspend fun changePassword(request: ChangePasswordRequest): Flow<NetworkResult<Unit>>

    suspend fun forgotPassword(request: ForgotPasswordRequest): Flow<NetworkResult<Unit>>

    suspend fun resetPassword(request: ResetPasswordRequest): Flow<NetworkResult<Unit>>

    suspend fun setPassword(request: SetPasswordRequest): Flow<NetworkResult<Unit>>

    suspend fun verifyOtp(request: VerifyOtpRequest): Flow<NetworkResult<Unit>>

    suspend fun getBranches(search: String? = null, status: Int? = null): Flow<NetworkResult<BranchListResponse>>

    suspend fun createBranch(request: CreateBranchRequest): Flow<NetworkResult<Unit>>

    suspend fun updateBranch(branchId: String, request: CreateBranchRequest): Flow<NetworkResult<Unit>>

    suspend fun deleteBranch(branchId: String): Flow<NetworkResult<Unit>>

    suspend fun getBranchDetail(branchId: String): Flow<NetworkResult<BranchDetailResponse>>

    suspend fun getDocumentChecklists(
        search: String? = null,
        documentCategory: Int? = null,
        applicableFor: Int? = null,
        isMandatory: Boolean? = null,
        ordering: String? = null,
        page: Int? = null,
        perPage: Int? = null,
    ): Flow<NetworkResult<DocumentChecklistListResponse>>
}

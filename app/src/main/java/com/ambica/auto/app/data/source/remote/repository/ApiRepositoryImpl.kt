package com.ambica.auto.app.data.source.remote.repository

import com.ambica.auto.app.data.source.remote.helper.NetworkResult
import com.ambica.auto.app.data.source.remote.model.auth.ChangePasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.ForgotPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.ResetPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.SetPasswordRequest
import com.ambica.auto.app.data.source.remote.model.auth.VerifyOtpRequest
import com.ambica.auto.app.data.source.remote.model.auth.LoginRequest
import com.ambica.auto.app.data.source.remote.model.auth.LoginResponse
import com.ambica.auto.app.data.source.remote.model.branch.BranchDetailResponse
import com.ambica.auto.app.data.source.remote.model.branch.BranchListResponse
import com.ambica.auto.app.data.source.remote.model.branch.CreateBranchRequest
import com.ambica.auto.app.data.source.remote.model.document_checklist.DocumentChecklistListResponse
import com.ambica.auto.app.data.source.remote.model.auth.UpdateProfileRequest
import com.ambica.auto.app.data.source.remote.model.auth.UserProfileResponse
import com.ambica.auto.app.utils.ext.extractError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(
    private val apiServices: ApiServices
) : ApiRepository {

    override suspend fun ping(): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.ping()
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun login(
        email: String,
        password: String,
        role: Int,
    ): Flow<NetworkResult<LoginResponse>> = flow {
        try {
            val response = apiServices.login(LoginRequest(email, password, role))
            if (response.isSuccessful) {
                emit(NetworkResult.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun getProfile(): Flow<NetworkResult<UserProfileResponse>> = flow {
        try {
            val response = apiServices.getProfile()
            if (response.isSuccessful) {
                val profile = response.body()?.data?.userProfile
                emit(NetworkResult.Success(profile))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): Flow<NetworkResult<UserProfileResponse>> = flow {
        try {
            val response = apiServices.updateProfile(request)
            if (response.isSuccessful) {
                val profile = response.body()?.data?.userProfile
                emit(NetworkResult.Success(profile))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun logout(): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.logout()
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun changePassword(request: ChangePasswordRequest): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.changePassword(request)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun forgotPassword(request: ForgotPasswordRequest): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.forgotPassword(request)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.resetPassword(request)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun verifyOtp(request: VerifyOtpRequest): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.verifyOtp(request)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun setPassword(request: SetPasswordRequest): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.setPassword(request)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun getBranches(search: String?, status: Int?): Flow<NetworkResult<BranchListResponse>> = flow {
        try {
            val response = apiServices.getBranches(search = search, status = status)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(response.body()))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun createBranch(request: CreateBranchRequest): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.createBranch(request)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                if (response.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            if (e.code() == 401) emit(NetworkResult.UnAuthenticated(e.message))
            else emit(NetworkResult.Error(e.message))
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun updateBranch(branchId: String, request: CreateBranchRequest): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.updateBranch(branchId = branchId, request = request)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                when (response.code()) {
                    401 -> emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                    404 -> emit(NetworkResult.Error("Branch not found."))
                    else -> emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(NetworkResult.UnAuthenticated(e.message))
                404 -> emit(NetworkResult.Error("Branch not found."))
                else -> emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun deleteBranch(branchId: String): Flow<NetworkResult<Unit>> = flow {
        try {
            val response = apiServices.deleteBranch(branchId)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(Unit))
            } else {
                when (response.code()) {
                    401 -> emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                    404 -> emit(NetworkResult.Error("Branch not found."))
                    else -> emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(NetworkResult.UnAuthenticated(e.message))
                404 -> emit(NetworkResult.Error("Branch not found."))
                else -> emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun getBranchDetail(branchId: String): Flow<NetworkResult<BranchDetailResponse>> = flow {
        try {
            val response = apiServices.getBranchDetail(branchId)
            if (response.isSuccessful) {
                emit(NetworkResult.Success(response.body()))
            } else {
                when (response.code()) {
                    401 -> emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                    404 -> emit(NetworkResult.Error("Branch not found."))
                    else -> emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(NetworkResult.UnAuthenticated(e.message))
                404 -> emit(NetworkResult.Error("Branch not found."))
                else -> emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }

    override suspend fun getDocumentChecklists(
        search: String?,
        documentCategory: Int?,
        applicableFor: Int?,
        isMandatory: Boolean?,
        ordering: String?,
        page: Int?,
        perPage: Int?,
    ): Flow<NetworkResult<DocumentChecklistListResponse>> = flow {
        try {
            val response = apiServices.getDocumentChecklists(
                search = search,
                documentCategory = documentCategory,
                applicableFor = applicableFor,
                isMandatory = isMandatory,
                ordering = ordering,
                page = page,
                perPage = perPage,
            )
            if (response.isSuccessful) {
                emit(NetworkResult.Success(response.body()))
            } else {
                when (response.code()) {
                    401 -> emit(NetworkResult.UnAuthenticated(response.errorBody().extractError()))
                    403 -> emit(NetworkResult.Error("Only Owner or System Admin can access this."))
                    else -> emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            }
        } catch (e: IOException) {
            emit(NetworkResult.Error("No internet connection"))
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(NetworkResult.UnAuthenticated(e.message))
                403 -> emit(NetworkResult.Error("Only Owner or System Admin can access this."))
                else -> emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch {
        emit(NetworkResult.Error(it.message))
    }
}

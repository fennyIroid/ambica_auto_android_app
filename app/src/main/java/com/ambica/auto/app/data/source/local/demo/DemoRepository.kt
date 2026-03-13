package com.ambica.auto.app.data.source.local.demo

import com.ambica.auto.app.model.domain.Branch
import com.ambica.auto.app.model.domain.StaffRole
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobId
import com.ambica.auto.app.model.domain.job.JobStage
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.model.domain.job.HoldReason
import com.ambica.auto.app.model.domain.job.PartItem
import com.ambica.auto.app.model.domain.job.PartStatus
import com.ambica.auto.app.data.source.remote.model.auth.UserProfileResponse
import com.ambica.auto.app.model.domain.job.ProgressPhoto
import kotlinx.coroutines.flow.StateFlow

data class Session(
    val isLoggedIn: Boolean,
    val staffName: String? = null,
    val role: StaffRole? = null,
    val branch: Branch? = null,
)

data class JobQuery(
    val search: String = "",
    val statuses: Set<JobStatus> = emptySet(),
    val branchIds: Set<String> = emptySet(),
    val sort: JobSort = JobSort.LATEST_UPDATE,
)

enum class JobSort {
    LATEST_UPDATE,
    OLDEST_PENDING,
    DELIVERY_DATE,
}

interface DemoRepository {
    val sessionFlow: StateFlow<Session>
    val branchesFlow: StateFlow<List<Branch>>
    val allJobsFlow: StateFlow<List<Job>>

    fun jobsFlow(query: JobQuery): StateFlow<List<Job>>
    fun jobFlow(id: JobId): StateFlow<Job?>

    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun selectBranch(branchId: String)
    suspend fun selectRole(role: StaffRole)
    suspend fun updateSessionFromProfile(profile: UserProfileResponse)

    suspend fun createGateEntryJob(job: Job): Result<JobId>
    suspend fun updateJobStatus(jobId: JobId, status: JobStatus, holdReason: HoldReason? = null)
    suspend fun updateJobStage(jobId: JobId, stage: JobStage)
    suspend fun addProgressPhoto(jobId: JobId, stage: JobStage, photo: ProgressPhoto)

    suspend fun setPartStatus(jobId: JobId, partId: String, status: PartStatus)
    suspend fun upsertPart(jobId: JobId, part: PartItem)

    suspend fun updateEstimate(jobId: JobId, amount: Double, pdfName: String?)
    suspend fun toggleDocumentReceived(jobId: JobId, docId: String, received: Boolean)
    suspend fun updateInsurance(
        jobId: JobId,
        surveyorName: String?,
        approvalStatus: com.ambica.auto.app.model.domain.job.ApprovalStatus,
        notes: String,
    )
    suspend fun updateBilling(
        jobId: JobId,
        insuranceLiability: Double,
        customerLiability: Double,
        shortfallAmount: Double,
        paymentMode: com.ambica.auto.app.model.domain.job.PaymentMode,
        paymentVerified: Boolean,
    )
    suspend fun generateGatePass(jobId: JobId)
    suspend fun confirmGateOut(jobId: JobId)
}

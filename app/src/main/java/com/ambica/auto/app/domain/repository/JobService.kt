package com.ambica.auto.app.domain.repository

import com.ambica.auto.app.model.domain.Branch
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobId
import com.ambica.auto.app.model.domain.job.JobStage
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.model.domain.job.HoldReason
import com.ambica.auto.app.model.domain.job.PartItem
import com.ambica.auto.app.model.domain.job.PartStatus
import com.ambica.auto.app.model.domain.job.ProgressPhoto
import kotlinx.coroutines.flow.StateFlow

/**
 * Service layer for job operations. UI and UseCases call this instead of raw data sources.
 * Currently backed by mock (DemoRepository); replace with API implementation when ready.
 */
interface JobService {

    val allJobsFlow: StateFlow<List<Job>>

    fun jobsFlow(query: JobQuery): StateFlow<List<Job>>
    fun jobFlow(id: JobId): StateFlow<Job?>

    suspend fun createGateEntryJob(job: Job): Result<JobId>
    suspend fun updateJobStatus(jobId: JobId, status: JobStatus, holdReason: HoldReason? = null): Result<Unit>
    suspend fun updateJobStage(jobId: JobId, stage: JobStage): Result<Unit>
    suspend fun addProgressPhoto(jobId: JobId, stage: JobStage, photo: ProgressPhoto): Result<Unit>

    suspend fun setPartStatus(jobId: JobId, partId: String, status: PartStatus): Result<Unit>
    suspend fun upsertPart(jobId: JobId, part: PartItem): Result<Unit>

    suspend fun updateEstimate(jobId: JobId, amount: Double, pdfName: String?): Result<Unit>
    suspend fun toggleDocumentReceived(jobId: JobId, docId: String, received: Boolean): Result<Unit>
    suspend fun updateInsurance(
        jobId: JobId,
        surveyorName: String?,
        approvalStatus: com.ambica.auto.app.model.domain.job.ApprovalStatus,
        notes: String,
    ): Result<Unit>
    suspend fun updateBilling(
        jobId: JobId,
        insuranceLiability: Double,
        customerLiability: Double,
        shortfallAmount: Double,
        paymentMode: com.ambica.auto.app.model.domain.job.PaymentMode,
        paymentVerified: Boolean,
    ): Result<Unit>
    suspend fun generateGatePass(jobId: JobId): Result<Unit>
    suspend fun confirmGateOut(jobId: JobId): Result<Unit>
}

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

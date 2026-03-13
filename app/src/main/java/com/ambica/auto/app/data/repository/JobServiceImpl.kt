package com.ambica.auto.app.data.repository

import com.ambica.auto.app.data.source.local.demo.DemoRepository
import com.ambica.auto.app.data.source.local.demo.JobQuery as DemoJobQuery
import com.ambica.auto.app.data.source.local.demo.JobSort as DemoJobSort
import com.ambica.auto.app.domain.repository.JobQuery
import com.ambica.auto.app.domain.repository.JobService
import com.ambica.auto.app.domain.repository.JobSort
import com.ambica.auto.app.model.domain.job.HoldReason
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobId
import com.ambica.auto.app.model.domain.job.JobStage
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.model.domain.job.PartItem
import com.ambica.auto.app.model.domain.job.PartStatus
import com.ambica.auto.app.model.domain.job.ProgressPhoto
import com.ambica.auto.app.model.domain.job.StatusTransition
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobServiceImpl @Inject constructor(
    private val demoRepository: DemoRepository,
) : JobService {

    override val allJobsFlow: StateFlow<List<Job>> = demoRepository.allJobsFlow

    override fun jobsFlow(query: JobQuery): StateFlow<List<Job>> {
        val demoQuery = DemoJobQuery(
            search = query.search,
            statuses = query.statuses,
            branchIds = query.branchIds,
            sort = when (query.sort) {
                JobSort.LATEST_UPDATE -> DemoJobSort.LATEST_UPDATE
                JobSort.OLDEST_PENDING -> DemoJobSort.OLDEST_PENDING
                JobSort.DELIVERY_DATE -> DemoJobSort.DELIVERY_DATE
            },
        )
        return demoRepository.jobsFlow(demoQuery)
    }

    override fun jobFlow(id: JobId): StateFlow<Job?> = demoRepository.jobFlow(id)

    override suspend fun createGateEntryJob(job: Job): Result<JobId> =
        demoRepository.createGateEntryJob(job)

    override suspend fun updateJobStatus(jobId: JobId, status: JobStatus, holdReason: HoldReason?): Result<Unit> {
        val job = demoRepository.jobFlow(jobId).value ?: return Result.failure(IllegalStateException("Job not found"))
        val error = StatusTransition.validateTransition(job.status, status, holdReason)
        if (error != null) return Result.failure(IllegalArgumentException(error))
        demoRepository.updateJobStatus(jobId, status, holdReason)
        return Result.success(Unit)
    }

    override suspend fun updateJobStage(jobId: JobId, stage: JobStage): Result<Unit> {
        demoRepository.updateJobStage(jobId, stage)
        return Result.success(Unit)
    }

    override suspend fun addProgressPhoto(jobId: JobId, stage: JobStage, photo: ProgressPhoto): Result<Unit> {
        demoRepository.addProgressPhoto(jobId, stage, photo)
        return Result.success(Unit)
    }

    override suspend fun setPartStatus(jobId: JobId, partId: String, status: PartStatus): Result<Unit> {
        demoRepository.setPartStatus(jobId, partId, status)
        return Result.success(Unit)
    }

    override suspend fun upsertPart(jobId: JobId, part: PartItem): Result<Unit> {
        demoRepository.upsertPart(jobId, part)
        return Result.success(Unit)
    }

    override suspend fun updateEstimate(jobId: JobId, amount: Double, pdfName: String?): Result<Unit> {
        demoRepository.updateEstimate(jobId, amount, pdfName)
        return Result.success(Unit)
    }

    override suspend fun toggleDocumentReceived(jobId: JobId, docId: String, received: Boolean): Result<Unit> {
        demoRepository.toggleDocumentReceived(jobId, docId, received)
        return Result.success(Unit)
    }

    override suspend fun updateInsurance(
        jobId: JobId,
        surveyorName: String?,
        approvalStatus: com.ambica.auto.app.model.domain.job.ApprovalStatus,
        notes: String,
    ): Result<Unit> {
        demoRepository.updateInsurance(jobId, surveyorName, approvalStatus, notes)
        return Result.success(Unit)
    }

    override suspend fun updateBilling(
        jobId: JobId,
        insuranceLiability: Double,
        customerLiability: Double,
        shortfallAmount: Double,
        paymentMode: com.ambica.auto.app.model.domain.job.PaymentMode,
        paymentVerified: Boolean,
    ): Result<Unit> {
        demoRepository.updateBilling(jobId, insuranceLiability, customerLiability, shortfallAmount, paymentMode, paymentVerified)
        return Result.success(Unit)
    }

    override suspend fun generateGatePass(jobId: JobId): Result<Unit> {
        demoRepository.generateGatePass(jobId)
        return Result.success(Unit)
    }

    override suspend fun confirmGateOut(jobId: JobId): Result<Unit> {
        demoRepository.confirmGateOut(jobId)
        return Result.success(Unit)
    }
}

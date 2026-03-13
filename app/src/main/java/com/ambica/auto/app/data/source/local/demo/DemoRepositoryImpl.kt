package com.ambica.auto.app.data.source.local.demo

import com.ambica.auto.app.model.domain.Branch
import com.ambica.auto.app.model.domain.StaffRole
import com.ambica.auto.app.model.domain.job.Job
import com.ambica.auto.app.model.domain.job.JobDocument
import com.ambica.auto.app.model.domain.job.JobId
import com.ambica.auto.app.model.domain.job.JobStage
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.model.domain.job.HoldReason
import com.ambica.auto.app.model.domain.job.EstimateInfo
import com.ambica.auto.app.model.domain.job.ApprovalStatus
import com.ambica.auto.app.model.domain.job.PaymentMode
import com.ambica.auto.app.model.domain.job.PartItem
import com.ambica.auto.app.model.domain.job.PartStatus
import com.ambica.auto.app.model.domain.job.ProgressPhoto
import com.ambica.auto.app.model.domain.job.ProgressUpdate
import com.ambica.auto.app.data.source.remote.model.auth.UserProfileResponse
import com.ambica.auto.app.model.domain.job.TimelineEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoRepositoryImpl @Inject constructor() : DemoRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutex = Mutex()

    private val _branchesFlow = MutableStateFlow(
        listOf(
            Branch(id = "hazira", name = "Hazira", city = "Surat"),
            Branch(id = "amboli", name = "Amboli", city = "Surat"),
            Branch(id = "ankleshwar", name = "Ankleshwar", city = "Ankleshwar"),
            Branch(id = "baroda", name = "Baroda", city = "Vadodara"),
        )
    )
    override val branchesFlow: StateFlow<List<Branch>> = _branchesFlow.asStateFlow()

    private val _sessionFlow = MutableStateFlow(Session(isLoggedIn = false))
    override val sessionFlow: StateFlow<Session> = _sessionFlow.asStateFlow()

    private val _jobsFlow = MutableStateFlow(seedJobs(branchId = "hazira"))
    override val allJobsFlow: StateFlow<List<Job>> = _jobsFlow.asStateFlow()

    override fun jobsFlow(query: JobQuery): StateFlow<List<Job>> {
        return _jobsFlow
            .map { jobs ->
                jobs
                    .filter { job ->
                        val q = query.search.trim().lowercase(Locale.getDefault())
                        val matchesSearch = if (q.isBlank()) true else {
                            job.vehicleNumber.lowercase(Locale.getDefault()).contains(q) ||
                                job.customerName.lowercase(Locale.getDefault()).contains(q) ||
                                job.jobCardNumber.lowercase(Locale.getDefault()).contains(q) ||
                                (job.claimNumber?.lowercase(Locale.getDefault())?.contains(q) == true)
                        }
                        val matchesStatus = query.statuses.isEmpty() || query.statuses.contains(job.status)
                        val matchesBranch = query.branchIds.isEmpty() || query.branchIds.contains(job.branchId)
                        matchesSearch && matchesStatus && matchesBranch
                    }
                    .sortedWith(
                        when (query.sort) {
                            JobSort.LATEST_UPDATE -> compareByDescending<Job> { it.timeline.maxOfOrNull { e -> e.atMillis } ?: it.gateEntryAtMillis }
                            JobSort.OLDEST_PENDING -> compareBy<Job> { it.gateEntryAtMillis }
                            JobSort.DELIVERY_DATE -> compareByDescending<Job> { it.gatePass.gateOutAtMillis ?: 0L }
                        }
                    )
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    override fun jobFlow(id: JobId): StateFlow<Job?> {
        return _jobsFlow
            .map { jobs -> jobs.firstOrNull { it.id == id } }
            .stateIn(scope, SharingStarted.WhileSubscribed(5_000), null)
    }

    override suspend fun login(username: String, password: String): Result<Unit> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Username/password required"))
        }
        mutex.withLock {
            val branch = null
            val role = when (username.trim().lowercase(Locale.getDefault())) {
                "gate", "gateentry", "gate_entry", "security" -> StaffRole.SECURITY
                "insurance", "survey", "surveyor" -> StaffRole.INSURANCE
                "parts", "spare", "spares" -> StaffRole.SPARE_PARTS_TEAM
                "billing", "cashier", "accountant" -> StaffRole.ACCOUNTANT
                "workshop", "technician", "supervisor" -> StaffRole.SUPERVISOR
                "crm", "operator", "crm_operator" -> StaffRole.CRM_OPERATOR
                "admin" -> StaffRole.SYSTEM_ADMIN
                "owner" -> StaffRole.OWNER
                else -> StaffRole.MANAGER
            }
            _sessionFlow.value = Session(
                isLoggedIn = true,
                staffName = username.trim().ifBlank { "Staff" },
                role = role,
                branch = branch,
            )
        }
        return Result.success(Unit)
    }

    override suspend fun logout() {
        mutex.withLock {
            _sessionFlow.value = Session(isLoggedIn = false)
        }
    }

    override suspend fun selectBranch(branchId: String) {
        mutex.withLock {
            val branch = branchesFlow.value.firstOrNull { it.id == branchId }
            _sessionFlow.value = _sessionFlow.value.copy(branch = branch)
        }
    }

    override suspend fun selectRole(role: StaffRole) {
        mutex.withLock {
            _sessionFlow.value = _sessionFlow.value.copy(role = role)
        }
    }

    override suspend fun updateSessionFromProfile(profile: UserProfileResponse) {
        mutex.withLock {
            val displayName = listOfNotNull(profile.firstName?.trim(), profile.lastName?.trim())
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { profile.email.orEmpty().ifBlank { "Staff" } }

            val role = when (profile.role) {
                1 -> StaffRole.OWNER
                2 -> StaffRole.SYSTEM_ADMIN
                3 -> when (profile.staffType) {
                    1 -> StaffRole.SECURITY
                    2 -> StaffRole.SUPERVISOR
                    3 -> StaffRole.MANAGER
                    4 -> StaffRole.BILLING
                    5 -> StaffRole.SPARE_PARTS_TEAM
                    else -> StaffRole.MANAGER
                }
                else -> null
            }

            val branchName = profile.branch?.trim().orEmpty()
            val branch = if (branchName.isNotBlank()) {
                Branch(
                    id = branchName.lowercase(Locale.getDefault()).replace(" ", "_"),
                    name = branchName,
                    city = "",
                )
            } else {
                null
            }

            _sessionFlow.value = Session(
                isLoggedIn = true,
                staffName = displayName,
                role = role,
                branch = branch,
            )
        }
    }

    override suspend fun createGateEntryJob(job: Job): Result<JobId> {
        mutex.withLock {
            val id = job.id
            val now = System.currentTimeMillis()
            _jobsFlow.value = listOf(job.copy(
                createdAt = now,
                updatedAt = now,
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Gate Entry Created",
                    atMillis = now,
                    description = "Created by ${job.createdBy}"
                )
            )) + _jobsFlow.value
            return Result.success(id)
        }
    }

    override suspend fun updateJobStatus(jobId: JobId, status: JobStatus, holdReason: HoldReason?) {
        val now = System.currentTimeMillis()
        updateJob(jobId) { job ->
            job.copy(
                status = status,
                holdReason = if (status == JobStatus.ON_HOLD) holdReason else null,
                holdSinceMillis = if (status == JobStatus.ON_HOLD) now else null,
                updatedAt = now,
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Status: ${status.label}",
                    atMillis = now,
                    description = holdReason?.label
                )
            )
        }
    }

    override suspend fun updateJobStage(jobId: JobId, stage: JobStage) {
        val now = System.currentTimeMillis()
        updateJob(jobId) { job ->
            job.copy(
                stage = stage,
                status = if (job.status == JobStatus.NEW_ENTRY || job.status == JobStatus.AWAITING_DOCUMENTS) {
                    JobStatus.APPROVED_AND_WORK_IN_PROGRESS
                } else {
                    job.status
                },
                updatedAt = now,
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Stage: ${stage.label}",
                    atMillis = now,
                )
            )
        }
    }

    override suspend fun addProgressPhoto(jobId: JobId, stage: JobStage, photo: ProgressPhoto) {
        updateJob(jobId) { job ->
            val existing = job.progress.firstOrNull { it.stage == stage }
            val updatedProgress = if (existing == null) {
                job.progress + ProgressUpdate(
                    stage = stage,
                    updatedAtMillis = System.currentTimeMillis(),
                    photos = listOf(photo),
                )
            } else {
                job.progress.map {
                    if (it.stage == stage) it.copy(
                        updatedAtMillis = System.currentTimeMillis(),
                        photos = it.photos + photo
                    ) else it
                }
            }
            job.copy(
                progress = updatedProgress,
                updatedAt = System.currentTimeMillis(),
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Progress photo added",
                    atMillis = System.currentTimeMillis(),
                    description = stage.label
                )
            )
        }
    }

    override suspend fun setPartStatus(jobId: JobId, partId: String, status: PartStatus) {
        updateJob(jobId) { job ->
            job.copy(
                parts = job.parts.map { if (it.id == partId) it.copy(status = status) else it },
                updatedAt = System.currentTimeMillis(),
            )
        }
    }

    override suspend fun upsertPart(jobId: JobId, part: PartItem) {
        updateJob(jobId) { job ->
            val exists = job.parts.any { it.id == part.id }
            job.copy(
                parts = if (exists) job.parts.map { if (it.id == part.id) part else it } else job.parts + part,
                updatedAt = System.currentTimeMillis(),
            )
        }
    }

    override suspend fun updateEstimate(jobId: JobId, amount: Double, pdfName: String?) {
        val now = System.currentTimeMillis()
        updateJob(jobId) { job ->
            job.copy(
                estimateInfo = EstimateInfo(amount = amount, pdfName = pdfName),
                status = JobStatus.AWAITING_INSURANCE_APPROVAL,
                updatedAt = now,
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Estimate updated",
                    atMillis = now,
                    description = pdfName ?: "Amount: $amount"
                )
            )
        }
    }

    override suspend fun toggleDocumentReceived(jobId: JobId, docId: String, received: Boolean) {
        updateJob(jobId) { job ->
            job.copy(
                documents = job.documents.map { if (it.id == docId) it.copy(received = received) else it },
                updatedAt = System.currentTimeMillis(),
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = if (received) "Document received" else "Document marked pending",
                    atMillis = System.currentTimeMillis(),
                    description = docId
                )
            )
        }
    }

    override suspend fun updateInsurance(jobId: JobId, surveyorName: String?, approvalStatus: ApprovalStatus, notes: String) {
        val now = System.currentTimeMillis()
        updateJob(jobId) { job ->
            job.copy(
                insuranceInfo = job.insuranceInfo.copy(
                    surveyorName = surveyorName,
                    approvalStatus = approvalStatus,
                    notes = notes,
                    approvalDateMillis = if (approvalStatus == ApprovalStatus.APPROVED) now else job.insuranceInfo.approvalDateMillis
                ),
                status = if (approvalStatus == ApprovalStatus.APPROVED) JobStatus.APPROVED_AND_WORK_IN_PROGRESS else JobStatus.AWAITING_INSURANCE_APPROVAL,
                updatedAt = now,
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Insurance: ${approvalStatus.label}",
                    atMillis = now,
                    description = surveyorName
                )
            )
        }
    }

    override suspend fun updateBilling(
        jobId: JobId,
        insuranceLiability: Double,
        customerLiability: Double,
        shortfallAmount: Double,
        paymentMode: PaymentMode,
        paymentVerified: Boolean,
    ) {
        val now = System.currentTimeMillis()
        updateJob(jobId) { job ->
            job.copy(
                billing = job.billing.copy(
                    insuranceLiability = insuranceLiability,
                    customerLiability = customerLiability,
                    shortfallAmount = shortfallAmount,
                    paymentMode = paymentMode,
                    paymentVerified = paymentVerified,
                    paymentReceived = paymentVerified
                ),
                status = if (paymentVerified) JobStatus.READY_FOR_DELIVERY else JobStatus.AWAITING_PAYMENT,
                updatedAt = now,
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = if (paymentVerified) "Payment verified" else "Billing updated",
                    atMillis = now,
                )
            )
        }
    }

    override suspend fun generateGatePass(jobId: JobId) {
        val now = System.currentTimeMillis()
        updateJob(jobId) { job ->
            if (!job.billing.paymentVerified) return@updateJob job
            job.copy(
                gatePass = job.gatePass.copy(
                    generated = true,
                    gatePassNumber = job.gatePass.gatePassNumber ?: "WAR-${(1000..9999).random()}"
                ),
                status = JobStatus.READY_FOR_DELIVERY,
                updatedAt = now,
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Gate pass generated",
                    atMillis = now,
                )
            )
        }
    }

    override suspend fun confirmGateOut(jobId: JobId) {
        val now = System.currentTimeMillis()
        updateJob(jobId) { job ->
            job.copy(
                gatePass = job.gatePass.copy(
                    gateOutAtMillis = now
                ),
                status = JobStatus.DELIVERED,
                updatedAt = now,
                timeline = job.timeline + TimelineEvent(
                    id = UUID.randomUUID().toString(),
                    title = "Gate out confirmed",
                    atMillis = now,
                )
            )
        }
    }

    private suspend fun updateJob(jobId: JobId, transform: (Job) -> Job) {
        mutex.withLock {
            _jobsFlow.value = _jobsFlow.value.map { job ->
                if (job.id == jobId) transform(job) else job
            }
        }
    }

    private fun seedJobs(branchId: String): List<Job> {
        val now = System.currentTimeMillis()
        val sixHoursAgo = now - 1000L * 60L * 60L * 6L
        val twentyTwoHoursAgo = now - 1000L * 60L * 60L * 22L
        return listOf(
            Job(
                id = JobId.random(),
                vehicleNumber = "GJ 05 AB 1234",
                customerName = "Mihir Roy",
                customerPhone = "99999 88888",
                branchId = branchId,
                jobCardNumber = "JOB-ACB-1234",
                claimNumber = "CLM-8891",
                createdBy = "Nitesh",
                gateEntryAtMillis = sixHoursAgo,
                status = JobStatus.AWAITING_INSURANCE_APPROVAL,
                stage = JobStage.DENTING,
                createdAt = sixHoursAgo,
                updatedAt = now,
                visibleNotes = "Front bumper scratch; dashboard items missing: none",
                documents = listOf(
                    JobDocument(id = "rc", title = "RC Copy", received = true),
                    JobDocument(id = "insurance", title = "Insurance Policy", received = true),
                    JobDocument(id = "kyc", title = "KYC", received = false),
                    JobDocument(id = "claimForm", title = "Claim Form", received = false),
                ),
                timeline = listOf(
                    TimelineEvent(id = UUID.randomUUID().toString(), title = "Gate Entry", atMillis = sixHoursAgo),
                    TimelineEvent(id = UUID.randomUUID().toString(), title = "Estimate uploaded", atMillis = sixHoursAgo - 1000L * 60L * 60L * 2L),
                    TimelineEvent(id = UUID.randomUUID().toString(), title = "Insurance survey scheduled", atMillis = sixHoursAgo - 1000L * 60L * 60L),
                )
            ),
            Job(
                id = JobId.random(),
                vehicleNumber = "MH 12 CD 5678",
                customerName = "Riya Mehta",
                customerPhone = "98765 43210",
                branchId = branchId,
                jobCardNumber = "JOB-XFZ-9021",
                claimNumber = null,
                createdBy = "Amit",
                gateEntryAtMillis = twentyTwoHoursAgo,
                status = JobStatus.APPROVED_AND_WORK_IN_PROGRESS,
                stage = JobStage.PAINTING,
                createdAt = twentyTwoHoursAgo,
                updatedAt = now,
                visibleNotes = "Right door dent; side mirror cracked",
                parts = listOf(
                    PartItem(id = "mirror", name = "Side mirror", status = PartStatus.ORDERED, notes = "OEM delayed"),
                    PartItem(id = "doorTrim", name = "Door trim", status = PartStatus.RECEIVED),
                ),
                timeline = listOf(
                    TimelineEvent(id = UUID.randomUUID().toString(), title = "Gate Entry", atMillis = twentyTwoHoursAgo),
                    TimelineEvent(id = UUID.randomUUID().toString(), title = "Stage: Dismantling", atMillis = twentyTwoHoursAgo - 1000L * 60L * 60L * 2L),
                    TimelineEvent(id = UUID.randomUUID().toString(), title = "Stage: Painting", atMillis = twentyTwoHoursAgo - 1000L * 60L * 60L * 12L),
                )
            ),
        )
    }
}

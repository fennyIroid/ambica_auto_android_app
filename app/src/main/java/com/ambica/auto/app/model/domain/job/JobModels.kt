package com.ambica.auto.app.model.domain.job

import java.util.UUID

enum class ApprovalStatus(val label: String) {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    RE_INSPECTION("Re-inspection needed"),
}

enum class PartStatus(val label: String) {
    REQUIRED("Required"),
    ORDERED("Ordered"),
    RECEIVED("Received"),
    INSTALLED("Installed"),
    NOT_AVAILABLE("Not available"),
    ALTERNATIVE_USED("Alternative used"),
}

enum class PaymentMode(val label: String) {
    CASH("Cash"),
    ONLINE("Online"),
    BANK_TRANSFER("Bank transfer"),
}

data class JobId(val value: String) {
    companion object {
        fun random(): JobId = JobId(UUID.randomUUID().toString())
    }
}

data class JobDocument(
    val id: String,
    val title: String,
    val required: Boolean = true,
    val received: Boolean = false,
)

data class EstimateInfo(
    val amount: Double = 0.0,
    val pdfName: String? = null,
)

data class InsuranceInfo(
    val company: String? = null,
    val surveyorName: String? = null,
    val surveyDateMillis: Long? = null,
    val approvalStatus: ApprovalStatus = ApprovalStatus.PENDING,
    val approvalDateMillis: Long? = null,
    val notes: String = "",
    val approvalDocName: String? = null,
)

data class ProgressPhoto(
    val id: String,
    val dateMillis: Long,
    val note: String = "",
    val uri: String? = null, // demo placeholder
)

data class ProgressUpdate(
    val stage: JobStage,
    val updatedAtMillis: Long,
    val note: String = "",
    val photos: List<ProgressPhoto> = emptyList(),
)

data class PartItem(
    val id: String,
    val name: String,
    val status: PartStatus = PartStatus.REQUIRED,
    val expectedDateMillis: Long? = null,
    val receivedDateMillis: Long? = null,
    val notes: String = "",
)

data class BillingInfo(
    val insuranceLiability: Double = 0.0,
    val customerLiability: Double = 0.0,
    val shortfallAmount: Double = 0.0,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val paymentProofName: String? = null,
    val paymentReceived: Boolean = false,
    val paymentVerified: Boolean = false,
)

data class GatePassInfo(
    val generated: Boolean = false,
    val gatePassNumber: String? = null,
    val gateOutAtMillis: Long? = null,
)

data class TimelineEvent(
    val id: String,
    val title: String,
    val atMillis: Long,
    val description: String? = null,
)

data class Job(
    val id: JobId,
    val vehicleNumber: String,
    val customerName: String,
    val customerPhone: String? = null,
    val branchId: String,
    val jobCardNumber: String,
    val claimNumber: String? = null,
    val createdBy: String,
    val gateEntryAtMillis: Long,
    val status: JobStatus = JobStatus.NEW_ENTRY,
    val stage: JobStage = JobStage.GATE_ENTRY_COMPLETED,
    val holdReason: HoldReason? = null,
    val holdSinceMillis: Long? = null,
    val customerId: String? = null,
    val vehicleId: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val entryPhotoUris: List<String> = emptyList(),
    val visibleNotes: String = "",
    val internalRemarks: String = "",
    val estimateInfo: EstimateInfo? = null,
    val documents: List<JobDocument> = emptyList(),
    val insuranceInfo: InsuranceInfo = InsuranceInfo(),
    val progress: List<ProgressUpdate> = emptyList(),
    val parts: List<PartItem> = emptyList(),
    val billing: BillingInfo = BillingInfo(),
    val gatePass: GatePassInfo = GatePassInfo(),
    val timeline: List<TimelineEvent> = emptyList(),
    val notes: List<JobNote> = emptyList(),
)

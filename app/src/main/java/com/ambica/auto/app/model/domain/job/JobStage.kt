package com.ambica.auto.app.model.domain.job

/**
 * Detailed operational progress inside the workshop.
 * Stages may later become configurable by admin.
 */
enum class JobStage(val label: String, val category: StageCategory) {
    // Entry
    GATE_ENTRY_COMPLETED("Gate Entry Completed", StageCategory.ENTRY),
    JOB_CARD_CREATED("Job Card Created", StageCategory.ENTRY),
    INITIAL_INSPECTION("Initial Inspection", StageCategory.ENTRY),
    ESTIMATE_PREPARED("Estimate Prepared", StageCategory.ENTRY),
    ESTIMATE_SHARED_TO_INSURANCE("Estimate Shared to Insurance", StageCategory.ENTRY),
    // Insurance
    SURVEY_SCHEDULED("Survey Scheduled", StageCategory.INSURANCE),
    SURVEY_COMPLETED("Survey Completed", StageCategory.INSURANCE),
    APPROVAL_PENDING("Approval Pending", StageCategory.INSURANCE),
    APPROVED("Approved", StageCategory.INSURANCE),
    RE_INSPECTION_REQUIRED("Re Inspection Required", StageCategory.INSURANCE),
    // Repair
    DISMANTLING("Dismantling", StageCategory.REPAIR),
    DENTING("Denting", StageCategory.REPAIR),
    PAINTING_PREPARATION("Painting Preparation", StageCategory.REPAIR),
    PAINTING("Painting", StageCategory.REPAIR),
    DRYING_AND_CURING("Drying and Curing", StageCategory.REPAIR),
    FITTING_AND_ASSEMBLY("Fitting and Assembly", StageCategory.REPAIR),
    ELECTRICAL_MECHANICAL_CHECK("Electrical & Mechanical Check", StageCategory.REPAIR),
    FINAL_QUALITY_CHECK("Final Quality Check", StageCategory.REPAIR),
    WASHING_AND_CLEANING("Washing and Cleaning", StageCategory.REPAIR),
    // Delivery
    BILLING_PREPARED("Billing Prepared", StageCategory.DELIVERY),
    PAYMENT_PENDING("Payment Pending", StageCategory.DELIVERY),
    PAYMENT_RECEIVED("Payment Received", StageCategory.DELIVERY),
    GATE_PASS_GENERATED("Gate Pass Generated", StageCategory.DELIVERY),
    GATE_OUT_COMPLETED("Gate Out Completed", StageCategory.DELIVERY),
    ;

    companion object {
        val entryStages: List<JobStage> = entries.filter { it.category == StageCategory.ENTRY }
        val insuranceStages: List<JobStage> = entries.filter { it.category == StageCategory.INSURANCE }
        val repairStages: List<JobStage> = entries.filter { it.category == StageCategory.REPAIR }
        val deliveryStages: List<JobStage> = entries.filter { it.category == StageCategory.DELIVERY }

        /** All stages in display order (entry → insurance → repair → delivery). */
        val ordered: List<JobStage> = entryStages + insuranceStages + repairStages + deliveryStages
    }
}

enum class StageCategory { ENTRY, INSURANCE, REPAIR, DELIVERY }

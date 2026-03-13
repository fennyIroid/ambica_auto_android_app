package com.ambica.auto.app.model.domain.job

/**
 * High-level job state for dashboards, filtering, and reporting.
 * Fixed set; status transitions must follow logical order.
 */
enum class JobStatus(val label: String) {
    NEW_ENTRY("New Entry"),
    AWAITING_DOCUMENTS("Awaiting Documents"),
    AWAITING_SURVEY("Awaiting Survey"),
    AWAITING_INSURANCE_APPROVAL("Awaiting Insurance Approval"),
    APPROVED_AND_WORK_IN_PROGRESS("Approved & Work In Progress"),
    ON_HOLD("On Hold"),
    VEHICLE_READY("Vehicle Ready"),
    AWAITING_PAYMENT("Awaiting Payment"),
    READY_FOR_DELIVERY("Ready for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    ;

    /** Terminal statuses from which no transition is allowed. */
    fun isTerminal(): Boolean = this == DELIVERED || this == CANCELLED

    /** Valid next statuses. Invalid transitions (e.g. New Entry → Delivered) are prevented. */
    fun allowedNextStatuses(): Set<JobStatus> = when (this) {
        NEW_ENTRY -> setOf(AWAITING_DOCUMENTS, CANCELLED)
        AWAITING_DOCUMENTS -> setOf(AWAITING_SURVEY, ON_HOLD, CANCELLED)
        AWAITING_SURVEY -> setOf(AWAITING_INSURANCE_APPROVAL, ON_HOLD, CANCELLED)
        AWAITING_INSURANCE_APPROVAL -> setOf(APPROVED_AND_WORK_IN_PROGRESS, ON_HOLD, CANCELLED)
        APPROVED_AND_WORK_IN_PROGRESS -> setOf(VEHICLE_READY, ON_HOLD, CANCELLED)
        ON_HOLD -> setOf(
            AWAITING_DOCUMENTS, AWAITING_SURVEY, AWAITING_INSURANCE_APPROVAL,
            APPROVED_AND_WORK_IN_PROGRESS, VEHICLE_READY, AWAITING_PAYMENT,
            READY_FOR_DELIVERY, CANCELLED
        )
        VEHICLE_READY -> setOf(AWAITING_PAYMENT, ON_HOLD, CANCELLED)
        AWAITING_PAYMENT -> setOf(READY_FOR_DELIVERY, ON_HOLD, CANCELLED)
        READY_FOR_DELIVERY -> setOf(DELIVERED, ON_HOLD, CANCELLED)
        DELIVERED, CANCELLED -> emptySet()
    }

    fun canTransitionTo(next: JobStatus): Boolean = next in allowedNextStatuses()
}

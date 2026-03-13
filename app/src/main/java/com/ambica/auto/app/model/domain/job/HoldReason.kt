package com.ambica.auto.app.model.domain.job

/**
 * Required when job status is On Hold.
 */
enum class HoldReason(val label: String) {
    SPARE_PARTS_PENDING("Spare parts pending"),
    INSURANCE_PENDING("Insurance pending"),
    CUSTOMER_DOCUMENTS_PENDING("Customer documents pending"),
    PAYMENT_PENDING("Payment pending"),
    TECHNICIAN_NOT_AVAILABLE("Technician not available"),
    WORKSHOP_FULL("Workshop full"),
}

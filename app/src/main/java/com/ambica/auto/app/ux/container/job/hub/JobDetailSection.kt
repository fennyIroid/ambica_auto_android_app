package com.ambica.auto.app.ux.container.job.hub

import com.ambica.auto.app.model.domain.StaffRole

/**
 * Sections on the Job Detail screen. Visibility is role-based.
 * CRM Operator sees all except Billing (no billing details, payment verification, financial data).
 */
enum class JobDetailSection(val title: String) {
    OVERVIEW("Overview"),
    DOCUMENTS("Estimate"),
    INSURANCE("Insurance"),
    REPAIR_PROGRESS("Repair Progress"),
    PARTS_TRACKING("Parts Tracking"),
    BILLING("Billing"),
    GATE_PASS("Gate Pass"),
    NOTES("Notes"),
}

/**
 * Returns true if the given role can see this section.
 * Manager, Owner, System Admin see all. CRM Operator sees all except Billing.
 */
fun JobDetailSection.isVisibleTo(role: StaffRole?): Boolean {
    if (role == null) return true
    return when (this) {
        JobDetailSection.OVERVIEW -> true
        JobDetailSection.NOTES -> true
        JobDetailSection.DOCUMENTS -> role in setOf(
            StaffRole.GATE_ENTRY, StaffRole.ESTIMATOR, StaffRole.MANAGER, StaffRole.OWNER,
            StaffRole.SYSTEM_ADMIN, StaffRole.CRM_OPERATOR, StaffRole.ADMIN
        )
        JobDetailSection.INSURANCE -> role in setOf(
            StaffRole.INSURANCE, StaffRole.MANAGER, StaffRole.OWNER, StaffRole.SYSTEM_ADMIN,
            StaffRole.CRM_OPERATOR, StaffRole.ADMIN
        )
        JobDetailSection.REPAIR_PROGRESS -> role in setOf(
            StaffRole.WORKSHOP, StaffRole.SUPERVISOR, StaffRole.MANAGER, StaffRole.OWNER,
            StaffRole.SYSTEM_ADMIN, StaffRole.CRM_OPERATOR, StaffRole.ADMIN
        )
        JobDetailSection.PARTS_TRACKING -> role in setOf(
            StaffRole.PARTS, StaffRole.SPARE_PARTS_TEAM, StaffRole.WORKSHOP, StaffRole.SUPERVISOR,
            StaffRole.MANAGER, StaffRole.OWNER, StaffRole.SYSTEM_ADMIN, StaffRole.CRM_OPERATOR, StaffRole.ADMIN
        )
        JobDetailSection.BILLING -> role.canAccessBilling()
        JobDetailSection.GATE_PASS -> role in setOf(
            StaffRole.GATE_ENTRY, StaffRole.SECURITY, StaffRole.MANAGER, StaffRole.OWNER,
            StaffRole.SYSTEM_ADMIN, StaffRole.CRM_OPERATOR, StaffRole.ADMIN
        )
    }
}

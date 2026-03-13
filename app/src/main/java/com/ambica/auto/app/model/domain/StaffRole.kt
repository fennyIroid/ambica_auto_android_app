package com.ambica.auto.app.model.domain

/**
 * System roles as provided by client.
 * Used for role-based section visibility and permission guards.
 */
enum class StaffRole(val label: String) {
    SECURITY("Security"),
    SUPERVISOR("Supervisor"),
    MANAGER("Manager / Service Advisor"),
    SPARE_PARTS_TEAM("Spare Parts Team"),
    ACCOUNTANT("Accountant"),
    OWNER("Owner"),
    SYSTEM_ADMIN("System Admin"),
    CUSTOMER("Customer"),
    CRM_OPERATOR("CRM Operator"),
    // Legacy aliases for backward compatibility
    GATE_ENTRY("Gate Entry"),
    ESTIMATOR("Estimator"),
    INSURANCE("Insurance Desk"),
    WORKSHOP("Workshop"),
    PARTS("Spare Parts"),
    BILLING("Billing"),
    ADMIN("Admin"),
    ;

    /** Roles that can see billing and payment verification (not CRM). */
    fun canAccessBilling(): Boolean = this in setOf(
        ACCOUNTANT, MANAGER, OWNER, SYSTEM_ADMIN, BILLING, ADMIN
    )

    /** Roles that can update stages (CRM, Supervisor, Manager, etc.). */
    fun canUpdateStages(): Boolean = this in setOf(
        CRM_OPERATOR, SUPERVISOR, MANAGER, SYSTEM_ADMIN, WORKSHOP, ADMIN
    )

    /** Roles that can verify payment / financial data. */
    fun canVerifyPayment(): Boolean = this in setOf(
        ACCOUNTANT, MANAGER, OWNER, SYSTEM_ADMIN, BILLING, ADMIN
    )

    /** Roles that can create and manage branches. */
    fun canManageBranches(): Boolean = this in setOf(
        OWNER, SYSTEM_ADMIN, ADMIN
    )
}

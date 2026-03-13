package com.ambica.auto.app.model.domain.job

/**
 * Validates status transitions. When status is On Hold, holdReason must be provided.
 */
object StatusTransition {

    /**
     * Returns error message if transition is invalid, null if valid.
     */
    fun validateTransition(
        current: JobStatus,
        next: JobStatus,
        holdReason: HoldReason?,
    ): String? {
        if (current == next) return null
        if (current.isTerminal()) return "Cannot change status from ${current.label}"
        if (!current.canTransitionTo(next)) {
            return "Invalid transition: ${current.label} → ${next.label}"
        }
        if (next == JobStatus.ON_HOLD && holdReason == null) {
            return "Hold reason is required when status is On Hold"
        }
        return null
    }
}

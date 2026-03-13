package com.ambica.auto.app.model.domain.job

/**
 * Note attached to a job (customer communication, internal).
 * For API readiness: id, jobId, note, createdBy, timestamp.
 */
data class JobNote(
    val id: String,
    val jobId: String,
    val note: String,
    val createdBy: String,
    val timestamp: Long,
)

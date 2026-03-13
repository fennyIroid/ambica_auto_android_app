package com.ambica.auto.app.model.domain.job

/**
 * Photo attached to a job (repair progress, documents).
 * For API readiness: id, jobId, imageUrl, uploadedBy, timestamp.
 */
data class JobPhoto(
    val id: String,
    val jobId: String,
    val imageUrl: String? = null,
    val uploadedBy: String,
    val timestamp: Long,
)

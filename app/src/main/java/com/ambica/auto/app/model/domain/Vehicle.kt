package com.ambica.auto.app.model.domain

/**
 * Vehicle entity for API readiness.
 * Job may reference vehicleId; details fetched via service layer.
 */
data class Vehicle(
    val id: String,
    val number: String,
    val brand: String? = null,
    val model: String? = null,
    val year: Int? = null,
)

package com.ambica.auto.app.model.domain

/**
 * Customer entity for API readiness.
 * Job may reference customerId; details fetched via service layer.
 */
data class Customer(
    val id: String,
    val name: String,
    val phone: String? = null,
    val address: String? = null,
)

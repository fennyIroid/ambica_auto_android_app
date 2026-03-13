package com.ambica.auto.app.data.source.remote.model.document_checklist

import com.google.gson.annotations.SerializedName

/**
 * Paginated document checklist response.
 * API: GET /document-checklists/
 */
data class DocumentChecklistListResponse(
    @SerializedName("count") val count: Int?,
    @SerializedName("results") val results: List<DocumentChecklistItemResponse>?,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
)

data class DocumentChecklistItemResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("document_category") val documentCategory: Int?,
    @SerializedName("applicable_for") val applicableFor: Int?,
    @SerializedName("is_mandatory") val isMandatory: Boolean?,
    @SerializedName("display_order") val displayOrder: Int?,
    @SerializedName("order") val order: Int?,
    @SerializedName("document_name") val documentName: String?,
) {
    val displayName: String get() = name ?: documentName ?: code.orEmpty()
}

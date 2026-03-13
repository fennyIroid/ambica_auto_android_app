package com.ambica.auto.app.model.domain.job

/**
 * Insurance document checklist items (from workshop checklist sheet).
 * Stored in UI state today; later can be sent to backend as part of insurance module payload.
 */
enum class InsuranceChecklistItem(val label: String, val isRequired: Boolean = false) {
    CLAIM_NO("Claim No.", isRequired = true),
    INVOICE_COPY("Invoice Copy"),
    RE_INSPECTION_PHOTO("Re-inspection Photo", isRequired = false),
    INTIMATION_SHEET("Intimation Sheet"),
    CLAIM_FORM("Claim Form"),
    INSURANCE_POLICY("Insurance Policy (End. Copy or Mail if req.)", isRequired = true),
    RC("R/C", isRequired = true),
    FITNESS_CERTIFICATE_CV("Fitness Certificate (For CV)"),
    ROUTE_PERMIT_CV("Route Permit (For CV)"),
    LOAD_CHALLAN_CV("Load Challan (For CV)"),
    DRIVING_LICENSE("Driving License"),
    REPAIR_ESTIMATE("Repair Estimate", isRequired = true),
    PHOTOS("Photos (4 per page; re-inspection photos if req.)", isRequired = true),
    SPOT_SURVEY_REPORT_CV("Spot Survey Report with photo (For CV)"),
    FIR_IF_REQUIRED("FIR (if required)"),
    INVESTIGATION_REPORT("Investigation Report"),
    PUNCH_NAMA("Punch Nama"),
    JANVA_JOB_ENTRY("Janva Job Entry"),
    KYC_AADHAR("KYC: Aadhar Card", isRequired = true),
    KYC_PAN("KYC: Pan Card"),
    KYC_VOTING("KYC: Voting Card"),
    GST_CERTIFICATE("GST Certificate"),
    ADVANCE_PAYMENT_DETAILS("Advance Payment Details"),
    SCREEN_REPORT("Screen Report"),
    PANCHNAMU("Panchnamu"),
    VEH_IN_DATE("Veh. in Date", isRequired = false),
    TIME("Time", isRequired = false),
    REMARKS("Remarks", isRequired = false),
    ;

    val needsUpload: Boolean
        get() = this !in setOf(CLAIM_NO, VEH_IN_DATE, TIME, REMARKS)

    companion object {
        fun defaultMap(): Map<InsuranceChecklistItem, Boolean> = entries.associateWith { false }
    }
}


package com.ambica.auto.app.ui.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ambica.auto.app.model.domain.job.JobStatus
import com.ambica.auto.app.ui.theme.StatusCompletedBg
import com.ambica.auto.app.ui.theme.StatusCompletedFg
import com.ambica.auto.app.ui.theme.StatusInProgressBg
import com.ambica.auto.app.ui.theme.StatusInProgressFg
import com.ambica.auto.app.ui.theme.StatusOnHoldBg
import com.ambica.auto.app.ui.theme.StatusOnHoldFg
import com.ambica.auto.app.ui.theme.StatusPendingBg
import com.ambica.auto.app.ui.theme.StatusPendingFg

/**
 * Maps JobStatus to the four status chip types (Green/Orange/Red/Blue).
 */
fun JobStatus.toStatusChipType(): StatusChipType = when (this) {
    JobStatus.READY_FOR_DELIVERY, JobStatus.DELIVERED -> StatusChipType.COMPLETED
    JobStatus.AWAITING_DOCUMENTS, JobStatus.AWAITING_SURVEY, JobStatus.AWAITING_INSURANCE_APPROVAL,
    JobStatus.AWAITING_PAYMENT -> StatusChipType.PENDING
    JobStatus.ON_HOLD, JobStatus.CANCELLED -> StatusChipType.ON_HOLD
    JobStatus.NEW_ENTRY, JobStatus.APPROVED_AND_WORK_IN_PROGRESS, JobStatus.VEHICLE_READY -> StatusChipType.IN_PROGRESS
}

/**
 * Status type for consistent chip colors across the app.
 * Green = Completed, Orange = Pending, Red = On Hold, Blue = In Progress.
 */
enum class StatusChipType(val backgroundColor: Color, val textColor: Color) {
    COMPLETED(StatusCompletedBg, StatusCompletedFg),
    PENDING(StatusPendingBg, StatusPendingFg),
    ON_HOLD(StatusOnHoldBg, StatusOnHoldFg),
    IN_PROGRESS(StatusInProgressBg, StatusInProgressFg),
}

@Composable
fun StatusChip(
    label: String,
    type: StatusChipType,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(type.backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = type.textColor,
        )
    }
}


package com.ambica.auto.app.ui.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ambica.auto.app.ui.theme.StatusCompletedFg
import com.ambica.auto.app.ui.theme.StatusInProgressFg

/**
 * Single item in a vertical timeline: icon circle + optional connector line.
 */
@Composable
fun TimelineItem(
    title: String,
    subtitle: String?,
    isCompleted: Boolean,
    isCurrent: Boolean,
    showConnector: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        when {
                            isCompleted -> StatusCompletedFg
                            isCurrent -> StatusInProgressFg
                            else -> Color(0xFFE0E0E0)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isCompleted) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                } else if (isCurrent) {
                    Box(
                        modifier = Modifier.size(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                        ) {}
                    }
                }
            }
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(
                            if (isCompleted) StatusCompletedFg else Color(0xFFE0E0E0),
                        ),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (showConnector) 0.dp else 4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}


package com.ambica.auto.app.ui.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ambica.auto.app.ui.theme.colorSplashOrange

/**
 * Neutral pill chip for filters (selected/unselected).
 */
@Composable
fun FilterPillChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val background = if (selected) {
        colorSplashOrange.copy(alpha = 0.12f)
    } else {
        Color(0xFFF2F2F2)
    }
    val foreground = if (selected) {
        colorSplashOrange
    } else {
        Color(0xFF7A7A7A)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = foreground,
        )
    }
}


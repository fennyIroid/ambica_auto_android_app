package com.ambica.auto.app.ux.main.bottombar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.ambica.auto.app.ui.theme.colorSplashOrange

@Composable
fun AmbicaBottomBar(
    currDestination: NavDestination?,
    onNavItemClicked: (NavBarItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        val containerShape = RoundedCornerShape(32.dp)

        Row(
            modifier = Modifier
                .shadow(
                    elevation = 14.dp,
                    shape = containerShape,
                    spotColor = Color(0x33000000),
                )
                .clip(containerShape)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavBarItem.entries.forEach { item ->
                val selected = currDestination.isSelected(item)
                val pillShape = RoundedCornerShape(999.dp)

                val pillPaddingHorizontal by animateDpAsState(
                    targetValue = if (selected) 22.dp else 16.dp,
                    animationSpec = tween(durationMillis = 200),
                    label = "nav_pill_padding",
                )

                val iconTint by animateColorAsState(
                    targetValue = if (selected) Color.White else Color(0xFF9A9A9A),
                    animationSpec = tween(220),
                    label = "nav_tint",
                )

                val pillBackground by animateColorAsState(
                    targetValue = if (selected) colorSplashOrange else Color.Transparent,
                    animationSpec = tween(220),
                    label = "nav_bg",
                )

                Row(
                    modifier = Modifier
                        .clip(pillShape)
                        .background(pillBackground)
                        .clickable { onNavItemClicked(item) }
                        .padding(horizontal = pillPaddingHorizontal, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                        tint = iconTint,
                    )

                    AnimatedVisibility(
                        visible = selected,
                        enter = fadeIn(tween(160)) + expandHorizontally(animationSpec = tween(160)),
                        exit = fadeOut(tween(140)),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun NavDestination?.isSelected(navBarItem: NavBarItem): Boolean {
    return this?.hierarchy?.any { it.route == navBarItem.route.value } == true
}


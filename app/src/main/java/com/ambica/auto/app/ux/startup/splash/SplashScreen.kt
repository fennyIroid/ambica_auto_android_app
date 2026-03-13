package com.ambica.auto.app.ux.startup.splash

import android.os.Bundle
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.R
import com.ambica.auto.app.navigation.HandleNavigation
import com.ambica.auto.app.ui.theme.colorSplashAppText
import com.ambica.auto.app.ui.theme.colorSplashNavy
import com.ambica.auto.app.ui.theme.colorSplashOrange
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
    bundle: Bundle? = null,
) {
    val uiState = viewModel.splashUiState
    val uiData by uiState.splashStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(bundle) {
        bundle?.let { uiState.event(SplashUiEvent.GetIntentData(it)) }
    }

    SplashScreenContent(
        uiData = uiData,
        event = uiState.event,
    )

    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun SplashScreenContent(
    uiData: SplashData?,
    event: (SplashUiEvent) -> Unit,
) {
    val context = LocalContext.current
    var startAnimations by remember { mutableStateOf(false) }

    // Professional entrance animation: Slide up + Fade
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "logo_alpha"
    )
    val logoOffset by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else 20.dp,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "logo_offset"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "text_alpha"
    )
    val textOffset by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else 16.dp,
        animationSpec = tween(1000, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "text_offset"
    )

    // Pulse animation for the living glow feel
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )
    val glowAlphaPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimations = true
        delay(3000)
        event(SplashUiEvent.NavigateAfterSplash(context))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Box to perfectly center the glow on the logo
            Box(contentAlignment = Alignment.Center) {
                // Subtle Even Glow
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .graphicsLayer {
                            translationY = logoOffset.toPx()
                            alpha = logoAlpha * glowAlphaPulse
                            scaleX = glowScale
                            scaleY = glowScale
                        }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    colorSplashOrange.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ambica_app_icon_removebg),
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .graphicsLayer {
                            translationY = logoOffset.toPx()
                            alpha = logoAlpha
                        },
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.padding(20.dp))
            
            // App Name Group below logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    translationY = textOffset.toPx()
                    alpha = textAlpha
                }
            ) {
                Text(
                    text = "Ambica Auto",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorSplashNavy,
                        letterSpacing = (-1).sp
                    )
                )

                Text(
                    text = "App",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Light,
                        color = colorSplashAppText,
                        letterSpacing = 2.sp
                    )
                )
            }
        }
        
        // Version / Build Identifier at the bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(textAlpha * 0.4f)
        ) {
            Text(
                text = "Version 1.0.0.1",
                style = MaterialTheme.typography.labelSmall,
                color = colorSplashNavy.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSplash() {
    Surface {
        SplashScreenContent(uiData = SplashData(), event = {})
    }
}

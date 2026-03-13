package com.ambica.auto.app.ux.container.job.modules.timeline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ambica.auto.app.ux.container.job.modules.JobModuleScaffold

@Composable
fun TimelineScreen(
    navController: NavController,
    jobId: String,
    viewModel: TimelineViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val state by uiState.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        uiState.event(TimelineUiEvent.OnSetJobId(jobId))
    }

    JobModuleScaffold(title = "Job Timeline", navController = navController) {
        state.job?.timeline
            ?.sortedByDescending { it.atMillis }
            ?.forEach { ev ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(3.dp),
                    modifier = Modifier.padding(bottom = 10.dp),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            ev.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        ev.description?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7A7A7A),
                            )
                        }
                    }
                }
            }
    }
}


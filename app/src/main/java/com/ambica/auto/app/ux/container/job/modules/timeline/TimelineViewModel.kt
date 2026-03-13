package com.ambica.auto.app.ux.container.job.modules.timeline

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getTimelineUiStateUseCase: GetTimelineUiStateUseCase,
) : BaseViewModel() {
    val uiState: TimelineUiState =
        getTimelineUiStateUseCase(context = context, coroutineScope = viewModelScope)
}


package com.ambica.auto.app.ux.container.job.hub

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class JobDetailsHubViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getJobDetailsHubUiStateUseCase: GetJobDetailsHubUiStateUseCase,
) : BaseViewModel() {
    val uiState: JobDetailsHubUiState =
        getJobDetailsHubUiStateUseCase(context = context, coroutineScope = viewModelScope)
}


package com.ambica.auto.app.ux.main.jobs

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import com.ambica.auto.app.navigation.ViewModelNav
import com.ambica.auto.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getJobsUiStateUseCase: GetJobsUiStateUseCase,
) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: JobsUiState =
        getJobsUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}

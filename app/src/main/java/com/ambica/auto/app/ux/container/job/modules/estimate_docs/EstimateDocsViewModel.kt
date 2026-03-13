package com.ambica.auto.app.ux.container.job.modules.estimate_docs

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class EstimateDocsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getEstimateDocsUiStateUseCase: GetEstimateDocsUiStateUseCase,
) : BaseViewModel() {
    val uiState: EstimateDocsUiState =
        getEstimateDocsUiStateUseCase(context = context, coroutineScope = viewModelScope)
}


package com.ambica.auto.app.ux.container.job.modules.insurance_survey

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class InsuranceSurveyViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getInsuranceSurveyUiStateUseCase: GetInsuranceSurveyUiStateUseCase,
) : BaseViewModel() {
    val uiState: InsuranceSurveyUiState =
        getInsuranceSurveyUiStateUseCase(context = context, coroutineScope = viewModelScope)
}


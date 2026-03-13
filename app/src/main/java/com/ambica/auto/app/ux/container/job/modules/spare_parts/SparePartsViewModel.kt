package com.ambica.auto.app.ux.container.job.modules.spare_parts

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ambica.auto.app.model.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SparePartsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getSparePartsUiStateUseCase: GetSparePartsUiStateUseCase,
) : BaseViewModel() {
    val uiState: SparePartsUiState =
        getSparePartsUiStateUseCase(context = context, coroutineScope = viewModelScope)
}


package com.ambica.auto.app.ui.compose.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity

/**
 * Stabilizes [ModalBottomSheet] when the user performs a very fast upward drag/fling.
 *
 * With [skipPartiallyExpanded = true], an aggressive upward fling can put the sheet into an
 * unstable loop: the sheet keeps trying to settle to Expanded while residual upward velocity is
 * fed back through the nested scroll chain. This modifier consumes that leftover upward velocity
 * when the sheet is already Expanded, breaking the loop while leaving downward velocity intact so
 * drag-to-dismiss still works.
 *
 * Apply to the **root content Column/Box** that is the direct child of [ModalBottomSheet].
 */
@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.bottomSheetFlingStabilizer(sheetState: SheetState): Modifier =
    this.nestedScroll(BottomSheetFlingStabilizerConnection(sheetState))

@OptIn(ExperimentalMaterial3Api::class)
private class BottomSheetFlingStabilizerConnection(
    private val sheetState: SheetState,
) : NestedScrollConnection {

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = Offset.Zero

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (sheetState.currentValue == SheetValue.Expanded && available.y < 0) return available
        return Velocity.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (sheetState.currentValue == SheetValue.Expanded && available.y < 0) return Velocity.Zero
        return available
    }
}

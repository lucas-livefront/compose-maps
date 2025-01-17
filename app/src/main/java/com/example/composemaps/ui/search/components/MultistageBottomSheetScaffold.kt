package com.example.composemaps.ui.search.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MultistageBottomSheetScaffold(
    bottomSheetDraggedToNewStateCallback: (MultistageBottomSheetState) -> Unit,
    bottomSheetHeader: @Composable () -> Unit,
    bottomSheetHeaderHeightDp: Dp,
    bottomSheetScrollableContent: LazyListScope.() -> Unit,
    bottomSheetState: MultistageBottomSheetState,
    header: @Composable (Float) -> Unit,
    headerCollapseDeltaDp: Dp,
    fab: @Composable () -> Unit,
    fabPosition: FabPosition,
    content: @Composable () -> Unit,
) {
    Scaffold(
        floatingActionButton = fab,
        floatingActionButtonPosition = fabPosition,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val targetHeaderDelta = headerCollapseDeltaDp * bottomSheetState.expansionPercentage
        val mutableHeaderExpansionPercentage = remember { mutableStateOf(targetHeaderDelta / headerCollapseDeltaDp) }

        Column(
            modifier = Modifier.padding(paddingValues),
        ) {

            Column(
                modifier = Modifier.zIndex(4f),
            ) {
                header(mutableHeaderExpansionPercentage.value)
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .alpha(0f)
                        .height(headerCollapseDeltaDp * (1f - mutableHeaderExpansionPercentage.value))
                )
            }

            BoxWithConstraints(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize(),
            ) {
                val currentMaxHeightDp = this@BoxWithConstraints.maxHeight
                val maxHeightWithFullHeader = remember {
                    currentMaxHeightDp + headerCollapseDeltaDp - targetHeaderDelta
                }
                content()
                MultistageBottomSheet(
                    bottomSheetHeader = bottomSheetHeader,
                    bottomSheetHeaderHeightDp = bottomSheetHeaderHeightDp,
                    state = bottomSheetState,
                    dragToNewStateCallback = bottomSheetDraggedToNewStateCallback,
                    bodyHeightDp = maxHeightWithFullHeader,
                    headerCollapseDeltaPx = headerCollapseDeltaDp,
                    scrollableBody = bottomSheetScrollableContent,
                ) { mutableHeaderExpansionPercentage.value = it }
            }
        }
    }
}
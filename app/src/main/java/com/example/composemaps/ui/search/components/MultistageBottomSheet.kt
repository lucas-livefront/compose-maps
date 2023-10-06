package com.example.composemaps.ui.search.components

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

/**
 * Todo:
 * * bug: If you catch the sheet while it is animating the bottom sheet catches the click and you cannot scroll the list
 * * bug: configuration change fab is absolutely fucked
 * * bug: fling from fuller to full sometimes leaves the list in a weird state
 * * task: Remove bottomSheetHeaderDp from MultistageBottomSheet parameters?
 */


sealed class MultistageBottomSheetState : Parcelable {
    abstract val expansionPercentage: Float
    abstract val percentOffset: Float

    /**
     * Get the additional additive offset that is required for the given [MultistageBottomSheetState].
     *
     *  The [LazyColumn] is the size of the parent composable. Therefore the full bottom sheet must be larger than the
     *  parent. The additional height comes from the draggable bar which is of size [bottomSheetBarPx] and the
     *  collapsible header delta which is of size [headerCollapseDeltaPx].
     *
     *  It is important to know that this height overflow is split between the bottom and top of the parent. Therefore,
     *  the [Gone] state must be offset by the size of the entire bottom sheet minus half of any other height bearing
     *  components (in this case the collapsible header and bottom sheet bar).
     */
    abstract fun calculateAdditionalOffset(
        bottomSheetBarPx: Float,
        headerCollapseDeltaPx: Float,
    ): Float

    fun toAnchor(
        maxHeightPx: Float,
        bottomSheetBarPx: Float,
        headerCollapseDeltaPx: Float,
    ): Pair<Float, MultistageBottomSheetState> = this.getOffset(
        bottomSheetBarPx = bottomSheetBarPx,
        headerCollapseDeltaPx = headerCollapseDeltaPx,
        maxHeightPx = maxHeightPx,
    ) to this

    /**
     * Get the offset for the anchor of the given state.
     */
    fun getOffset(
        bottomSheetBarPx: Float,
        headerCollapseDeltaPx: Float,
        maxHeightPx: Float,
    ): Float = maxHeightPx * percentOffset + this.calculateAdditionalOffset(
        bottomSheetBarPx = bottomSheetBarPx,
        headerCollapseDeltaPx = headerCollapseDeltaPx,
    )

    @Parcelize
    data object Fuller : MultistageBottomSheetState() {
        override val expansionPercentage: Float get() = 0f
        override val percentOffset: Float get() = 0f
        override fun calculateAdditionalOffset(
            bottomSheetBarPx: Float,
            headerCollapseDeltaPx: Float,
        ): Float = -bottomSheetBarPx / 2f - headerCollapseDeltaPx / 2f
    }

    @Parcelize
    data object Full : MultistageBottomSheetState() {
        override val expansionPercentage: Float get() = 1f
        override val percentOffset: Float get() = 0f
        override fun calculateAdditionalOffset(
            bottomSheetBarPx: Float,
            headerCollapseDeltaPx: Float,
        ): Float = -bottomSheetBarPx / 2f + headerCollapseDeltaPx / 2f
    }

    @Parcelize
    data object Gone : MultistageBottomSheetState() {
        override val expansionPercentage: Float get() = 1f
        override val percentOffset: Float get() = 1f
        override fun calculateAdditionalOffset(
            bottomSheetBarPx: Float,
            headerCollapseDeltaPx: Float,
        ): Float = -bottomSheetBarPx / 2f - headerCollapseDeltaPx / 2f
    }

    @Parcelize
    data object Half : MultistageBottomSheetState() {
        override val expansionPercentage: Float get() = 1f
        override val percentOffset: Float get() = .5f
        override fun calculateAdditionalOffset(
            bottomSheetBarPx: Float,
            headerCollapseDeltaPx: Float,
        ) = 0f
    }

    companion object {
        fun getAnchors(
            bottomSheetBarPx: Float,
            headerCollapseDeltaPx: Float,
            maxHeightPx: Float,
        ): Map<Float, MultistageBottomSheetState> = mapOf(
            Gone.toAnchor(
                bottomSheetBarPx = bottomSheetBarPx,
                headerCollapseDeltaPx = headerCollapseDeltaPx,
                maxHeightPx = maxHeightPx,
            ),
            Half.toAnchor(
                bottomSheetBarPx = bottomSheetBarPx,
                headerCollapseDeltaPx = headerCollapseDeltaPx,
                maxHeightPx = maxHeightPx,
            ),
            Full.toAnchor(
                bottomSheetBarPx = bottomSheetBarPx,
                headerCollapseDeltaPx = headerCollapseDeltaPx,
                maxHeightPx = maxHeightPx,
            ),
            Fuller.toAnchor(
                bottomSheetBarPx = bottomSheetBarPx,
                headerCollapseDeltaPx = headerCollapseDeltaPx,
                maxHeightPx = maxHeightPx,
            ),
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun MultistageBottomSheet(
    bottomSheetHeader: @Composable () -> Unit,
    bottomSheetHeaderHeightDp: Dp,
    dragToNewStateCallback: (MultistageBottomSheetState) -> Unit,
    scrollableBody: LazyListScope.() -> Unit,
    state: MultistageBottomSheetState,
    headerCollapseDeltaPx: Dp,
    bodyHeightDp: Dp,
    onChangeExpansionPercentage: (Float) -> Unit,
) {
    // Setup remembered state
    val swipeableState = rememberSwipeableState(initialValue = state)
    val lazyListState = rememberLazyListState()
    val bottomListInteractionSource = remember { MutableInteractionSource() }
    val nestedScrollConnection = remember {
        BottomSheetNestedScrollConnection(
            lazyListState = lazyListState,
            swipeableState = swipeableState,
        )
    }

    // Setup required values
    val bottomSheetMaxHeightDp = bodyHeightDp + bottomSheetHeaderHeightDp + headerCollapseDeltaPx
    val fullOffset = MultistageBottomSheetState.Full.getOffset(
        bottomSheetBarPx = bottomSheetHeaderHeightDp.toPx(),
        headerCollapseDeltaPx = headerCollapseDeltaPx.toPx(),
        maxHeightPx = bottomSheetMaxHeightDp.toPx(),
    )

    // Setup animation on state change
    LaunchedEffect(key1 = state) {
        swipeableState.animateTo(state)
    }

    // Setup callbacks
    OverscrollCallback(
        headerCollapseDeltaPx = headerCollapseDeltaPx,
        fullOffset = fullOffset,
        swipeableState = swipeableState,
    ) { overscrollDp ->
        onChangeExpansionPercentage(overscrollDp)
    }
    TargetStateChangeCallback(
        interactionSource = bottomListInteractionSource,
        lazyListInteractionSource = lazyListState.interactionSource,
        swipeableState = swipeableState,
    ) { bottomSheetState ->
        dragToNewStateCallback(bottomSheetState)
    }


    Column(
        modifier = Modifier
            .requiredHeight(bottomSheetMaxHeightDp)
            .offset { swipeableState.offset.value.toIntOffset() }
            .swipeable(
                interactionSource = bottomListInteractionSource,
                resistance = ResistanceConfig,
                thresholds = { _, _ -> FractionalThreshold(fraction = 0.5f) },
                state = swipeableState,
                orientation = Orientation.Vertical,
                anchors = MultistageBottomSheetState.getAnchors(
                    bottomSheetBarPx = bottomSheetHeaderHeightDp.toPx(),
                    headerCollapseDeltaPx = headerCollapseDeltaPx.toPx(),
                    maxHeightPx = bottomSheetMaxHeightDp.toPx(),
                ),
            )
            .nestedScroll(nestedScrollConnection)
    ) {
        bottomSheetHeader()
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .background(color = Color.White)
                .requiredHeight(bodyHeightDp + headerCollapseDeltaPx)
        ) {
            scrollableBody.invoke(this)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private class BottomSheetNestedScrollConnection(
    val lazyListState: LazyListState,
    val swipeableState: SwipeableState<MultistageBottomSheetState>,
) : NestedScrollConnection {

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset = if (available.y < 0) {
        swipeableState.performDrag(available.y).toOffset()
    } else {
        Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = swipeableState.performDrag(available.y).toOffset()

    override suspend fun onPreFling(available: Velocity): Velocity = if (
        available.y < 0 && lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
    ) {
        swipeableState.performFling(velocity = available.y)
        available
    } else {
        Velocity.Zero
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        swipeableState.performFling(velocity = available.y)
        return super.onPostFling(consumed, available)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TargetStateChangeCallback(
    interactionSource: InteractionSource,
    lazyListInteractionSource: InteractionSource,
    swipeableState: SwipeableState<MultistageBottomSheetState>,
    callback: (MultistageBottomSheetState) -> Unit,
) {
    val isBottomSheetDragged by interactionSource.collectIsDraggedAsState()
    val isLazyListDragged by lazyListInteractionSource.collectIsDraggedAsState()

    if (isBottomSheetDragged.not() && isLazyListDragged.not()) {
        callback(swipeableState.targetValue)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OverscrollCallback(
    headerCollapseDeltaPx: Dp,
    fullOffset: Float,
    swipeableState: SwipeableState<MultistageBottomSheetState>,
    onOverscroll: (Float) -> Unit,
) {
    val density = LocalDensity.current

    LaunchedEffect(key1 = swipeableState, key2 = fullOffset) {
        snapshotFlow { swipeableState.offset.value }.collect {
            if (swipeableState.offset.value < fullOffset) {
                val expansionDp = headerCollapseDeltaPx - (fullOffset - swipeableState.offset.value).toDp(density)
                onOverscroll(expansionDp / headerCollapseDeltaPx)
            } else {
                onOverscroll(1f)
            }
        }
    }
}

private val ResistanceConfig = ResistanceConfig(
    basis = SwipeableDefaults.StiffResistanceFactor,
    factorAtMin = SwipeableDefaults.StiffResistanceFactor,
    factorAtMax = SwipeableDefaults.StiffResistanceFactor,
)

private fun Float.toOffset() = Offset(
    x = 0f,
    y = this,
)

private fun Float.toIntOffset() = IntOffset(
    x = 0,
    y = this.roundToInt(),
)

@Composable
fun Float.toDp() = with(LocalDensity.current) {
    this@toDp.toDp()
}

@Composable
fun Dp.toPx() = with(LocalDensity.current) {
    this@toPx.toPx()
}

@Composable
fun TextUnit.toDp() = with(LocalDensity.current) {
    this@toDp.toDp()
}

fun Float.toDp(density: Density) = with(density) { this@toDp.toDp() }

operator fun TextUnit.minus(textUnit: TextUnit) = (this.value - textUnit.value).sp
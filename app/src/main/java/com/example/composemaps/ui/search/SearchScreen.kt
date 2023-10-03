package com.example.composemaps.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.FabPosition
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composemaps.R
import com.example.composemaps.ui.search.components.MultistageBottomSheetScaffold
import com.example.composemaps.ui.search.components.MultistageBottomSheetState
import com.example.composemaps.ui.search.components.SearchFab
import com.example.composemaps.ui.search.components.SearchFabState
import com.example.composemaps.ui.search.components.SearchListContent
import com.example.composemaps.ui.search.components.SearchListData
import com.example.composemaps.ui.search.components.SearchMap
import com.example.composemaps.ui.search.components.SearchMapContent
import com.example.composemaps.ui.search.components.SearchMapData
import com.example.composemaps.ui.search.components.minus
import com.example.composemaps.ui.search.components.searchListItems
import com.example.composemaps.ui.search.components.toDp

private val BottomSheetBarHeightDp = 40.dp

private val HeaderCollapsedFontSizeSp = 17.sp
private val HeaderExpandedFontSizeSp = 28.sp

private val HeaderCollapsedLineHeightSp = 24.sp
private val HeaderExpandedLineHeightSp = 36.sp
private val HeaderLineHeightDeltaSp = HeaderExpandedLineHeightSp - HeaderCollapsedLineHeightSp

private val SubheaderCollapsedBottomPaddingDp = 8.dp
private val SubheaderExpandedBottomPaddingDp = 16.dp
private val SubheaderBottomPaddingDelta = SubheaderExpandedBottomPaddingDp - SubheaderCollapsedBottomPaddingDp

private val SubheaderCollapsedLineHeightSp = 0.sp
private val SubheaderExpandedLineHeightSp = 19.sp
private val SubheaderLineHeightDeltaSp = SubheaderExpandedLineHeightSp - SubheaderCollapsedLineHeightSp

@Composable
fun SearchScreen(
    onToKeyboardClicked: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val searchScreenContent by viewModel.searchScreenContent.collectAsState(initial = SearchScreenContent.DEFAULT)
    val totalHeaderDeltaDp =
        SubheaderBottomPaddingDelta + SubheaderLineHeightDeltaSp.toDp() + HeaderLineHeightDeltaSp.toDp()

    MultistageBottomSheetScaffold(
        bottomSheetDraggedToNewStateCallback = viewModel::onDragToNewState,
        bottomSheetHeader = { BottomSheetHeader(modifier = Modifier.height(BottomSheetBarHeightDp)) },
        bottomSheetHeaderHeightDp = BottomSheetBarHeightDp,
        bottomSheetScrollableContent = {
            searchListItems(
                searchListContent = SearchListContent(
                    data = searchScreenContent.listData,
                    bottomSheetState = searchScreenContent.searchScreenState.bottomSheetState,
                )
            )
        },
        bottomSheetState = searchScreenContent.searchScreenState.bottomSheetState,
        header = {
            SearchHeader(
                expansionPercentage = it,
                onToKeyboardClicked = onToKeyboardClicked,
            )
        },
        headerCollapseDeltaDp = totalHeaderDeltaDp,
        fab = {
            SearchFab(
                searchFabState = searchScreenContent.searchScreenState.searchFabState,
                onClick = viewModel::fabClicked
            )
        },
        fabPosition = FabPosition.End,
    ) {
        SearchMap(
            searchMapContent = SearchMapContent(
                data = searchScreenContent.mapData,
            )
        )
    }
}

@Composable
fun BottomSheetHeader(
    modifier: Modifier,
) {
    val roundedCornerShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.LightGray,
                shape = roundedCornerShape
            )
            .shadow(
                elevation = 32.dp,
                shape = roundedCornerShape,
            ),
    ) {
        Spacer(
            modifier = Modifier
                .size(
                    width = 60.dp,
                    height = 6.dp
                )
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(100.dp)
                )
        )
    }
}

@Composable
fun SearchHeader(
    onToKeyboardClicked: () -> Unit,
    expansionPercentage: Float,
) {
    val actualSubheaderBottomPadding = lerp(
        start = SubheaderCollapsedBottomPaddingDp,
        stop = SubheaderExpandedBottomPaddingDp,
        fraction = expansionPercentage,
    )
    val actualSubheaderLineHeight = lerp(
        start = SubheaderCollapsedLineHeightSp,
        stop = SubheaderExpandedLineHeightSp,
        fraction = expansionPercentage,
    )
    val actualHeaderLineHeight = lerp(
        start = HeaderCollapsedLineHeightSp,
        stop = HeaderExpandedLineHeightSp,
        fraction = expansionPercentage,
    )
    val actualHeaderFontSize = lerp(
        start = HeaderCollapsedFontSizeSp,
        stop = HeaderExpandedFontSizeSp,
        fraction = expansionPercentage,
    )
    val actualHeaderBackgroundColor = lerp(
        start = Color.Gray,
        stop = Color.LightGray,
        fraction = expansionPercentage,
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color = actualHeaderBackgroundColor)
            .fillMaxWidth()
            .padding(
                end = 24.dp,
                start = 24.dp,
                top = 16.dp,
            )
            .wrapContentHeight(),
    ) {
        Text(
            text = "Compose Map",
            fontSize = actualHeaderFontSize,
            lineHeight = actualHeaderLineHeight,
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onToKeyboardClicked,
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = "To keyboard")
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color = actualHeaderBackgroundColor)
            .fillMaxWidth()
            .padding(
                bottom = actualSubheaderBottomPadding,
                end = 24.dp,
                start = 24.dp,
                top = 8.dp,
            )
            .height(actualSubheaderLineHeight.toDp()),
    ) {
        Text(
            text = "Using google maps.",
            fontSize = 13.sp,
        )
    }
}

/**
 * A total state object for the search screen.
 */
data class SearchScreenContent(
    val listData: SearchListData,
    val mapData: SearchMapData,
    val searchScreenState: SearchScreenState,
) {
    companion object {
        val DEFAULT = SearchScreenContent(
            listData = SearchListData.Loading,
            mapData = SearchMapData.Loading,
            searchScreenState = SearchScreenState.FullMap
        )
    }
}

/**
 * A sealed class to represent search screen UI states.
 */
sealed class SearchScreenState {

    abstract val bottomSheetState: MultistageBottomSheetState

    abstract val searchFabState: SearchFabState

    object FullMap : SearchScreenState() {
        override val bottomSheetState: MultistageBottomSheetState get() = MultistageBottomSheetState.Gone
        override val searchFabState: SearchFabState
            get() = SearchFabState(
                iconRes = R.drawable.ic_list,
                isVisible = true,
                textRes = R.string.search_fab_list,
            )
    }

    object FullerList : SearchScreenState() {
        override val bottomSheetState: MultistageBottomSheetState get() = MultistageBottomSheetState.Fuller
        override val searchFabState: SearchFabState
            get() = SearchFabState(
                iconRes = R.drawable.ic_map,
                isVisible = true,
                textRes = R.string.search_fab_map,
            )
    }

    object FullList : SearchScreenState() {
        override val bottomSheetState: MultistageBottomSheetState get() = MultistageBottomSheetState.Full
        override val searchFabState: SearchFabState
            get() = SearchFabState(
                iconRes = R.drawable.ic_map,
                isVisible = true,
                textRes = R.string.search_fab_map,
            )
    }

    data class HalfMap(
        val iconRes: Int,
        val textRes: Int,
    ) : SearchScreenState() {
        override val bottomSheetState: MultistageBottomSheetState get() = MultistageBottomSheetState.Half
        override val searchFabState: SearchFabState = SearchFabState(
            iconRes = iconRes,
            isVisible = false,
            textRes = textRes,
        )
    }
}
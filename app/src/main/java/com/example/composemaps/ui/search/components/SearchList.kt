package com.example.composemaps.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun LazyListScope.searchListItems(
    searchListContent: SearchListContent,
) {
    when (searchListContent.data) {
        is SearchListData.Loaded -> {
            itemsIndexed(searchListContent.data.listRowData) { index, item ->
                SearchListRow(
                    displayDivider = index == searchListContent.data.listRowData.size,
                    searchListRowData = item,
                )
            }
        }
        else -> {
            item {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun SearchListRow(
    displayDivider: Boolean,
    searchListRowData: SearchListRowData,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .clickable { },
    ) {
        Text(
            text = searchListRowData.title,
            color = Color.Black,
            modifier = Modifier
                .background(color = searchListRowData.backgroundColor)
                .padding(vertical = 16.dp),
        )
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth(.9f)
        ).takeIf { displayDivider }
    }
}

data class SearchListContent(
    val data: SearchListData,
    val bottomSheetState: MultistageBottomSheetState,
)

sealed class SearchListData {
    data object Error : SearchListData()

    data class Loaded(
        val listRowData: List<SearchListRowData>,
    ) : SearchListData()

    data object Loading : SearchListData()
}

data class SearchListRowData(
    val backgroundColor: Color,
    val isHighlighted: Boolean,
    val title: String,
)
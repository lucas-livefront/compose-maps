package com.example.composemaps.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composemaps.ui.navigation.Destinations
import com.example.composemaps.ui.search.SearchScreen

@Composable
fun ScreenDispatcher() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.Search.key) {
        composable(route = Destinations.Search.key) {
            SearchScreen()
        }
    }
}

package com.example.composemaps.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composemaps.ui.keyboard.KeyboardScreen
import com.example.composemaps.ui.search.SearchScreen

@Composable
fun ScreenDispatcher() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.Keyboard.key) {
        composable(route = Destinations.Search.key) {
            SearchScreen(
                onToKeyboardClicked = { navController.navigate(Destinations.Keyboard.key) },
            )
        }
        composable(route = Destinations.Keyboard.key) {
            KeyboardScreen(
                onToMapClicked = { navController.navigate(Destinations.Search.key) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

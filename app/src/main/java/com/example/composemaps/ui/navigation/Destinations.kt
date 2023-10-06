package com.example.composemaps.ui.navigation

sealed class Destinations {
    abstract val key: String

    data object Keyboard: Destinations() {
        override val key: String
            get() = "keyboard"
    }

    data object Search: Destinations() {
        override val key: String
            get() = "search"
    }
}
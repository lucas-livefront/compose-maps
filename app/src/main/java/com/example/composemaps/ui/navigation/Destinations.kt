package com.example.composemaps.ui.navigation

sealed class Destinations {
    abstract val key: String

    object Search: Destinations() {
        override val key: String
            get() = "search"
    }
}
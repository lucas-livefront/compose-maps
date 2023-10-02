package com.example.composemaps.ui.keyboard

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@Composable
fun KeyboardScreen(
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var value by rememberSaveable { mutableStateOf("") }
    var keyboardShouldBeFocused by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(keyboardShouldBeFocused) {
        if (keyboardShouldBeFocused) {
            Log.d("DAVID", "Request focus")
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
            Log.d("DAVID",  "remove focus")
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    // If a user taps the text field, focus is gained. This
                    // helps us set `keyboardShouldBeFocused` in that case.
                    if (keyboardShouldBeFocused.not()) {
                        keyboardShouldBeFocused = it.isFocused
                    }
                }
        )

        Button(
            onClick =  { keyboardShouldBeFocused = keyboardShouldBeFocused.not() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Toggle the keyboard")
        }
    }
}
package com.example.composemaps.ui.keyboard

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun KeyboardScreen(
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var value by rememberSaveable { mutableStateOf("") }
    var keyboardShouldBeOpen by rememberSaveable { mutableStateOf(false) }
    var textFieldIsFocused by rememberSaveable { mutableStateOf(false) }
    val textFieldFocusRequester = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(keyboardShouldBeOpen) {
        when {
            // Keyboard should be opened but the text field is already in focus.
            keyboardShouldBeOpen && textFieldIsFocused -> softwareKeyboardController?.show()
            // Keyboard should be opened and the text field is not focused.
            keyboardShouldBeOpen -> focusRequester.requestFocus()
            // Keyboard should be closed and the text field is focused.
            textFieldIsFocused -> textFieldFocusRequester.clearFocus()
            // Keyboard should be closed and the text field is focused.
            else -> softwareKeyboardController?.hide()
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
                    if (keyboardShouldBeOpen.not()) {
                        keyboardShouldBeOpen = it.isFocused
                    }
                    textFieldIsFocused = it.isFocused
                }
        )

        Button(
            onClick =  { keyboardShouldBeOpen = keyboardShouldBeOpen.not() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Toggle the keyboard")
        }
    }
}
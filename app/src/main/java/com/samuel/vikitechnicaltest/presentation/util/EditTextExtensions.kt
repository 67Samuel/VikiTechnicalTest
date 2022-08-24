package com.samuel.vikitechnicaltest.presentation.util

import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.onSubmit(func: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            func()
        }
        true
    }
}
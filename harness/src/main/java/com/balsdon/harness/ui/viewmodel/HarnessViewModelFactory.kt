package com.balsdon.harness.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HarnessViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HarnessViewModel::class.java)) {
            return HarnessViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
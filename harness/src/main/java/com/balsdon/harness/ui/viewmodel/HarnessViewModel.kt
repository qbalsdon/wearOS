package com.balsdon.harness.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.balsdon.harness.ui.view.WatchFaceMode

class HarnessViewModel : ViewModel() {
    companion object {
        private const val DEFAULT_SIZE = 640
        private const val DEFAULT_SPEED = 1
    }

    data class WatchDisplaySettings(
        var isAmbientMode: Boolean = false,
        var isMuteModeToggle: Boolean = false,
        var isLowBitAmbientToggle: Boolean = false,
        var isBurnInProtectionToggle: Boolean = false,
        var isAnimateTimeToggle: Boolean = false,
        var isTwentyFourHourMode: Boolean = false,
        var showComplications: Boolean = false,
        var faceMode: WatchFaceMode = WatchFaceMode.Round,
        var size: Int = DEFAULT_SIZE,
        var speed: Int = DEFAULT_SPEED
    )

    private val settingsData: MutableLiveData<WatchDisplaySettings> = MutableLiveData<WatchDisplaySettings>()
    val data: LiveData<WatchDisplaySettings> = settingsData

    private val currentSettings
    get() = settingsData.value ?: WatchDisplaySettings()

    private val currentTime: MutableLiveData<Long> = MutableLiveData<Long>()
    val watchTime: LiveData<Long> = currentTime
    var time: Long = System.currentTimeMillis()
        set(value) {
            if (field == value) return
            field = value
            currentTime.value = field
        }


    var isAmbientMode: Boolean = currentSettings.isAmbientMode
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(isAmbientMode = value)
    }

    var isMuteModeToggle: Boolean = currentSettings.isMuteModeToggle
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(isMuteModeToggle = value)
    }

    var isLowBitAmbientToggle: Boolean = currentSettings.isLowBitAmbientToggle
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(isLowBitAmbientToggle = value)
    }

    var isBurnInProtectionToggle: Boolean = currentSettings.isBurnInProtectionToggle
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(isBurnInProtectionToggle = value)
    }

    var isAnimateTimeToggle: Boolean = currentSettings.isAnimateTimeToggle
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(isAnimateTimeToggle = value)
    }

    var isTwentyFourHourMode: Boolean = currentSettings.isTwentyFourHourMode
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(isTwentyFourHourMode = value)
    }

    var showComplicationsToggle: Boolean = currentSettings.showComplications
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(showComplications = value)
    }

    var faceMode: WatchFaceMode = currentSettings.faceMode
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(faceMode = value)
    }

    var size: Int = currentSettings.size
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(size = value)
    }

    var speed: Int = currentSettings.speed
    set(value) {
        if (field == value) return
        field = value
        settingsData.value = currentSettings.copy(speed = value)
    }
}
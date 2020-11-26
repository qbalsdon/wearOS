package com.balsdon.harness.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.balsdon.harness.R
import com.balsdon.harness.ui.viewmodel.HarnessViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_watch.*

@AndroidEntryPoint
class WatchFragment : HarnessFragment() {

    companion object {
        fun newInstance() = WatchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_watch, container, false)
    }

    override fun settingsUpdated(settings: HarnessViewModel.WatchDisplaySettings) {
        watchFace.screenSettings.apply {
            isAmbientMode = settings.isAmbientMode
            isMuteMode = settings.isAmbientMode
            isLowBitAmbient = settings.isMuteModeToggle
            isBurnInProtection = settings.isLowBitAmbientToggle
            isTwentyFourHour = settings.isTwentyFourHourMode
        }
        watchFace.apply {
            faceMode = settings.faceMode
            size = settings.size
        }
    }

    override fun timeUpdated(time: Long) {
        watchFace.currentTime = time
    }
}
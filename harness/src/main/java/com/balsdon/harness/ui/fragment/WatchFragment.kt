package com.balsdon.harness.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.balsdon.harness.databinding.FragmentWatchBinding
import com.balsdon.harness.ui.viewmodel.HarnessViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchFragment : HarnessFragment<FragmentWatchBinding>() {

    companion object {
        fun newInstance() = WatchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingReference = FragmentWatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun settingsUpdated(settings: HarnessViewModel.WatchDisplaySettings) {
        binding.watchFace.screenSettings.apply {
            isAmbientMode = settings.isAmbientMode
            isMuteMode = settings.isAmbientMode
            isLowBitAmbient = settings.isMuteModeToggle
            isBurnInProtection = settings.isLowBitAmbientToggle
            isTwentyFourHour = settings.isTwentyFourHourMode
        }
        binding.watchFace.apply {
            faceMode = settings.faceMode
            size = settings.size
            showComplications = settings.showComplications
        }
    }

    override fun timeUpdated(time: Long) {
        binding.watchFace.currentTime = time
    }
}
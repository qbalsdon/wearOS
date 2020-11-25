package com.balsdon.harness.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.balsdon.harness.ui.viewmodel.HarnessViewModel
import com.balsdon.harness.ui.viewmodel.HarnessViewModelFactory

abstract class HarnessFragment: Fragment() {
    protected lateinit var viewModel: HarnessViewModel
    abstract fun settingsUpdated(settings: HarnessViewModel.WatchDisplaySettings)
    abstract fun timeUpdated(time: Long)

    private val settingsObserver = Observer<HarnessViewModel.WatchDisplaySettings> { newSettings ->
        run {
            settingsUpdated(newSettings)
        }
    }

    private val timeObserver = Observer<Long> { newTime ->
        run {
            timeUpdated(newTime)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), HarnessViewModelFactory()).get(HarnessViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.apply {
            data.observe(viewLifecycleOwner, settingsObserver)
            watchTime.observe(viewLifecycleOwner, timeObserver)
        }
    }

    override fun onPause() {
        viewModel.apply{
            data.removeObserver(settingsObserver)
            watchTime.removeObserver(timeObserver)
        }
        super.onPause()
    }
}
package com.balsdon.harness.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.balsdon.harness.ui.viewmodel.HarnessViewModel
import com.balsdon.harness.ui.viewmodel.HarnessViewModelFactory

abstract class HarnessFragment<T : ViewBinding>: Fragment() {
    protected lateinit var viewModel: HarnessViewModel
    protected var bindingReference: T? = null
    protected val binding get() = bindingReference!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        bindingReference = null
    }
}
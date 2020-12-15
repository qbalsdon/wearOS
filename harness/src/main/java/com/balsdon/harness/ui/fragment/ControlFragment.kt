package com.balsdon.harness.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.balsdon.harness.R
import com.balsdon.harness.databinding.FragmentControlBinding
import com.balsdon.harness.ui.view.TimePickerView
import com.balsdon.harness.ui.view.WatchFaceMode
import com.balsdon.harness.ui.viewmodel.HarnessViewModel

class ControlFragment : HarnessFragment<FragmentControlBinding>() {
    companion object {
        fun newInstance() = ControlFragment()
    }

    private val displayBinding get() = binding.displayControls
    private val timeBinding get() = binding.timeControls

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingReference = FragmentControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        attachToggles()
        attachSpinners()

        displayBinding.faceType.setOnCheckedChangeListener { _, checkedId ->
            viewModel.faceMode = when (checkedId) {
                R.id.faceTypeRound -> WatchFaceMode.Round
                R.id.faceTypeSquare -> WatchFaceMode.Square
                else -> throw RuntimeException("Unknown Face Mode: $checkedId")
            }
        }

        timeBinding.timePicker.timeChangeNotifier = object : TimePickerView.TimeChangeNotifier {
            override fun onTimeChanged() {
                viewModel.time = timeBinding.timePicker.time
            }
        }
    }

    private fun attachToggles() {
        displayBinding.ambientModeToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isAmbientMode = isChecked
        }
        displayBinding.muteModeToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isMuteModeToggle = isChecked
        }
        displayBinding.lowBitAmbientToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isLowBitAmbientToggle = isChecked
        }
        displayBinding.burnInProtectionToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isBurnInProtectionToggle = isChecked
        }
        displayBinding.showComplicationsToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.showComplicationsToggle = isChecked
        }
        timeBinding.animateTimeToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isAnimateTimeToggle = isChecked
        }
        timeBinding.twentyFourHourMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isTwentyFourHourMode = isChecked
        }
    }

    private fun attachSpinners() {
        displayBinding.sizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (view as? TextView)?.text?.apply {
                    viewModel.size = this.intValue()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        timeBinding.speedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (view as? TextView)?.text?.apply {
                    viewModel.speed = this.intValue()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun CharSequence.intValue(): Int =
        Regex("[^0-9]").replace(this, "").toInt()

    override fun settingsUpdated(settings: HarnessViewModel.WatchDisplaySettings) {
        timeBinding.timePicker.isTwentyFourHour = settings.isTwentyFourHourMode
    }

    override fun timeUpdated(time: Long) {
        timeBinding.timePicker.time = time
    }
}
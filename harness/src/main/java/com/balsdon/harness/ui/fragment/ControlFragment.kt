package com.balsdon.harness.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.balsdon.harness.R
import com.balsdon.harness.ui.viewmodel.HarnessViewModel
import com.balsdon.harness.ui.view.TimePickerView
import com.balsdon.harness.ui.view.WatchFaceMode
import kotlinx.android.synthetic.main.fragment_control_display.*
import kotlinx.android.synthetic.main.fragment_control_time.*

class ControlFragment : HarnessFragment() {

    companion object {
        fun newInstance() = ControlFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_control, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        attachToggles()
        attachSpinners()

        faceType.setOnCheckedChangeListener { _, checkedId ->
            viewModel.faceMode = when (checkedId) {
                R.id.faceTypeRound -> WatchFaceMode.Round
                R.id.faceTypeSquare -> WatchFaceMode.Square
                else -> throw RuntimeException("Unknown Face Mode: $checkedId")
            }
        }

        timePicker.timeChangeNotifier = object : TimePickerView.TimeChangeNotifier {
            override fun onTimeChanged() {
                viewModel.time = timePicker.time
            }
        }
    }

    private fun attachToggles() {
        ambientModeToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isAmbientMode = isChecked
        }
        muteModeToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isMuteModeToggle = isChecked
        }
        lowBitAmbientToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isLowBitAmbientToggle = isChecked
        }
        burnInProtectionToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isBurnInProtectionToggle = isChecked
        }
        animateTimeToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isAnimateTimeToggle = isChecked
        }
        twentyFourHourMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isTwentyFourHourMode = isChecked
        }
    }

    private fun attachSpinners() {
        sizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        speedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    override fun settingsUpdated(settings: HarnessViewModel.WatchDisplaySettings) = Unit
    override fun timeUpdated(time: Long) {
        timePicker.time = time
    }
}
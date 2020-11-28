package com.balsdon.harness.ui.view

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.balsdon.harness.R
import com.balsdon.harness.databinding.ViewTimePickerBinding
import java.text.SimpleDateFormat
import java.util.*

class TimePickerView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {
    interface TimeChangeNotifier {
        fun onTimeChanged()
    }

    companion object {
        const val DEFAULT_SEPARATOR = "-"
        const val DEFAULT_TWENTY_FOUR_HOUR_MODE = false
        const val DEFAULT_INCREMENT = 1
        const val DATE_FORMAT = "dd MMM yyyy"
    }

    private val binding: ViewTimePickerBinding =
        ViewTimePickerBinding.inflate(LayoutInflater.from(context), this, true)

    private var separator = DEFAULT_SEPARATOR
        set(value) {
            field = value
            updateLabels()
        }

    var isTwentyFourHour = DEFAULT_TWENTY_FOUR_HOUR_MODE
        set(value) {
            field = value
            binding.meridiemText.visibility = if (value) View.GONE else View.VISIBLE
            updateLabels()
        }

    var timeChangeNotifier: TimeChangeNotifier? = null

    var time = System.currentTimeMillis()
        set(value) {
            if (field == value) return
            field = value
            updateLabels()
        }

    init {

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimePickerView,
            0,
            0
        ).apply {

            try {
                separator = getString(R.styleable.TimePickerView_separator) ?: DEFAULT_SEPARATOR
                //TODO: HOUR / MINUTE / SECOND attributes

            } finally {
                recycle()
                updateLabels()
            }
        }

        binding.hourUpButton.setOnClickListener {
            time = time.addHour(DEFAULT_INCREMENT)
            timeChangeNotifier?.onTimeChanged()
        }

        binding.hourDownButton.setOnClickListener {
            time = time.addHour(DEFAULT_INCREMENT * -1)
            timeChangeNotifier?.onTimeChanged()
        }

        binding.minuteUpButton.setOnClickListener {
            time = time.addMinute(DEFAULT_INCREMENT)
            timeChangeNotifier?.onTimeChanged()
        }

        binding.minuteDownButton.setOnClickListener {
            time = time.addMinute(DEFAULT_INCREMENT * -1)
            timeChangeNotifier?.onTimeChanged()
        }

        binding.secondUpButton.setOnClickListener {
            time = time.addSecond(DEFAULT_INCREMENT)
            timeChangeNotifier?.onTimeChanged()
        }

        binding.secondDownButton.setOnClickListener {
            time = time.addSecond(DEFAULT_INCREMENT * -1)
            timeChangeNotifier?.onTimeChanged()
        }

        binding.datePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            time = time.setDate(year, month, dayOfMonth)
            timeChangeNotifier?.onTimeChanged()
        }

        binding.expandButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                binding.datePickerCard,
                AutoTransition()
            )
            binding.datePicker.visibility =
                if (binding.datePicker.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    private fun updateLabels() {
        binding.hourMinuteSeparator.text = separator
        binding.minuteSecondSeparator.text = separator

        with(Calendar.getInstance().apply { timeInMillis = this@TimePickerView.time }) {
            binding.expandButton.text = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(this.time)
            binding.meridiemText.text = this@TimePickerView.resources.getString(
                if (this.get(Calendar.AM_PM) == Calendar.AM)
                    R.string.am
                else
                    R.string.pm
            )
        }
        binding.datePicker.date = time
        binding.hourLabel.text = time.hour().zeroPad()
        binding.minuteLabel.text = time.minute().zeroPad()
        binding.secondLabel.text = time.second().zeroPad()
    }

    //region Utils
    private fun Int.zeroPad(): String =
        this.toString().padStart(2, '0')

    private fun Long.hour(): Int =
        this.timeValue(if (isTwentyFourHour) Calendar.HOUR_OF_DAY else Calendar.HOUR)

    private fun Long.minute(): Int = this.timeValue(Calendar.MINUTE)
    private fun Long.second(): Int = this.timeValue(Calendar.SECOND)

    private fun Long.timeValue(value: Int): Int {
        require(
            value in arrayOf(
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH,
                Calendar.HOUR_OF_DAY,
                Calendar.HOUR,
                Calendar.MINUTE,
                Calendar.SECOND
            )
        ) { "Time Value not one of Calendar.HOUR_OF_DAY | Calendar.HOUR | Calendar.MINUTE | Calendar.SECOND" }

        val cal = Calendar.getInstance()
        cal.timeInMillis = this
        return cal.get(value)
    }

    private fun Long.setDate(year: Int, month: Int, dayOfMonth: Int) =
        this.setDate(year, Calendar.YEAR)
            .setDate(month, Calendar.MONTH)
            .setDate(dayOfMonth, Calendar.DAY_OF_MONTH)

    private fun Long.addHour(value: Int) = this.addTime(value, Calendar.HOUR)
    private fun Long.addMinute(value: Int) = this.addTime(value, Calendar.MINUTE)
    private fun Long.addSecond(value: Int) = this.addTime(value, Calendar.SECOND)

    private fun Long.setDate(value: Int, timeElement: Int): Long {
        require(timeElement in arrayOf(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH))
        { "Time Element Value [$timeElement] not one of Calendar.YEAR | Calendar.MONTH | Calendar.DAY_OF_MONTH" }

        return Calendar.getInstance().apply {
            timeInMillis = this@TimePickerView.time
            set(timeElement, value)
        }.timeInMillis
    }

    private fun Long.addTime(value: Int, timeElement: Int): Long {
        require(timeElement in arrayOf(Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND))
        { "Time Element Value [$timeElement] not one of Calendar.HOUR | Calendar.MINUTE | Calendar.SECOND" }

        return Calendar.getInstance().apply {
            timeInMillis = this@TimePickerView.time
            add(timeElement, value)
        }.timeInMillis
    }
    //endregion
}
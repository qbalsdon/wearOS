package com.balsdon.harness.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.balsdon.harness.R
import kotlinx.android.synthetic.main.view_time_picker.view.*
import java.util.*

interface TimeChangeNotifier {
    fun onTimeChanged()
}

class TimePickerView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {
    companion object {
        //const val DEFAULT_MODE //TODO: 24 / 12 hour mode
        const val DEFAULT_SEPARATOR = "-"
        const val DEFAULT_INCREMENT = 1
    }

    private var separator = DEFAULT_SEPARATOR
        set(value: String) {
            field = value
            updateLabels()
        }

    var timeChangeNotifier: TimeChangeNotifier? = null

    var time = System.currentTimeMillis()
        set(value) {
            field = value
            timeChangeNotifier?.onTimeChanged()
            updateLabels()
        }

    init {
        inflate(context, R.layout.view_time_picker, this)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimePickerView,
            0,
            0
        ).apply {

            try {
                separator = getString(R.styleable.TimePickerView_separator) ?: DEFAULT_SEPARATOR
                //TODO: HOUR / MINUTE / SECOND

            } finally {
                recycle()
                updateLabels()
            }
        }

        hourUpButton.setOnClickListener {
            time = time.addHour(DEFAULT_INCREMENT)
        }

        hourDownButton.setOnClickListener {
            time = time.addHour(DEFAULT_INCREMENT * -1)
        }

        minuteUpButton.setOnClickListener {
            time = time.addMinute(DEFAULT_INCREMENT)
        }

        minuteDownButton.setOnClickListener {
            time = time.addMinute(DEFAULT_INCREMENT * -1)
        }

        secondUpButton.setOnClickListener {
            time = time.addSecond(DEFAULT_INCREMENT)
        }

        secondDownButton.setOnClickListener {
            time = time.addSecond(DEFAULT_INCREMENT * -1)
        }
    }

    private fun updateLabels() {
        hourMinuteSeparator.text = separator
        minuteSecondSeparator.text = separator

        hourLabel.text = time.hour().zeroPad()
        minuteLabel.text = time.minute().zeroPad()
        secondLabel.text = time.second().zeroPad()
    }

    //region Utils
    private fun Int.zeroPad(): String =
        this.toString().padStart(2, '0')

    private fun Long.hour(): Int = this.timeValue(Calendar.HOUR)
    private fun Long.minute(): Int = this.timeValue(Calendar.MINUTE)
    private fun Long.second(): Int = this.timeValue(Calendar.SECOND)

    private fun Long.timeValue(value: Int): Int {
        require(value == Calendar.HOUR || value == Calendar.MINUTE || value == Calendar.SECOND)
        { "Time Value not one of Calendar.HOUR | Calendar.MINUTE | Calendar.SECOND" }

        val cal = Calendar.getInstance()
        cal.timeInMillis = this
        return cal.get(value)
    }

    private fun Long.addHour(value: Int) = this.addTime(value, Calendar.HOUR)
    private fun Long.addMinute(value: Int) = this.addTime(value, Calendar.MINUTE)
    private fun Long.addSecond(value: Int) = this.addTime(value, Calendar.SECOND)

    private fun Long.addTime(value: Int, timeElement: Int): Long {
        require(timeElement == Calendar.HOUR || timeElement == Calendar.MINUTE || timeElement == Calendar.SECOND)
        { "Time Element Value not one of Calendar.HOUR | Calendar.MINUTE | Calendar.SECOND" }

        return Calendar.getInstance().apply {
            timeInMillis = this@TimePickerView.time
            add(timeElement, value)
        }.timeInMillis
    }
//endregion
}
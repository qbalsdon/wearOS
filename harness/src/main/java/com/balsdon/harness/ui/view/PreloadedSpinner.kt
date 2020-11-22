package com.balsdon.harness.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.balsdon.harness.R

class PreloadedSpinner(context: Context, attrs: AttributeSet) : AppCompatSpinner(context, attrs) {
    private lateinit var itemValues: List<Int>
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PreloadedSpinner,
            0,
            0
        ).apply {
            try {
                getResourceId(R.styleable.PreloadedSpinner_items, R.array.template).apply {
                    adapter = ArrayAdapter.createFromResource(
                        context,
                        this,
                        R.layout.spinner_item
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }
            } finally {
                recycle()
            }
        }
    }
}
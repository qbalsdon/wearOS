package com.balsdon.watchfacerenderer

import android.graphics.Canvas
import java.util.*

/**
 * The basic requirements for drawing a watch face or complication onto a canvas.
 * The contract allows for other * watch faces to be implemented with as little
 * overhead as possible
 */

abstract class WatchFaceRenderable {
    var currentTime: Calendar = Calendar.getInstance()
    var invalidate: (() -> Unit)? = null
    var screenSettings: WatchScreenSettings = WatchScreenSettings()
        set(value) {
            field = value
            updateStyle()
            invalidate?.invoke()
        }

    fun setTimeZone(timeZone: TimeZone) {
        currentTime.timeZone = timeZone
        invalidate?.invoke()
    }

    abstract fun render(canvas: Canvas, time: Long)
    abstract fun initialise()
    abstract fun updateStyle()
    abstract fun surfaceChanged(width: Int, height: Int)
}
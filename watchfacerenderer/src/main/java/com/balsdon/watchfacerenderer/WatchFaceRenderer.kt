package com.balsdon.watchfacerenderer

import android.content.res.Resources
import android.graphics.Canvas
import java.util.*

/**
 * The basic requirements for drawing a watch face onto a canvas. The contract allows for other
 * watch faces to be implemented with as little overhead as possible
 */

abstract class WatchFaceRenderer(resources: Resources? = null) {
    var currentTime: Calendar = Calendar.getInstance()
    var screenSettings: WatchScreenSettings = WatchScreenSettings()
        set(value) {
            field = value
            updateStyle()
        }

    init {
        resources?.let { res -> initImages(res) }
    }

    fun setTimeZone(timeZone: TimeZone) {
        currentTime.timeZone = timeZone
    }

    fun renderWatchFace(canvas: Canvas, time: Long) {
        currentTime.timeInMillis = time
        drawWatchFace(canvas)
    }

    abstract fun initStyle()
    abstract fun updateStyle()

    //Optional methods - if the face requires images
    protected open fun initImages(resources: Resources) = Unit

    abstract fun surfaceChanged(width: Int, height: Int)
    abstract fun drawWatchFace(canvas: Canvas)
}
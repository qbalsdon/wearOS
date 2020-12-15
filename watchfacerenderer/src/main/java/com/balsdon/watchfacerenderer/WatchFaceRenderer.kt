package com.balsdon.watchfacerenderer

import android.content.res.Resources
import android.graphics.Canvas
import java.util.*

abstract class WatchFaceRenderer(resources: Resources? = null): WatchFaceRenderable() {
    init {
        resources?.let { res -> initImages(res) }
    }

    override fun render(canvas: Canvas, time: Long) {
        currentTime.timeInMillis = time
        drawWatchFace(canvas)
    }

    //Optional methods - if the face requires images
    protected open fun initImages(resources: Resources) = Unit
    abstract fun drawWatchFace(canvas: Canvas)
}
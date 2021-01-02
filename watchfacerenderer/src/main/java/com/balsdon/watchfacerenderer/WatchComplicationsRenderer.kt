package com.balsdon.watchfacerenderer

import android.content.Context
import android.graphics.Canvas

abstract class WatchComplicationsRenderer(val context: Context) : WatchFaceRenderable() {
    abstract val complicationIdList: IntArray
    lateinit var dataSource: ComplicationDataSource

    override fun render(canvas: Canvas, time: Long) {
        currentTime.timeInMillis = time
        if (!screenSettings.isMuteMode) {
            dataSource.drawComplications(canvas, currentTime.timeInMillis)
        }
    }

    override fun initialise() {
        dataSource.initialise(context, complicationIdList)
    }
}
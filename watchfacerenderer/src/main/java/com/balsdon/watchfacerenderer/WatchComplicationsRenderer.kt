package com.balsdon.watchfacerenderer

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray

abstract class WatchComplicationsRenderer : WatchFaceRenderable() {
    abstract val complicationIdList: IntArray
    var dataSource: WatchComplicationDataSource? = null

    override fun render(canvas: Canvas, time: Long) {
        currentTime.timeInMillis = time
        drawComplications(canvas)
    }

    abstract fun drawComplications(canvas: Canvas)
    override fun initialise() {
        dataSource?.initialise(complicationIdList)
    }
}
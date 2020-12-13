package com.balsdon.watchfacerenderer

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.SparseArray

/**
 * The data source for complications has been separated from the renderer so that the
 * logic for on screen display is distinct from the data that is used to power that
 * display. This allows for the harness to render blank drawables and the watch app
 * to deal in real data.
 *
 * TODO: Make the data source use a generic
 */

interface WatchComplicationDataSource {
    val activeComplicationDataList: SparseArray<Parcelable>
    val complicationDrawableList: SparseArray<Drawable>
    fun drawComplication(drawable: Drawable, canvas: Canvas, time: Long)
    fun updateComplication(complicationId: Int, data: Parcelable?)
    fun updateStyle(screenSettings: WatchScreenSettings)
    fun initialise(keys: IntArray)
}
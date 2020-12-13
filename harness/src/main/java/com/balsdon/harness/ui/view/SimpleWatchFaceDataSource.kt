package com.balsdon.harness.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import com.balsdon.harness.R
import com.balsdon.watchfacerenderer.WatchComplicationDataSource
import com.balsdon.watchfacerenderer.WatchScreenSettings

class SimpleWatchFaceDataSource(private val context: Context): WatchComplicationDataSource {
    override val activeComplicationDataList: SparseArray<Parcelable>
            = SparseArray()
    override val complicationDrawableList: SparseArray<Drawable>
            = SparseArray()

    override fun initialise(keys: IntArray) {
        keys.forEach {
            complicationDrawableList.put(it,ContextCompat.getDrawable(context, R.drawable.ic_complication_placeholder))
        }
    }

    override fun drawComplication(drawable: Drawable, canvas: Canvas, time: Long) {
        drawable.draw(canvas)
    }

    override fun updateComplication(complicationId: Int, data: Parcelable?) = Unit

    override fun updateStyle(screenSettings: WatchScreenSettings) {
        //TODO: Change drawable based on settings
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_complication_placeholder)!!

        complicationDrawableList.forEach { key, _ ->
            complicationDrawableList.put(key, drawable)
        }
    }
}
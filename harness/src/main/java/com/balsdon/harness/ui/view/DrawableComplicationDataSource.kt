package com.balsdon.harness.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import com.balsdon.harness.R
import com.balsdon.watchfacerenderer.ComplicationDataSource
import com.balsdon.watchfacerenderer.WatchScreenSettings

class DrawableComplicationDataSource : ComplicationDataSource {
    override val activeComplicationDataList: SparseArray<Parcelable> = SparseArray()
    override val complicationDrawableList: SparseArray<Drawable> = SparseArray()

    private lateinit var context: Context

    override fun initialise(context: Context, keys: IntArray) {
        this.context = context
        keys.forEach {
            complicationDrawableList.put(
                it,
                ContextCompat.getDrawable(context, R.drawable.ic_complication_placeholder)
            )
        }
    }

    override fun drawComplications(canvas: Canvas, time: Long) =
        complicationDrawableList.forEach { _, drawable ->
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
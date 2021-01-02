package com.balsdon.harness.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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

    /**
     * TODO: Update the drawables based on screen settings
     * WARNING: Maintain the drawable.bounds as this is only called on surface changed
     * I have found that when I change the drawables in the complicationDrawableList
     * the bounds get confused and both drawables get rendered on the same point
     */
    override fun updateStyle(screenSettings: WatchScreenSettings) = Unit
}
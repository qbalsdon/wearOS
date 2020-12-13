package com.balsdon.watchapplication.di

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.rendering.ComplicationDrawable
import android.util.SparseArray
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import com.balsdon.watchapplication.R
import com.balsdon.watchfacerenderer.WatchComplicationDataSource
import com.balsdon.watchfacerenderer.WatchScreenSettings

class ExampleComplicationDataSource(private val ctx: Context) : WatchComplicationDataSource {

    override val activeComplicationDataList: SparseArray<Parcelable> =
        SparseArray()
    override val complicationDrawableList: SparseArray<Drawable> =
        SparseArray()

    override fun drawComplication(drawable: Drawable, canvas: Canvas, time: Long) {
        (drawable as ComplicationDrawable).draw(canvas, time)
    }

    override fun updateComplication(complicationId: Int, data: Parcelable?) {
        if (data == null || data !is ComplicationData) return
        activeComplicationDataList.put(complicationId, data)

        (complicationDrawableList.get(complicationId) as ComplicationDrawable)
        .setComplicationData(data)
    }

    override fun updateStyle(screenSettings: WatchScreenSettings) {
        complicationDrawableList.forEach { _, drawable ->
            with(drawable as ComplicationDrawable) {
                setInAmbientMode(screenSettings.isAmbientMode)
                setLowBitAmbient(screenSettings.isLowBitAmbient)
                setBurnInProtection(screenSettings.isBurnInProtection)
            }
        }
    }

    override fun initialise(keys: IntArray) {
        val complicationResource = R.drawable.custom_complication_styles

        keys.forEach {
            complicationDrawableList.put(it, ctx.loadComplicationDrawable(complicationResource))
        }
    }

    private fun Context.loadComplicationDrawable(resource: Int): ComplicationDrawable =
        (ContextCompat.getDrawable(this, resource) as ComplicationDrawable).apply {
            setContext(this@loadComplicationDrawable)
        }
}
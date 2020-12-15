package com.balsdon.watchapplication.complication

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
import com.balsdon.watchfacerenderer.ComplicationDataSource
import com.balsdon.watchfacerenderer.WatchScreenSettings

/**
 * Provides the logic and structure around actual complication data. The data source
 * is a component of the [WatchComplicationsRenderer] that provides the Android Wear
 * OS-dependent structures. This is separated to the app because it is agnostic of
 * how many complications the user has and where they are drawn.
 *
 * I am questioning the injection of this module because I do not really see the point
 * there is no reason for a developer to want to change or swap out this structure.
 * However it is good that it's based on [ComplicationDataSource] and that it's
 * distinct from the [ComplicatedWatchFaceService]
 */

class WatchComplicationDataSource : ComplicationDataSource {

    override val activeComplicationDataList: SparseArray<Parcelable> =
        SparseArray()
    override val complicationDrawableList: SparseArray<Drawable> =
        SparseArray()

    override fun drawComplications(canvas: Canvas, time: Long) =
        complicationDrawableList.forEach { _, drawable ->
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

    override fun initialise(context: Context, keys: IntArray) {
        val complicationResource = R.drawable.custom_complication_styles

        keys.forEach {
            complicationDrawableList.put(it, context.loadComplicationDrawable(complicationResource))
        }
    }

    private fun Context.loadComplicationDrawable(resource: Int): ComplicationDrawable =
        (ContextCompat.getDrawable(this, resource) as ComplicationDrawable).apply {
            setContext(this@loadComplicationDrawable)
        }
}
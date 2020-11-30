package com.balsdon.watchapplication.service

import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.rendering.ComplicationDrawable
import android.util.SparseArray
import com.balsdon.watchfacerenderer.ComplicationRenderer

interface ComplicationController {
    //TODO: Data structure for these arrays - they are parallel
    val activeComplicationDataList: SparseArray<ComplicationData>
    val complicationDrawableList: SparseArray<ComplicationDrawable>
    val complicationIdList: IntArray

    fun initializeComplications()
    fun onAmbientModeChanged(inAmbientMode: Boolean)
    fun onSurfaceChanged(width: Int, height: Int)
    fun updateProperties(isLowBitAmbient: Boolean, isBurnInProtection: Boolean)

    val complicationRenderer: ComplicationRenderer
    // Relates to [WatchFaceService.onComplicationDataUpdate]
    fun onComplicationDataUpdate(watchFaceComplicationId: Int, complicationData: ComplicationData?)
}
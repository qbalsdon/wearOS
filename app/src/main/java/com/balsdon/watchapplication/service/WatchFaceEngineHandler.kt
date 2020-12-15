package com.balsdon.watchapplication.service

import android.graphics.Canvas
import android.graphics.Rect
import android.support.wearable.complications.ComplicationData
import android.view.SurfaceHolder

/**
 * [CanvasWatchFaceService] has an annoying inner class [CanvasWatchFaceService.Engine] that
 * can only be internally overriden. This surfaces the required methods up and out so that
 * a neater structure can be made.
 *
 * The Engine handler is an interface that allows the Watch Face to update different properties
 * and demonstrates the need for simplicity within the Wear OS Framework.
 */
interface WatchFaceEngineHandler {
    fun engineCreated()
    fun updateProperties(lowBitAmbientStatus: Boolean, isBurnInProtectionMode: Boolean)
    fun updateAmbientMode(inAmbientMode: Boolean)
    fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int)
    fun render(canvas: Canvas, bounds: Rect?, time: Long)
    fun updateComplications(watchFaceComplicationId: Int, data: ComplicationData?)
}
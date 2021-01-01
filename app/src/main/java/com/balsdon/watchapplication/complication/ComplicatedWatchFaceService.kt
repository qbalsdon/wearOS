package com.balsdon.watchapplication.complication

import android.graphics.Canvas
import android.graphics.Rect
import android.support.wearable.complications.ComplicationData
import android.view.SurfaceHolder
import com.balsdon.watchapplication.service.WatchFaceService
import com.balsdon.watchfacerenderer.ComplicationDataSource
import com.balsdon.watchfacerenderer.WatchComplicationsRenderer
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


/**
 * This class demonstrates the results of performing the COMPLICATIONS CODELAB at
 * https://developer.android.com/codelabs/complications#0 with my proposed
 * architecture - which lifts the burden of managing complications out of the
 * engine
 */

@AndroidEntryPoint
class ComplicatedWatchFaceService : WatchFaceService() {
    @Inject
    lateinit var watchComplicationsRenderer: WatchComplicationsRenderer

    private val complicationsDataSource: ComplicationDataSource =
        WatchComplicationDataSource()

    override fun engineCreated() {
        super.engineCreated()

        watchComplicationsRenderer.apply {
            invalidate = engine::invalidate
            dataSource = complicationsDataSource
            engine.setActiveComplications(*complicationIdList)
            initialise()
        }
    }

    override fun setTimeZone(timeZone: TimeZone) {
        super.setTimeZone(timeZone)
        watchComplicationsRenderer.setTimeZone(timeZone)
    }

    override fun updateProperties(lowBitAmbientStatus: Boolean, isBurnInProtectionMode: Boolean) {
        super.updateProperties(lowBitAmbientStatus, isBurnInProtectionMode)

        watchComplicationsRenderer.screenSettings = watchComplicationsRenderer.screenSettings.copy(
            isLowBitAmbient = lowBitAmbientStatus,
            isBurnInProtection = isBurnInProtectionMode
        )
    }

    override fun updateAmbientMode(inAmbientMode: Boolean) {
        super.updateAmbientMode(inAmbientMode)

        if (watchComplicationsRenderer.screenSettings.isAmbientMode != inAmbientMode) {
            watchComplicationsRenderer.screenSettings =
                watchComplicationsRenderer.screenSettings.copy(
                    isAmbientMode = inAmbientMode
                )
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        super.surfaceChanged(holder, format, width, height)
        watchComplicationsRenderer.surfaceChanged(width, height)
    }

    override fun render(canvas: Canvas, bounds: Rect?, time: Long) {
        super.render(canvas, bounds, time)
        watchComplicationsRenderer.render(canvas, time)
    }

    override fun updateComplications(watchFaceComplicationId: Int, data: ComplicationData?) {
        super.updateComplications(watchFaceComplicationId, data)
        complicationsDataSource.updateComplication(watchFaceComplicationId, data)
        engine.invalidate()
    }
}
package com.balsdon.watchapplication.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.support.wearable.complications.ComplicationData
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.view.SurfaceHolder
import android.widget.Toast
import com.balsdon.watchapplication.R
import com.balsdon.watchapplication.service.EngineHandler.Companion.MSG_UPDATE_TIME
import com.balsdon.watchfacerenderer.WatchFaceRenderer
import java.util.*
import javax.inject.Inject

/**
 * Updates rate in milliseconds for interactive mode. We update once a second to advance the
 * second hand.
 */
private const val INTERACTIVE_UPDATE_RATE_MS = 1000

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn"t
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 *
 *
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
interface WatchFaceEngineHandler {
    fun engineCreated()
    fun updateProperties(lowBitAmbientStatus: Boolean, isBurnInProtectionMode: Boolean)
    fun updateAmbientMode(inAmbientMode: Boolean)
    fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int)
    fun render(canvas: Canvas, bounds: Rect?, time: Long)
    fun updateComplications(watchFaceComplicationId: Int, data: ComplicationData?)
}

abstract class WatchFaceService : CanvasWatchFaceService(), WatchFaceEngineHandler {
    @Inject
    lateinit var watchFaceRenderer: WatchFaceRenderer

    lateinit var engine: WatchFaceEngine

    override fun onCreateEngine(): WatchFaceEngine {
        engine = WatchFaceEngine(watchFaceRenderer)
        return engine
    }

    override fun engineCreated() {
        with(engine.faceRenderer) {
            invalidate = engine::invalidate
            initialise()
        }
    }

    override fun updateProperties(lowBitAmbientStatus: Boolean, isBurnInProtectionMode: Boolean) {
        engine.faceRenderer.screenSettings = engine.faceRenderer.screenSettings.copy(
            isLowBitAmbient = lowBitAmbientStatus,
            isBurnInProtection = isBurnInProtectionMode
        )
    }

    override fun updateAmbientMode(inAmbientMode: Boolean) {
        if (engine.faceRenderer.screenSettings.isAmbientMode != inAmbientMode) {
            engine.faceRenderer.screenSettings = engine.faceRenderer.screenSettings.copy(
                isAmbientMode = inAmbientMode
            )
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        engine.faceRenderer.surfaceChanged(width, height)
    }

    override fun render(canvas: Canvas, bounds: Rect?, time: Long) {
        engine.faceRenderer.render(canvas, time)
    }

    override fun updateComplications(watchFaceComplicationId: Int, data: ComplicationData?) = Unit

    inner class WatchFaceEngine(val faceRenderer: WatchFaceRenderer) :
        CanvasWatchFaceService.Engine(), TimeUpdateHandler {
        private var hasRegisteredTimeZoneReceiver = false

        /* Handler to update the time once a second in interactive mode. */
        private val updateTimeHandler = EngineHandler(this)

        private val timeZoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                faceRenderer.setTimeZone(TimeZone.getDefault())
            }
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)
            setWatchFaceStyle(
                WatchFaceStyle.Builder(this@WatchFaceService)
                    .setAcceptsTapEvents(true)
                    .build()
            )

            engineCreated()
        }

        override fun onDestroy() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        override fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)
            val lowBitAmbientStatus =
                properties.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false)
            val isBurnInProtectionMode =
                properties.getBoolean(WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false)
            updateProperties(lowBitAmbientStatus, isBurnInProtectionMode)
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            updateAmbientMode(inAmbientMode)
            // Check and trigger whether or not timer should be running (only
            // in active mode).
            updateTimer()
        }

        override fun onInterruptionFilterChanged(interruptionFilter: Int) {
            super.onInterruptionFilterChanged(interruptionFilter)
            val inMuteMode = interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE

            /* Dim display in mute mode. */
            if (faceRenderer.screenSettings.isMuteMode != inMuteMode) {
                faceRenderer.screenSettings = faceRenderer.screenSettings.copy(
                    isMuteMode = inMuteMode
                )
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            surfaceChanged(holder, format, width, height)
        }

        /**
         * Captures tap event (and tap type). The [WatchFaceService.TAP_TYPE_TAP] case can be
         * used for implementing specific logic to handle the gesture.
         */
        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            when (tapType) {
                WatchFaceService.TAP_TYPE_TOUCH -> {
                    // The user has started touching the screen.
                }
                WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                    // The user has started a different gesture or otherwise cancelled the tap.
                }
                WatchFaceService.TAP_TYPE_TAP ->
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(applicationContext, R.string.message, Toast.LENGTH_SHORT)
                        .show()
            }
            invalidate()
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            val now = System.currentTimeMillis()
            render(canvas, bounds, now)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()
                /* Update time zone in case it changed while we weren"t visible. */
                faceRenderer.setTimeZone(TimeZone.getDefault())
                invalidate()
            } else {
                unregisterReceiver()
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer()
        }

        override fun onComplicationDataUpdate(
            watchFaceComplicationId: Int,
            data: ComplicationData?
        ) {
            updateComplications(watchFaceComplicationId, data)
        }

        private fun registerReceiver() {
            if (hasRegisteredTimeZoneReceiver) {
                return
            }
            hasRegisteredTimeZoneReceiver = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@WatchFaceService.registerReceiver(timeZoneReceiver, filter)
        }

        private fun unregisterReceiver() {
            if (!hasRegisteredTimeZoneReceiver) {
                return
            }
            hasRegisteredTimeZoneReceiver = false
            this@WatchFaceService.unregisterReceiver(timeZoneReceiver)
        }

        /**
         * Starts/stops the [.mUpdateTimeHandler] timer based on the state of the watch face.
         */
        private fun updateTimer() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Returns whether the [.mUpdateTimeHandler] timer should be running. The timer
         * should only run in active mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !faceRenderer.screenSettings.isAmbientMode
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        override fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                updateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }
    }
}
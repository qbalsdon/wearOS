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
import com.balsdon.watchapplication.service.InteractiveTimeUpdateHandler.Companion.MSG_UPDATE_TIME
import com.balsdon.watchfacerenderer.WatchFaceRenderer
import java.util.*
import javax.inject.Inject
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

abstract class WatchFaceService : CanvasWatchFaceService(), WatchFaceEngineHandler {
    @Inject
    lateinit var watchFaceRenderer: WatchFaceRenderer
    lateinit var engine: WatchFaceEngine

    override fun onCreateEngine(): WatchFaceEngine {
        engine = WatchFaceEngine(this)
        return engine
    }

    //region WatchFaceEngineHandler
    override val inAmbientMode: Boolean
        get() = watchFaceRenderer.screenSettings.isAmbientMode

    override fun engineCreated() {
        with(watchFaceRenderer) {
            invalidate = engine::invalidate
            initialise()
        }
    }

    override fun updateProperties(lowBitAmbientStatus: Boolean, isBurnInProtectionMode: Boolean) =
        with(watchFaceRenderer) {
            screenSettings = watchFaceRenderer.screenSettings.copy(
                isLowBitAmbient = lowBitAmbientStatus,
                isBurnInProtection = isBurnInProtectionMode
            )
        }

    override fun updateAmbientMode(inAmbientMode: Boolean) =
        with(watchFaceRenderer.screenSettings) {
            if (isAmbientMode != inAmbientMode) {
                watchFaceRenderer.screenSettings = watchFaceRenderer.screenSettings.copy(
                    isAmbientMode = inAmbientMode
                )
            }
        }

    override fun updateMuteMode(inMuteMode: Boolean) =
        with(watchFaceRenderer) {
            if (screenSettings.isMuteMode != inMuteMode) {
                screenSettings = screenSettings.copy(
                    isMuteMode = inMuteMode
                )
            }
        }

    override fun setTimeZone(timeZone: TimeZone) =
        watchFaceRenderer.setTimeZone(timeZone)

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) =
        watchFaceRenderer.surfaceChanged(width, height)

    override fun render(canvas: Canvas, bounds: Rect?, time: Long) =
        watchFaceRenderer.render(canvas, time)

    override fun updateComplications(watchFaceComplicationId: Int, data: ComplicationData?) = Unit
    //endregion

    inner class WatchFaceEngine(private val watchFaceEngineHandler: WatchFaceEngineHandler) :
        CanvasWatchFaceService.Engine(), Updateable {
        private var hasRegisteredTimeZoneReceiver = false

        /* Handler to update the time once a second in interactive mode. */
        private val updateTimeHandler = InteractiveTimeUpdateHandler(this)

        private val timeZoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                watchFaceEngineHandler.setTimeZone(TimeZone.getDefault())
            }
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)
            setWatchFaceStyle(
                WatchFaceStyle.Builder(this@WatchFaceService)
                    .setAcceptsTapEvents(true)
                    .build()
            )

            watchFaceEngineHandler.engineCreated()
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
            watchFaceEngineHandler.updateProperties(lowBitAmbientStatus, isBurnInProtectionMode)
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            watchFaceEngineHandler.updateAmbientMode(inAmbientMode)
            // Check and trigger whether or not timer should be running (only
            // in active mode).
            updateTimer()
        }

        override fun onInterruptionFilterChanged(interruptionFilter: Int) {
            super.onInterruptionFilterChanged(interruptionFilter)
            val inMuteMode = interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE

            /* Dim display in mute mode. */
            watchFaceEngineHandler.updateMuteMode(inMuteMode)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            watchFaceEngineHandler.surfaceChanged(holder, format, width, height)
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
            watchFaceEngineHandler.render(canvas, bounds, now)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()
                /* Update time zone in case it changed while we weren't visible. */
                watchFaceEngineHandler.setTimeZone(TimeZone.getDefault())
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
         * Starts/stops the [updateTimeHandler] timer based on the state of the watch face.
         */
        private fun updateTimer() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Returns whether the [updateTimeHandler] timer should be running. The timer
         * should only run in active mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !watchFaceEngineHandler.inAmbientMode
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        override fun update() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = Companion.INTERACTIVE_UPDATE_RATE_MS - timeMs % Companion.INTERACTIVE_UPDATE_RATE_MS
                updateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }
    }

    companion object {
        /**
         * Updates rate in milliseconds for interactive mode. We update once a second to advance the
         * second hand.
         */
        private const val INTERACTIVE_UPDATE_RATE_MS = 1000
    }
}
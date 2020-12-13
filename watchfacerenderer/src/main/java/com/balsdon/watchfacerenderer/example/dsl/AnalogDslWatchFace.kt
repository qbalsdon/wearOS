package com.balsdon.watchfacerenderer.example.dsl

import android.content.res.Resources
import android.graphics.*
import com.balsdon.watchfacerenderer.R
import com.balsdon.watchfacerenderer.WatchFaceRenderer
import com.balsdon.watchfacerenderer.example.dsl.model.AnalogWatchFaceStyle
import com.balsdon.watchfacerenderer.example.dsl.model.EMPTY_IMAGE_RESOURCE
import com.balsdon.watchfacerenderer.example.dsl.service.analogWatchFaceStyle
import java.util.*

/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * I have modified the exiting Kotlin Codelab example to fit my DI
 */
class AnalogDslWatchFace(val resources: Resources? = null) : WatchFaceRenderer(resources) {
    private lateinit var analogWatchFaceStyle: AnalogWatchFaceStyle

    private var registeredTimeZoneReceiver = false
    private var centerX: Float = 0F
    private var centerY: Float = 0F

    private var secondHandLengthRatio: Float = 0F
    private var minuteHandLengthRatio: Float = 0F
    private var hourHandLengthRatio: Float = 0F

    private lateinit var hourPaint: Paint
    private lateinit var minutePaint: Paint
    private lateinit var secondPaint: Paint
    private lateinit var tickAndCirclePaint: Paint

    private lateinit var backgroundPaint: Paint

    // Best practice is to always use black for watch face in ambient mode (saves battery
    // and prevents burn-in.
    private val backgroundAmbientPaint: Paint = Paint().apply { color = Color.BLACK }

    private var backgroundImageEnabled: Boolean = false
    private lateinit var backgroundBitmap: Bitmap
    private lateinit var grayBackgroundBitmap: Bitmap

    private val calendar: Calendar
    get() = currentTime

    //region original DSL class methods
    private fun initializeBackground() {

        backgroundImageEnabled =
            analogWatchFaceStyle.watchFaceBackgroundImage.backgroundImageResource !=
                    EMPTY_IMAGE_RESOURCE

        if (backgroundImageEnabled) {
            backgroundBitmap = BitmapFactory.decodeResource(
                resources,
                analogWatchFaceStyle.watchFaceBackgroundImage.backgroundImageResource
            )
        }
    }

    private fun initializeWatchFace() {
        hourPaint = Paint().apply {
            color = analogWatchFaceStyle.watchFaceColors.main
            strokeWidth = analogWatchFaceStyle.watchFaceDimensions.hourHandWidth
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                0f,
                0f,
                analogWatchFaceStyle.watchFaceColors.shadow
            )
        }

        minutePaint = Paint().apply {
            color = analogWatchFaceStyle.watchFaceColors.main
            strokeWidth = analogWatchFaceStyle.watchFaceDimensions.minuteHandWidth
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                0f,
                0f,
                analogWatchFaceStyle.watchFaceColors.shadow
            )
        }

        secondPaint = Paint().apply {
            color = analogWatchFaceStyle.watchFaceColors.highlight
            strokeWidth = analogWatchFaceStyle.watchFaceDimensions.secondHandWidth
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                0f,
                0f,
                analogWatchFaceStyle.watchFaceColors.shadow
            )
        }

        tickAndCirclePaint = Paint().apply {
            color = analogWatchFaceStyle.watchFaceColors.main
            strokeWidth = analogWatchFaceStyle.watchFaceDimensions.secondHandWidth
            isAntiAlias = true
            style = Paint.Style.STROKE
            setShadowLayer(
                analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                0f,
                0f,
                analogWatchFaceStyle.watchFaceColors.shadow
            )
        }

        backgroundPaint = Paint().apply {
            color = analogWatchFaceStyle.watchFaceColors.background
        }
    }

    private fun updateWatchHandStyle() {
        if (screenSettings.isAmbientMode) {
            hourPaint.color = Color.WHITE
            minutePaint.color = Color.WHITE
            secondPaint.color = Color.WHITE
            tickAndCirclePaint.color = Color.WHITE

            if (screenSettings.isLowBitAmbient) {
                hourPaint.isAntiAlias = false
                minutePaint.isAntiAlias = false
                secondPaint.isAntiAlias = false
                tickAndCirclePaint.isAntiAlias = false
            }

            hourPaint.clearShadowLayer()
            minutePaint.clearShadowLayer()
            secondPaint.clearShadowLayer()
            tickAndCirclePaint.clearShadowLayer()

        } else {
            hourPaint.color = analogWatchFaceStyle.watchFaceColors.main
            minutePaint.color = analogWatchFaceStyle.watchFaceColors.main
            secondPaint.color = analogWatchFaceStyle.watchFaceColors.highlight
            tickAndCirclePaint.color = analogWatchFaceStyle.watchFaceColors.main

            hourPaint.isAntiAlias = true
            minutePaint.isAntiAlias = true
            secondPaint.isAntiAlias = true
            tickAndCirclePaint.isAntiAlias = true

            hourPaint.setShadowLayer(
                analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                0f,
                0f,
                analogWatchFaceStyle.watchFaceColors.shadow
            )
            minutePaint.setShadowLayer(
                analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                0f,
                0f,
                analogWatchFaceStyle.watchFaceColors.shadow
            )
            secondPaint.setShadowLayer(
                analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                0f,
                0f,
                analogWatchFaceStyle.watchFaceColors.shadow
            )
            tickAndCirclePaint.setShadowLayer(
                analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                0f,
                0f,
                analogWatchFaceStyle.watchFaceColors.shadow
            )
        }
    }

    private fun initGrayBackgroundBitmap() {
        grayBackgroundBitmap = Bitmap.createBitmap(
            backgroundBitmap.width,
            backgroundBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(grayBackgroundBitmap)
        val grayPaint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        grayPaint.colorFilter = filter
        canvas.drawBitmap(backgroundBitmap, 0f, 0f, grayPaint)
    }

    private fun drawBackground(canvas: Canvas) {

        if (screenSettings.isAmbientMode && screenSettings.isBurnInProtection) {
            canvas.drawColor(backgroundAmbientPaint.color)

        } else if (screenSettings.isAmbientMode && backgroundImageEnabled) {
            canvas.drawBitmap(grayBackgroundBitmap, 0f, 0f, backgroundAmbientPaint)

        } else if (backgroundImageEnabled) {
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundPaint)

        } else {
            canvas.drawColor(backgroundPaint.color)
        }
    }

    private fun drawDslWatchFace(canvas: Canvas) {
        /*
         * Draw ticks. Usually you will want to bake this directly into the photo, but in
         * cases where you want to allow users to select their own photos, this dynamically
         * creates them on top of the photo.
         */
        val innerTickRadius = centerX - 10
        val outerTickRadius = centerX
        for (tickIndex in 0..11) {
            val tickRot = (tickIndex.toDouble() * Math.PI * 2.0 / 12).toFloat()
            val innerX = Math.sin(tickRot.toDouble()).toFloat() * innerTickRadius
            val innerY = (-Math.cos(tickRot.toDouble())).toFloat() * innerTickRadius
            val outerX = Math.sin(tickRot.toDouble()).toFloat() * outerTickRadius
            val outerY = (-Math.cos(tickRot.toDouble())).toFloat() * outerTickRadius
            canvas.drawLine(
                centerX + innerX, centerY + innerY,
                centerX + outerX, centerY + outerY, tickAndCirclePaint
            )
        }

        /*
         * These calculations reflect the rotation in degrees per unit of time, e.g.,
         * 360 / 60 = 6 and 360 / 12 = 30.
         */
        val seconds =
            calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f
        val secondsRotation = seconds * 6f

        val minutesRotation = calendar.get(Calendar.MINUTE) * 6f

        val hourHandOffset = calendar.get(Calendar.MINUTE) / 2f
        val hoursRotation = calendar.get(Calendar.HOUR) * 30 + hourHandOffset

        /*
         * Save the canvas state before we can begin to rotate it.
         */
        canvas.save()

        val distanceFromCenterToArms =
            analogWatchFaceStyle.watchFaceDimensions.innerCircleRadius +
                    analogWatchFaceStyle.watchFaceDimensions.innerCircleToArmsDistance

        canvas.rotate(hoursRotation, centerX, centerY)
        canvas.drawLine(
            centerX,
            centerY - distanceFromCenterToArms,
            centerX,
            centerY - hourHandLengthRatio,
            hourPaint
        )

        canvas.rotate(minutesRotation - hoursRotation, centerX, centerY)
        canvas.drawLine(
            centerX,
            centerY - distanceFromCenterToArms,
            centerX,
            centerY - minuteHandLengthRatio,
            minutePaint
        )

        /*
         * Ensure the "seconds" hand is drawn only when we are in interactive mode.
         * Otherwise, we only update the watch face once a minute.
         */
        if (!screenSettings.isAmbientMode) {
            canvas.rotate(secondsRotation - minutesRotation, centerX, centerY)
            canvas.drawLine(
                centerX,
                centerY - distanceFromCenterToArms,
                centerX,
                centerY - secondHandLengthRatio,
                secondPaint
            )
        }
        canvas.drawCircle(
            centerX,
            centerY,
            analogWatchFaceStyle.watchFaceDimensions.innerCircleRadius,
            tickAndCirclePaint
        )

        /* Restore the canvas' original orientation. */
        canvas.restore()
    }
    //endregion

    //region modified methods
    private fun updateForMuteMode() {
        hourPaint.alpha = if (screenSettings.isMuteMode) 100 else 255
        minutePaint.alpha = if (screenSettings.isMuteMode) 100 else 255
        secondPaint.alpha = if (screenSettings.isMuteMode) 80 else 255
    }

    private fun createWatchFaceStyle(): AnalogWatchFaceStyle = analogWatchFaceStyle {
        watchFaceColors {
            main = Color.CYAN
            highlight = Color.parseColor("#ffa500")
            background = Color.WHITE
        }
        watchFaceDimensions {
            hourHandRadiusRatio = 0.2f
            minuteHandRadiusRatio = 0.5f
            secondHandRadiusRatio = 0.9f
        }
        watchFaceBackgroundImage {
            backgroundImageResource = R.drawable.background_image
        }
    }
    //endregion

    //region WatchFaceRenderer methods
    override fun initialise() {
        analogWatchFaceStyle = createWatchFaceStyle()

        initializeBackground()
        initializeWatchFace()
    }

    override fun updateStyle() {
        updateWatchHandStyle()
        updateForMuteMode()
    }

    override fun surfaceChanged(width: Int, height: Int) {
        /*
             * Find the coordinates of the center point on the screen, and ignore the window
             * insets, so that, on round watches with a "chin", the watch face is centered on the
             * entire screen, not just the usable portion.
             */
        centerX = width / 2f
        centerY = height / 2f

        /*
         * Calculate lengths of different hands based on watch screen size.
         */
        secondHandLengthRatio =
            (centerX * analogWatchFaceStyle.watchFaceDimensions.secondHandRadiusRatio)
        minuteHandLengthRatio =
            (centerX * analogWatchFaceStyle.watchFaceDimensions.minuteHandRadiusRatio)
        hourHandLengthRatio =
            (centerX * analogWatchFaceStyle.watchFaceDimensions.hourHandRadiusRatio)

        if (backgroundImageEnabled) {
            // Scale loaded background image (more efficient) if surface dimensions change.
            val scale = width.toFloat() / backgroundBitmap.width.toFloat()

            backgroundBitmap = Bitmap.createScaledBitmap(
                backgroundBitmap,
                (backgroundBitmap.width * scale).toInt(),
                (backgroundBitmap.height * scale).toInt(), true
            )

            /*
             * Create a gray version of the image only if it will look nice on the device in
             * ambient mode. That means we don't want devices that support burn-in
             * protection (slight movements in pixels, not great for images going all the way
             * to edges) and low ambient mode (degrades image quality).
             *
             * Also, if your watch face will know about all images ahead of time (users aren't
             * selecting their own photos for the watch face), it will be more
             * efficient to create a black/white version (png, etc.) and load that when
             * you need it.
             */
            if (!screenSettings.isBurnInProtection && !screenSettings.isLowBitAmbient) {
                initGrayBackgroundBitmap()
            }
        }
    }

    override fun drawWatchFace(canvas: Canvas) {
        drawBackground(canvas)
        drawDslWatchFace(canvas)
    }
    //endregion
}
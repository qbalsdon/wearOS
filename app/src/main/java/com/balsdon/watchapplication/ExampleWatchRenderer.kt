package com.balsdon.watchapplication

import android.content.res.Resources
import android.graphics.*
import androidx.palette.graphics.Palette
import com.balsdon.watchapplication.watchfacerenderer.WatchFaceRenderer
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class ExampleWatchRenderer(resources: Resources? = null) : WatchFaceRenderer(resources) {
    companion object {
        //These have been converted to relative-to-width ratios
        private const val HOUR_STROKE_WIDTH = 0.014F //5f / 360
        private const val MINUTE_STROKE_WIDTH = 0.0083F //3f / 360
        private const val SECOND_TICK_STROKE_WIDTH = 0.006F //2f / 360
        private const val TICK_LENGTH = 0.027F //10f / 360
        private const val SHADOW_RADIUS = 0.02F // 6f /360
        private const val CENTER_GAP_AND_CIRCLE_RADIUS = 0.01F // 4f / 360
    }

    private lateinit var mBackgroundPaint: Paint
    private lateinit var mBackgroundBitmap: Bitmap
    private lateinit var mGrayBackgroundBitmap: Bitmap

    private var mWatchHandColor: Int = 0
    private var mWatchHandHighlightColor: Int = 0
    private var mWatchHandShadowColor: Int = 0

    private lateinit var mHourPaint: Paint
    private lateinit var mMinutePaint: Paint
    private lateinit var mSecondPaint: Paint
    private lateinit var mTickAndCirclePaint: Paint

    private var mCenterX: Float = 0F
    private var mCenterY: Float = 0F
    private var mTickLength: Float = 10F
    private var mShadowRadius: Float = 10F
    private var mCenterCircleRadius: Float = 10F

    private var mSecondHandLength: Float = 0F
    private var sMinuteHandLength: Float = 0F
    private var sHourHandLength: Float = 0F

    override fun initImages(resources: Resources) {
        super.initImages(resources)
        mBackgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.watchface_service_bg)
    }

    override fun initStyle() {
        mBackgroundPaint = Paint().apply {
            color = Color.BLACK
        }

        mWatchHandColor = Color.WHITE
        mWatchHandHighlightColor = Color.RED
        mWatchHandShadowColor = Color.BLACK

        mHourPaint = Paint().apply {
            color = mWatchHandColor
            strokeWidth = HOUR_STROKE_WIDTH
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }

        mMinutePaint = Paint().apply {
            color = mWatchHandColor
            strokeWidth = MINUTE_STROKE_WIDTH
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }

        mSecondPaint = Paint().apply {
            color = mWatchHandHighlightColor
            strokeWidth = SECOND_TICK_STROKE_WIDTH
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }

        mTickAndCirclePaint = Paint().apply {
            color = mWatchHandColor
            strokeWidth = SECOND_TICK_STROKE_WIDTH
            isAntiAlias = true
            style = Paint.Style.STROKE
            setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }

        /* Extracts colors from background image to improve watchface style. */
        Palette.from(mBackgroundBitmap).generate {
            it?.let {
                mWatchHandHighlightColor = it.getVibrantColor(Color.RED)
                mWatchHandColor = it.getLightVibrantColor(Color.WHITE)
                mWatchHandShadowColor = it.getDarkMutedColor(Color.BLACK)
                updateStyle()
            }
        }
    }

    override fun updateStyle() {
        if (screenSettings.isAmbientMode) {
            mHourPaint.color = Color.WHITE
            mMinutePaint.color = Color.WHITE
            mSecondPaint.color = Color.WHITE
            mTickAndCirclePaint.color = Color.WHITE

            mHourPaint.isAntiAlias = false
            mMinutePaint.isAntiAlias = false
            mSecondPaint.isAntiAlias = false
            mTickAndCirclePaint.isAntiAlias = false
        } else {
            mHourPaint.color = mWatchHandColor
            mMinutePaint.color = mWatchHandColor
            mSecondPaint.color = mWatchHandHighlightColor
            mTickAndCirclePaint.color = mWatchHandColor

            mHourPaint.isAntiAlias = true
            mMinutePaint.isAntiAlias = true
            mSecondPaint.isAntiAlias = true
            mTickAndCirclePaint.isAntiAlias = true
        }

        updateShadowLayers()

        mHourPaint.alpha = if (screenSettings.isMuteMode) 100 else 255
        mMinutePaint.alpha = if (screenSettings.isMuteMode) 100 else 255
        mSecondPaint.alpha = if (screenSettings.isMuteMode) 80 else 255
    }

    private fun updateShadowLayers() {
        if (screenSettings.isAmbientMode) {
            mHourPaint.clearShadowLayer()
            mMinutePaint.clearShadowLayer()
            mSecondPaint.clearShadowLayer()
            mTickAndCirclePaint.clearShadowLayer()
        } else {
            mHourPaint.setShadowLayer(
                mShadowRadius, 0f, 0f, mWatchHandShadowColor
            )
            mMinutePaint.setShadowLayer(
                mShadowRadius, 0f, 0f, mWatchHandShadowColor
            )
            mSecondPaint.setShadowLayer(
                mShadowRadius, 0f, 0f, mWatchHandShadowColor
            )
            mTickAndCirclePaint.setShadowLayer(
                mShadowRadius, 0f, 0f, mWatchHandShadowColor
            )
        }
    }

    override fun surfaceChanged(width: Int, height: Int) {
        mCenterX = width / 2f
        mCenterY = height / 2f

        /*
         * Calculate lengths of different hands based on watch screen size.
         */
        mSecondHandLength = (mCenterX * 0.875).toFloat()
        sMinuteHandLength = (mCenterX * 0.75).toFloat()
        sHourHandLength = (mCenterX * 0.5).toFloat()

        mHourPaint.strokeWidth = width * HOUR_STROKE_WIDTH
        mMinutePaint.strokeWidth = width * MINUTE_STROKE_WIDTH
        mSecondPaint.strokeWidth = width * SECOND_TICK_STROKE_WIDTH
        mTickAndCirclePaint.strokeWidth = width * SECOND_TICK_STROKE_WIDTH
        mTickLength = width * TICK_LENGTH
        mShadowRadius = width * SHADOW_RADIUS
        mCenterCircleRadius = width * CENTER_GAP_AND_CIRCLE_RADIUS
        updateShadowLayers()
    }

    override fun scaleImages(width: Int) {
        /* Scale loaded background image (more efficient) if surface dimensions change. */
        val scale = width.toFloat() / mBackgroundBitmap.width.toFloat()

        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
            (mBackgroundBitmap.width * scale).toInt(),
            (mBackgroundBitmap.height * scale).toInt(), true)

        /*
         * Create a gray version of the image only if it will look nice on the device in
         * ambient mode. That means we don"t want devices that support burn-in
         * protection (slight movements in pixels, not great for images going all the way to
         * edges) and low ambient mode (degrades image quality).
         *
         * Also, if your watch face will know about all images ahead of time (users aren"t
         * selecting their own photos for the watch face), it will be more
         * efficient to create a black/white version (png, etc.) and load that when you need it.
         */
        if (!screenSettings.isBurnInProtection && !screenSettings.isLowBitAmbient) {
            initGrayBackgroundBitmap()
        }
    }

    private fun initGrayBackgroundBitmap() {
        mGrayBackgroundBitmap = Bitmap.createBitmap(
            mBackgroundBitmap.width,
            mBackgroundBitmap.height,
            Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mGrayBackgroundBitmap)
        val grayPaint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        grayPaint.colorFilter = filter
        canvas.drawBitmap(mBackgroundBitmap, 0f, 0f, grayPaint)
    }

    override fun drawWatchFace(canvas: Canvas) {
        canvas.apply {
            drawBackground()
            drawHandsAndTicks()
        }
    }

    private fun Canvas.drawBackground() {
        if (screenSettings.isAmbientMode &&
            (screenSettings.isLowBitAmbient || screenSettings.isBurnInProtection)) {
            drawColor(Color.BLACK)
        } else if (screenSettings.isAmbientMode) {
            drawBitmap(mGrayBackgroundBitmap, 0f, 0f, mBackgroundPaint)
        } else {
            drawBitmap(mBackgroundBitmap, 0f, 0f, mBackgroundPaint)
        }
    }

    private fun Canvas.drawHandsAndTicks() {
        /*
         * Draw ticks. Usually you will want to bake this directly into the photo, but in
         * cases where you want to allow users to select their own photos, this dynamically
         * creates them on top of the photo.
         */
        val innerTickRadius = mCenterX - mTickLength
        val outerTickRadius = mCenterX
        for (tickIndex in 0..11) {
            val tickRot = (tickIndex.toDouble() * Math.PI * 2.0 / 12).toFloat()
            val innerX = sin(tickRot) * innerTickRadius
            val innerY = (-cos(tickRot)) * innerTickRadius
            val outerX = sin(tickRot) * outerTickRadius
            val outerY = (-cos(tickRot)) * outerTickRadius
            drawLine(mCenterX + innerX, mCenterY + innerY,
                mCenterX + outerX, mCenterY + outerY, mTickAndCirclePaint)
        }

        /*
         * These calculations reflect the rotation in degrees per unit of time, e.g.,
         * 360 / 60 = 6 and 360 / 12 = 30.
         */
        val seconds =
            currentTime.get(Calendar.SECOND) + currentTime.get(Calendar.MILLISECOND) / 1000f
        val secondsRotation = seconds * 6f

        val minutesRotation = currentTime.get(Calendar.MINUTE) * 6f

        val hourHandOffset = currentTime.get(Calendar.MINUTE) / 2f
        val hoursRotation = currentTime.get(Calendar.HOUR) * 30 + hourHandOffset

        /*
         * Save the canvas state before we can begin to rotate it.
         */
        save()

        rotate(hoursRotation, mCenterX, mCenterY)
        drawLine(
            mCenterX,
            mCenterY - mCenterCircleRadius,
            mCenterX,
            mCenterY - sHourHandLength,
            mHourPaint)

        rotate(minutesRotation - hoursRotation, mCenterX, mCenterY)
        drawLine(
            mCenterX,
            mCenterY - mCenterCircleRadius,
            mCenterX,
            mCenterY - sMinuteHandLength,
            mMinutePaint)

        /*
         * Ensure the "seconds" hand is drawn only when we are in interactive mode.
         * Otherwise, we only update the watch face once a minute.
         */
        if (!screenSettings.isAmbientMode) {
            rotate(secondsRotation - minutesRotation, mCenterX, mCenterY)
            drawLine(
                mCenterX,
                mCenterY - mCenterCircleRadius,
                mCenterX,
                mCenterY - mSecondHandLength,
                mSecondPaint)

        }
        drawCircle(
            mCenterX,
            mCenterY,
            mCenterCircleRadius,
            mTickAndCirclePaint)

        /* Restore the canvas" original orientation. */
        restore()
    }
}
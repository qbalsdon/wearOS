package com.balsdon.harness.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.applyCanvas
import com.balsdon.watchfacerenderer.WatchFaceRenderer
import com.balsdon.watchfacerenderer.WatchScreenSettings
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WatchFaceView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    @Inject
    lateinit var watchFaceRenderer: WatchFaceRenderer

    var currentTime: Long = System.currentTimeMillis()
        set(value) {
            field = value
            invalidate()
        }

    var screenSettings: WatchScreenSettings = watchFaceRenderer.screenSettings
        get() = watchFaceRenderer.screenSettings
        set(value) {
            watchFaceRenderer.screenSettings = value
            field = value
            invalidate()
        }

    var faceMode: WatchFaceMode = WatchFaceMode.Round
        set(value) {
            field = value
            invalidate()
        }

    var size: Int = 1280
        set(value) {
            field = value

            layoutParams = layoutParams.apply {
                height = value
                width = value
            }
            updateDimensions()
            invalidate()
        }

    private var lastWidth = size
    private var lastHeight = size

    private val transparentPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private val blackPaint = Paint().apply {
        color = Color.BLACK
    }

    private val lightGrayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL; color = Color.LTGRAY
    }

    private val darkGrayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL; color = Color.GRAY
    }

    private val checkeredPaint = Paint().apply {
        val tileSize = 20
        shader = BitmapShader(
            Bitmap.createBitmap(
                tileSize * 2,
                tileSize * 2,
                Bitmap.Config.ARGB_8888
            ).apply {
                Canvas(this).apply {
                    for (x in (0..1)) {
                        for (y in (0..1)) {
                            drawRect(
                                x * tileSize.toFloat(),
                                y * tileSize.toFloat(),
                                tileSize.toFloat() * (x + 1),
                                tileSize.toFloat() * (y + 1),
                                if (x == y) lightGrayPaint else darkGrayPaint
                            )
                        }
                    }
                }
            }, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT
        )
        //THANKS: https://stackoverflow.com/a/58471997/932052
    }

    init {
        viewTreeObserver.addOnGlobalLayoutListener {
            updateDimensions()
        }
        watchFaceRenderer.invalidate = ::invalidate
        watchFaceRenderer.initStyle()
    }

    private fun updateDimensions() {
        if (lastWidth != width || lastHeight != height) {
            lastWidth = width
            lastHeight = height
            watchFaceRenderer.surfaceChanged(lastWidth, lastHeight)
            createMaskBitmap()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        watchFaceRenderer.surfaceChanged(lastWidth, lastHeight)
        createMaskBitmap()
        invalidate()
    }

    private lateinit var mask: Bitmap
    private fun createMaskBitmap() {
        mask = Bitmap.createBitmap(
            lastWidth,
            lastHeight,
            Bitmap.Config.ARGB_8888
        ).apply {
            applyCanvas {
                clipOutPath(Path().apply {
                    addCircle(
                        lastWidth.toFloat() / 2F,
                        lastHeight.toFloat() / 2F,
                        lastWidth / 2F,
                        Path.Direction.CCW
                    )
                })
                drawRect(0F, 0F, lastWidth.toFloat(), lastHeight.toFloat(), checkeredPaint)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            watchFaceRenderer.renderWatchFace(canvas, currentTime)
            if (faceMode == WatchFaceMode.Round) {
                canvas.drawBitmap(mask, 0F, 0F, blackPaint)
            }
        }
    }
}
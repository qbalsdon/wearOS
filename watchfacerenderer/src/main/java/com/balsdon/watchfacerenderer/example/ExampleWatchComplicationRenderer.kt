package com.balsdon.watchfacerenderer.example

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import androidx.core.util.forEach
import com.balsdon.watchfacerenderer.WatchComplicationsRenderer

class ExampleWatchComplicationRenderer: WatchComplicationsRenderer() {
    companion object {
        const val LEFT_COMPLICATION_ID = 0
        const val RIGHT_COMPLICATION_ID = 1

        val complicationsList = intArrayOf(
            LEFT_COMPLICATION_ID,
            RIGHT_COMPLICATION_ID
        )
    }

    private val tag: String get() = this.javaClass.simpleName
    override val complicationIdList: IntArray
        get() = complicationsList

    override fun updateStyle() {
        dataSource?.updateStyle(screenSettings)
    }

    override fun surfaceChanged(width: Int, height: Int) {
        val sizeOfComplication = width / 4
        val midpointOfScreen = width / 2

        val horizontalOffset = (midpointOfScreen - sizeOfComplication) / 2
        val verticalOffset = midpointOfScreen - sizeOfComplication / 2

        val leftBounds =  // Left, Top, Right, Bottom
            Rect(
                horizontalOffset,
                verticalOffset,
                horizontalOffset + sizeOfComplication,
                verticalOffset + sizeOfComplication
            )

        val rightBounds =  // Left, Top, Right, Bottom
            Rect(
                midpointOfScreen + horizontalOffset,
                verticalOffset,
                midpointOfScreen + horizontalOffset + sizeOfComplication,
                verticalOffset + sizeOfComplication
            )

        dataSource
            ?.complicationDrawableList
            ?.get(LEFT_COMPLICATION_ID)
            ?.bounds = leftBounds

        dataSource
            ?.complicationDrawableList
            ?.get(RIGHT_COMPLICATION_ID)
            ?.bounds = rightBounds
    }

    override fun drawComplications(canvas: Canvas) {
        if (dataSource == null) Log.e(tag,"$tag has null 'dataSource' reference")

        dataSource?.apply {
            complicationDrawableList.forEach { _, drawable ->
                drawComplication(drawable, canvas, currentTime.timeInMillis)
            }
        }
    }
}
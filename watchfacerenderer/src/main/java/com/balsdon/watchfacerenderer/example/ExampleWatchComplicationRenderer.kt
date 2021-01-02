package com.balsdon.watchfacerenderer.example

import android.content.Context
import android.graphics.Rect
import com.balsdon.watchfacerenderer.WatchComplicationsRenderer

class ExampleWatchComplicationRenderer(context: Context) : WatchComplicationsRenderer(context) {
    companion object {
        const val LEFT_COMPLICATION_ID = 0
        const val RIGHT_COMPLICATION_ID = 1

        val complicationsList = intArrayOf(
            LEFT_COMPLICATION_ID,
            RIGHT_COMPLICATION_ID
        )
    }
    
    override val complicationIdList: IntArray
        get() = complicationsList

    override fun updateStyle() {
        dataSource.updateStyle(screenSettings)
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

        dataSource.complicationDrawableList.apply {
            get(LEFT_COMPLICATION_ID)
                .bounds = leftBounds
            get(RIGHT_COMPLICATION_ID)
                .bounds = rightBounds
        }
    }
}
package com.balsdon.watchapplication

import android.graphics.Canvas
import android.graphics.Rect
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.rendering.ComplicationDrawable
import android.util.SparseArray
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import com.balsdon.watchapplication.activity.ComplicationConfigActivity.ComplicationLocation
import com.balsdon.watchapplication.service.BaseWatchFaceService
import com.balsdon.watchapplication.service.ComplicationController
import com.balsdon.watchfacerenderer.ComplicationRenderer
import dagger.hilt.android.AndroidEntryPoint


/**
 * This class demonstrates the results of performing the COMPLICATIONS CODELAB at
 * https://developer.android.com/codelabs/complications#0 with my proposed
 * architecture - which lifts the burden of managing complications out of the
 * engine
 */

@AndroidEntryPoint
class ComplicatedWatchFaceService : BaseWatchFaceService(), ComplicationController, ComplicationRenderer {

    override var complicationController: ComplicationController? = this

    //region ComplicationController methods
    override val complicationRenderer: ComplicationRenderer = this

    override val activeComplicationDataList: SparseArray<ComplicationData>
            by lazy { SparseArray(complicationsList.size) }

    override val complicationDrawableList: SparseArray<ComplicationDrawable>
            by lazy { SparseArray(complicationsList.size) }

    override val complicationIdList: IntArray
        get() = complicationsList

    override fun initializeComplications() {
        val complicationResource = R.drawable.custom_complication_styles

        val leftComplicationDrawable =
            ContextCompat.getDrawable(this, complicationResource) as ComplicationDrawable
        leftComplicationDrawable.setContext(applicationContext)

        val rightComplicationDrawable =
            ContextCompat.getDrawable(this, complicationResource) as ComplicationDrawable
        rightComplicationDrawable.setContext(applicationContext)

        complicationDrawableList.apply {
            put(LEFT_COMPLICATION_ID, leftComplicationDrawable)
            put(RIGHT_COMPLICATION_ID, rightComplicationDrawable)
        }
    }

    override fun onComplicationDataUpdate(
        watchFaceComplicationId: Int,
        complicationData: ComplicationData?
    ) {
        log("onComplicationDataUpdate: watchFaceComplicationId: [$watchFaceComplicationId] data is null: [${complicationData == null}]")
        // Adds/updates active complication data in the array.
        activeComplicationDataList.put(watchFaceComplicationId, complicationData)

        val drawableElement: ComplicationDrawable =
            complicationDrawableList.get(watchFaceComplicationId)!!

        // Updates correct ComplicationDrawable with updated data
        drawableElement.setComplicationData(complicationData)
    }

    override fun onAmbientModeChanged(inAmbientMode: Boolean) {
        complicationDrawableList.forEach { _, drawable ->
            drawable.setInAmbientMode(inAmbientMode)
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        log("onSurfaceChanged: width:[$width] height: [$height]")
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

        val leftComplicationDrawable: ComplicationDrawable =
            complicationDrawableList.get(LEFT_COMPLICATION_ID)
        leftComplicationDrawable.bounds = leftBounds

        val rightBounds =  // Left, Top, Right, Bottom
            Rect(
                midpointOfScreen + horizontalOffset,
                verticalOffset,
                midpointOfScreen + horizontalOffset + sizeOfComplication,
                verticalOffset + sizeOfComplication
            )

        val rightComplicationDrawable: ComplicationDrawable =
            complicationDrawableList.get(RIGHT_COMPLICATION_ID)
        rightComplicationDrawable.bounds = rightBounds
    }

    override fun updateProperties(isLowBitAmbient: Boolean, isBurnInProtection: Boolean) =
        complicationDrawableList.forEach { _, complicationDrawable ->
            complicationDrawable.setLowBitAmbient(isLowBitAmbient)
            complicationDrawable.setBurnInProtection(isBurnInProtection)
        }
    //endregion

    //region ComplicationRenderer methods
    override fun drawComplications(canvas: Canvas, currentTimeMillis: Long) {
        complicationsList.forEach { index ->
            log("drawComplications index: $index size ${complicationDrawableList.size()} bounds: [${complicationDrawableList[index].bounds}]")
            complicationDrawableList[index].draw(canvas, currentTimeMillis)
        }
    }
    //endregion

    companion object {
        private const val LEFT_COMPLICATION_ID = 0
        private const val RIGHT_COMPLICATION_ID = 1

        val complicationsList = intArrayOf(
            LEFT_COMPLICATION_ID,
            RIGHT_COMPLICATION_ID
        )

        private val supportedComplications = arrayOf(
            intArrayOf(
                ComplicationData.TYPE_RANGED_VALUE,
                ComplicationData.TYPE_ICON,
                ComplicationData.TYPE_SHORT_TEXT,
                ComplicationData.TYPE_SMALL_IMAGE
            ), intArrayOf(
                ComplicationData.TYPE_RANGED_VALUE,
                ComplicationData.TYPE_ICON,
                ComplicationData.TYPE_SHORT_TEXT,
                ComplicationData.TYPE_SMALL_IMAGE
            )
        )

        fun getComplicationId(
            complicationLocation: ComplicationLocation?
        ): Int {
            return when (complicationLocation) {
                ComplicationLocation.LEFT -> LEFT_COMPLICATION_ID
                ComplicationLocation.RIGHT -> RIGHT_COMPLICATION_ID
                else -> -1
            }
        }

        fun getSupportedComplicationTypes(
            complicationLocation: ComplicationLocation?
        ): IntArray {
            return when (complicationLocation) {
                ComplicationLocation.LEFT -> supportedComplications[0]
                ComplicationLocation.RIGHT -> supportedComplications[1]
                else -> intArrayOf()
            }
        }
    }
}
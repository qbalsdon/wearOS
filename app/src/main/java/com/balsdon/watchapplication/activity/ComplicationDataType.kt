package com.balsdon.watchapplication.activity

import android.support.wearable.complications.ComplicationData

class ComplicationDataType(val id: Int, val supportedTypes: IntArray = supportSmallComplications){
    companion object {
        val supportSmallComplications = intArrayOf(
            ComplicationData.TYPE_RANGED_VALUE,
            ComplicationData.TYPE_ICON,
            ComplicationData.TYPE_SHORT_TEXT,
            ComplicationData.TYPE_SMALL_IMAGE
        )
    }
}
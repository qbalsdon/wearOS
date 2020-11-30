package com.balsdon.watchfacerenderer

import android.graphics.Canvas

interface ComplicationRenderer {
    fun drawComplications(canvas: Canvas, currentTimeMillis: Long)
}
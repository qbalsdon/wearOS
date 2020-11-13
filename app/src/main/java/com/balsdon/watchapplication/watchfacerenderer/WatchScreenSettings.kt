package com.balsdon.watchapplication.watchfacerenderer

data class WatchScreenSettings(var isAmbientMode: Boolean = false,
                               var isMuteMode: Boolean = false,
                               var isLowBitAmbient: Boolean = false,
                               var isBurnInProtection: Boolean = false)
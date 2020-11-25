package com.balsdon.harness.ui.activity

interface TimeTicker {
    fun stopTick()
    fun setTickSpeed(speed: Int)
    fun setHandler(handler: TimeHandler?)
}
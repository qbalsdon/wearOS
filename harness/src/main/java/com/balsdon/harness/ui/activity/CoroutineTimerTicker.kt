package com.balsdon.harness.ui.activity

import kotlinx.coroutines.*

class CoroutineTimerTicker(private val scope: CoroutineScope): TimeTicker {
    companion object {
        private const val DEFAULT_TICK_SPEED = 1
    }

    private var timeHandler: TimeHandler? = null
    private var tickJob: Job = Job()
    private var tickSpeed: Int = DEFAULT_TICK_SPEED

    override fun setTickSpeed(speed: Int) {
        tickJob.cancel()
        tickSpeed = speed
        if (tickSpeed > 0) {
            startTick()
        }
    }

    override fun setHandler(handler: TimeHandler?) {
        timeHandler = handler
    }

    override fun stopTick() =
        tickJob.cancel()

    private fun startTick() {
        tickJob = scope.launch {
            while (isActive) {
                timeHandler?.increaseTime()
                delay(1000L / tickSpeed)
            }
        }
    }
}
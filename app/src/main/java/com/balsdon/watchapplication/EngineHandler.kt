package com.balsdon.watchapplication

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * Simplified coroutine-based EngineHandler. I removed the deprecated Handler reference
 *
 * It should probably have a message queue
 */

class EngineHandler(reference: MyWatchFace.Engine) : MessageReceiver {

    private val weakReference: WeakReference<MyWatchFace.Engine> = WeakReference(reference)

    override fun sendEmptyMessageDelayed(messageCode: Int, delayMs: Long) {
        val engine = weakReference.get()
        if (engine != null) {
            MainScope().launch {
                delay(delayMs)
                sendEmptyMessage(messageCode)
            }
        }
    }

    override fun sendEmptyMessage(messageCode: Int) {
        val engine = weakReference.get()
        if (engine != null) {
            when (messageCode) {
                MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
            }
        }
    }

    companion object {
        /**
         * Message id for updating the time periodically in interactive mode.
         */
        const val MSG_UPDATE_TIME = 0
    }
}
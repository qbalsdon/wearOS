package com.balsdon.watchapplication.service

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference

/**
 * https://developer.android.com/reference/kotlin/android/os/Handler
 * There are two main uses for a Handler:
 *     (1) to schedule messages and runnables to be executed at
 *         some point in the future; and
 *     (2) to enqueue an action to be performed on a different
 *         thread than your own.
 */
class InteractiveTimeUpdateHandler<T : Updateable>(reference: T) : Handler(Looper.getMainLooper()) {
    private val mWeakReference: WeakReference<T> = WeakReference(reference)

    companion object {
        /**
         * Message id for updating the time periodically in interactive mode.
         */
        const val MSG_UPDATE_TIME = 0
    }

    override fun handleMessage(msg: Message) {
        mWeakReference.get()?.let {
            when (msg.what) {
                MSG_UPDATE_TIME -> it.update()
            }
        }
    }
}
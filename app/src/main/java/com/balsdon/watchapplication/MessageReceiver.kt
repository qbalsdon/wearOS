package com.balsdon.watchapplication


/**
 * This interface behaves in the manner expected by the example.
 * It used the deprecated Handler, class, but it's really just a
 * notifier
 */
interface MessageReceiver {
    fun removeMessages(messageCode: Int) = Unit
    fun sendEmptyMessage(messageCode: Int)
    fun sendEmptyMessageDelayed(messageCode: Int, delayMs: Long)
}
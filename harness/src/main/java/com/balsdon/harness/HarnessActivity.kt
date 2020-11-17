package com.balsdon.harness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import com.balsdon.harness.ui.control.ControlFragment
import com.balsdon.harness.ui.watch.WatchFragment
import kotlinx.android.synthetic.main.harness_activity.*

class HarnessActivity : AppCompatActivity() {

    private val isCondensed: Boolean by lazy {
        watchContainer.tag as String == getString(R.string.layout_condensed)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.harness_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.controlContainer, ControlFragment.newInstance())
                .commitNow()
            supportFragmentManager.beginTransaction()
                .replace(R.id.watchContainer, WatchFragment.newInstance())
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isCondensed) {
            menuInflater.inflate(R.menu.menu, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
            R.id.settings_item -> {
                motionLayout?.apply {
                    if (currentState == R.id.end) {
                        transitionToStart()
                    } else {
                        transitionToEnd()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
    }
}
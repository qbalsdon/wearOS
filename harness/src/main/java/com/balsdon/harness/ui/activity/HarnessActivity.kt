package com.balsdon.harness.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.balsdon.harness.R
import com.balsdon.harness.ui.fragment.ControlFragment
import com.balsdon.harness.ui.fragment.WatchFragment
import com.balsdon.harness.ui.viewmodel.HarnessViewModel
import com.balsdon.harness.ui.viewmodel.HarnessViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.harness_activity.*
import kotlinx.coroutines.MainScope
import java.util.*

@AndroidEntryPoint
class HarnessActivity : AppCompatActivity(), TimeHandler {
    private val isCondensed: Boolean by lazy {
        watchContainer.tag as String == getString(R.string.layout_condensed)
    }

    private lateinit var viewModel: HarnessViewModel
    //TODO: Inject the coroutineTimerTicker - not sure I like the dependency here
    private val ticker: TimeTicker = CoroutineTimerTicker(MainScope())

    private val settingsObserver = Observer<HarnessViewModel.WatchDisplaySettings> { newSettings ->
        run {
            settingsUpdated(newSettings)
        }
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

        viewModel = ViewModelProvider(this, HarnessViewModelFactory()).get(HarnessViewModel::class.java)
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

    override fun onResume() {
        super.onResume()
        viewModel.data.observe(this, settingsObserver)
        ticker.setHandler(this)
    }

    override fun onPause() {
        viewModel.data.removeObserver(settingsObserver)
        ticker.setHandler(null)
        super.onPause()
    }

    private fun settingsUpdated(settings: HarnessViewModel.WatchDisplaySettings) {
        if (settings.isAnimateTimeToggle) {
            ticker.setTickSpeed(settings.speed)
        } else {
            ticker.stopTick()
        }
    }

    override fun increaseTime() {
        viewModel.time = Calendar.getInstance().apply {
            timeInMillis = viewModel.time
            add(Calendar.MILLISECOND, 1000)
        }.timeInMillis
    }
}
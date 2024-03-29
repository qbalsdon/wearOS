package com.balsdon.harness.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.balsdon.harness.R
import com.balsdon.harness.databinding.ActivityHarnessBinding
import com.balsdon.harness.ui.fragment.ControlFragment
import com.balsdon.harness.ui.fragment.WatchFragment
import com.balsdon.harness.ui.viewmodel.HarnessViewModel
import com.balsdon.harness.ui.viewmodel.HarnessViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HarnessActivity : AppCompatActivity(), TimeHandler {
    private val isCondensed: Boolean by lazy {
        binding.watchContainer.tag as String == getString(R.string.layout_condensed)
    }

    @Inject
    lateinit var ticker: TimeTicker

    private lateinit var viewModel: HarnessViewModel
    private lateinit var binding: ActivityHarnessBinding

    private val settingsObserver = Observer<HarnessViewModel.WatchDisplaySettings> { newSettings ->
        run {
            settingsUpdated(newSettings)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHarnessBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
                binding.motionLayout?.apply {
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
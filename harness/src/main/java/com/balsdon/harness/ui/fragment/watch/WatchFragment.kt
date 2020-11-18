package com.balsdon.harness.ui.fragment.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.balsdon.harness.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchFragment : Fragment() {

    companion object {
        fun newInstance() = WatchFragment()
    }

    private lateinit var viewModel: WatchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_watch, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WatchViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
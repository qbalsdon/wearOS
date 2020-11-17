package com.balsdon.harness.ui.control

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.balsdon.harness.R
import com.balsdon.harness.ui.watch.WatchViewModel

class ControlFragment : Fragment() {

    companion object {
        fun newInstance() = ControlFragment()
    }

    private lateinit var viewModel: ControlViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.control_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ControlViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
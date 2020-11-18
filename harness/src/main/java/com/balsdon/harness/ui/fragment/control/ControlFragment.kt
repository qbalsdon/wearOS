package com.balsdon.harness.ui.fragment.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.balsdon.harness.R

class ControlFragment : Fragment() {

    companion object {
        fun newInstance() = ControlFragment()
    }

    private lateinit var viewModel: ControlViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_control, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ControlViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
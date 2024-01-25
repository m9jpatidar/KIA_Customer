package com.knowitall.customer.ui.fragment.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.knowitall.customer.base.BaseFragment
import com.knowitall.customer.databinding.FragmentRequestBinding

class RequestFragment: BaseFragment() {

    private lateinit var binding: FragmentRequestBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root
    }
}
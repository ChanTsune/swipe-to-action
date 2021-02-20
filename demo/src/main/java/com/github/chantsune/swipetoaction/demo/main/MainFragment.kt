package com.github.chantsune.swipetoaction.demo.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.github.chantsune.swipetoaction.demo.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.goToCustomButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToCustomSwipeLayoutFragment())
        }
        binding.goToSimpleButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToSimpleSwipeLayoutFragment())
        }
        binding.goToGridButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToGridLayoutFragment())
        }
    }

}
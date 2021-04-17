package com.github.chantsune.swipetoaction.demo.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
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
        viewModel = ViewModelProvider(this).get()

        binding.goToSimpleButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToSimpleSwipeLayoutFragment())
        }
        binding.goToCustomButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToCustomSwipeLayoutFragment())
        }
        binding.goToGridButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToGridLayoutFragment())
        }
    }

}

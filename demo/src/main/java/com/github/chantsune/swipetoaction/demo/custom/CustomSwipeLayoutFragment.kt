package com.github.chantsune.swipetoaction.demo.custom

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.chantsune.swipetoaction.demo.databinding.FragmentCustomSwipeLayoutBinding

class CustomSwipeLayoutFragment : Fragment() {

    private lateinit var binding: FragmentCustomSwipeLayoutBinding
    private lateinit var viewModel: CustomSwipeLayoutViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomSwipeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CustomSwipeLayoutViewModel::class.java)
        binding.recyclerView.apply {
            adapter = RecyclerAdapter()
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

}
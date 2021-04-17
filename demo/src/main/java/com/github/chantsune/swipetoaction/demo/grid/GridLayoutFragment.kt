package com.github.chantsune.swipetoaction.demo.grid

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.GridLayoutManager
import com.github.chantsune.swipetoaction.demo.base.BaseListFragment
import com.github.chantsune.swipetoaction.demo.simple.RecyclerAdapter

class GridLayoutFragment : BaseListFragment() {

    private lateinit var viewModel: GridLayoutViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
        binding.recyclerView.apply {
            adapter = RecyclerAdapter()
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position in 0..3) {
                            2
                        } else {
                            1
                        }
                    }
                }
            }
        }
    }

}
package com.github.chantsune.swipetoaction.demo.grid

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.get
import androidx.recyclerview.widget.GridLayoutManager
import com.github.chantsune.swipetoaction.demo.R
import com.github.chantsune.swipetoaction.demo.base.BaseListFragment
import com.github.chantsune.swipetoaction.demo.simple.RecyclerAdapter

class GridLayoutFragment : BaseListFragment() {

    private lateinit var viewModel: GridLayoutViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
        binding.recyclerView.apply {
            adapter = RecyclerAdapter()
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

}
package com.github.chantsune.swipetoaction.demo.custom

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.chantsune.swipetoaction.demo.base.BaseListFragment

class CustomSwipeLayoutFragment : BaseListFragment() {

    private lateinit var viewModel: CustomSwipeLayoutViewModel

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
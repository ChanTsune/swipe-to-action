package com.github.chantsune.swipetoaction.demo.custom

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.chantsune.swipetoaction.demo.base.BaseListFragment

class CustomSwipeLayoutFragment : BaseListFragment() {

    private lateinit var viewModel: CustomSwipeLayoutViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.recyclerView.apply {
            adapter = RecyclerAdapter()
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            setHasFixedSize(true)
        }
    }

}
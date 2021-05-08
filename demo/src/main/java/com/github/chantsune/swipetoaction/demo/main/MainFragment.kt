package com.github.chantsune.swipetoaction.demo.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.chantsune.swipetoaction.demo.R
import com.github.chantsune.swipetoaction.demo.base.BaseListFragment

class MainFragment : BaseListFragment() {

    private lateinit var viewModel: MainViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.recyclerView.apply {
            adapter = object : MainListAdapter(listOf(
                R.string.simple_swipe_layout,
                R.string.custom_swipe_layout,
                R.string.mail_app_sample,
            ).map { getString(it) }) {
                override fun onItemClick(item: String, position: Int) {
                    findNavController().navigate(
                        when (position) {
                            0 -> MainFragmentDirections.actionMainFragmentToSimpleSwipeLayoutFragment()
                            1 -> MainFragmentDirections.actionMainFragmentToCustomSwipeLayoutFragment()
                            else -> MainFragmentDirections.actionMainFragmentToMailLayoutFragment()
                        }
                    )
                }
            }
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

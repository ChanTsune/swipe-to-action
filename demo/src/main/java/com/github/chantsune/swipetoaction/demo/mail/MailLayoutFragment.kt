package com.github.chantsune.swipetoaction.demo.mail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.core.view.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.R
import com.github.chantsune.swipetoaction.demo.base.BaseListFragment
import com.github.chantsune.swipetoaction.views.SwipeLayout

class MailLayoutFragment : BaseListFragment() {

    private lateinit var viewModel: MailLayoutViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.postDelayed(200) {
                viewModel.refresh()
                binding.swipeRefreshLayout.canChildScrollUp()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        binding.recyclerView.apply {
            adapter = object : MailAdapter(viewModel.mails.value ?: listOf()) {
                override fun onItemSwipeItemClicked(
                    swipeLayout: SwipeLayout,
                    view: View,
                    isLeft: Boolean,
                    index: Int,
                    position: Int
                ) {
                    if (isLeft) {
                        updateItemIsOpen(swipeLayout, position)
                    } else {
                        when (index) {
                            0 -> openGitHubRepo()
                            1 -> updateItemFlag(swipeLayout, position)
                            2 -> deleteItem(position)
                        }
                    }
                }

                private fun updateItemIsOpen(swipeLayout: SwipeLayout, position: Int) {
                    collapseItemAndUpdateView(swipeLayout, position)
                    val item = items[position]
                    item.isOpened = !item.isOpened
                }

                private fun updateItemFlag(swipeLayout: SwipeLayout, position: Int) {
                    collapseItemAndUpdateView(swipeLayout, position)
                    val item = items[position]
                    item.flag = !item.flag
                }

                private fun collapseItemAndUpdateView(swipeLayout: SwipeLayout, position: Int) {
                    swipeLayout.setItemState(
                        SwipeLayout.ITEM_STATE_COLLAPSED,
                        true,
                        object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {}

                            override fun onAnimationEnd(animation: Animation?) {
                                notifyItemChanged(position)
                            }

                            override fun onAnimationRepeat(animation: Animation?) {}
                        }
                    )
                }

                private fun deleteItem(position: Int) {
                    val list = items.toMutableList().apply {
                        removeAt(position)
                    }
                    notifyItemRemoved(position)
                    viewModel.mails.postValue(list)
                }
            }
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        viewModel.mails.observe(viewLifecycleOwner) {
            (binding.recyclerView.adapter as? MailAdapter)?.items = it
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }
    private fun openGitHubRepo() {
        val url = Uri.parse(getString(R.string.github_repo_url))
        startActivity(Intent(Intent.ACTION_VIEW, url))
    }
}

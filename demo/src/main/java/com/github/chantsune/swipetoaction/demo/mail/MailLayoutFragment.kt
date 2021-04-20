package com.github.chantsune.swipetoaction.demo.mail

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.base.BaseListFragment
import com.github.chantsune.swipetoaction.views.SwipeLayout

class MailLayoutFragment : BaseListFragment() {

    private lateinit var viewModel: MailLayoutViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
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
                            0 -> {}
                            1 -> {}
                            2 -> deleteItem(position)
                        }
                    }
                }
                private fun updateItemIsOpen(swipeLayout: SwipeLayout, position: Int) {
                    swipeLayout.setItemState(
                        SwipeLayout.ITEM_STATE_COLLAPSED,
                        true,
                        object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) { }

                            override fun onAnimationEnd(animation: Animation?) {
                                notifyItemChanged(position)
                            }

                            override fun onAnimationRepeat(animation: Animation?) { }
                        }
                    )
                    val item = items[position]
                    item.isOpened = !item.isOpened
                }
                private fun deleteItem(position:Int) {
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

}
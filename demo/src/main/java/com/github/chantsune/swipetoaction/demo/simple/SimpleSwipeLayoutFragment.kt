package com.github.chantsune.swipetoaction.demo.simple

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.chantsune.swipetoaction.demo.base.BaseListFragment
import com.github.chantsune.swipetoaction.views.SwipeLayout

class SimpleSwipeLayoutFragment : BaseListFragment() {

    private lateinit var viewModel: SimpleSwipeLayoutViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
        binding.recyclerView.also { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = RecyclerAdapter()
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
//        val swipeLayout: SwipeLayout = findViewById<View>(R.id.swipe_layout) as SwipeLayout
//        swipeLayout.setOnSwipeItemClickListener(object : SwipeLayout.OnSwipeItemClickListener {
//            override fun onSwipeItemClick(left: Boolean, index: Int) {
//                if (left) {
//                    when (index) {
//                        0 -> showToast("REFRESH")
//                    }
//                } else {
//                    when (index) {
//                        0 -> showToast("REFRESH")
//                        1 -> showToast("SETTINGS")
//                        2 -> showToast("TRASH")
//                    }
//                }
//            }
//        })
    }


    fun collapse(view: View?) {
        (binding.recyclerView.findViewHolderForAdapterPosition(0)!!.itemView as SwipeLayout).setItemState(
            SwipeLayout.ITEM_STATE_COLLAPSED,
            true
        )
    }

}

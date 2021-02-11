package com.github.chantsune.swipetoaction.demo.fragments.simple

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.chantsune.swipetoaction.demo.R
import com.github.chantsune.swipetoaction.demo.RecyclerAdapter
import com.github.chantsune.swipetoaction.demo.databinding.FragmentSimpleSwipeLayoutBinding
import com.github.chantsune.swipetoaction.views.SwipeLayout

class SimpleSwipeLayoutFragment : Fragment() {

    private lateinit var binding: FragmentSimpleSwipeLayoutBinding
    private lateinit var viewModel: SimpleSwipeLayoutViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSimpleSwipeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SimpleSwipeLayoutViewModel::class.java)
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

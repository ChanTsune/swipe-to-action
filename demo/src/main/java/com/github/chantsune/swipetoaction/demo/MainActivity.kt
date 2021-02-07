package com.github.chantsune.swipetoaction.demo

import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.adapter.SwipeToActionAdapter
import com.github.chantsune.swipetoaction.adapter.SwipeToActionViewHolder
import com.github.chantsune.swipetoaction.demo.databinding.LeftSwipeItemBinding
import com.github.chantsune.swipetoaction.demo.databinding.RightSwipeItemBinding
import com.github.chantsune.swipetoaction.demo.databinding.ViewListItemBinding
import com.github.chantsune.swipetoaction.views.SwipeLayout
import com.google.android.material.button.MaterialButton
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var adapter: Adapter

    private var count = 0

    override fun onStart() {
        super.onStart()
        setupNewAdapter()
        findViewById<MaterialButton>(R.id.button).setOnClickListener {
            switchAdapter()
        }
    }

    private fun setupOldAdapter() {
        findViewById<RecyclerView>(R.id.list_view).also { listView ->
            listView.adapter = OldAdapter()
            listView.layoutManager = LinearLayoutManager(this)
        }
    }
    private fun setupNewAdapter() {
        findViewById<RecyclerView>(R.id.list_view).also { listView ->
            listView.adapter = Adapter().also {
                adapter = it
            }
            listView.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun switchAdapter() {
        adapter.items.add("$count")
        adapter.notifyDataSetChanged()
//        if (count % 2 == 0) {
//            setupOldAdapter()
//        } else {
//            setupNewAdapter()
//        }
        count++
    }

    class Adapter : SwipeToActionAdapter() {
        private lateinit var binding: ViewListItemBinding

        var items: MutableList<String> = mutableListOf()

        override fun onCreateItemView(parent: ViewGroup, viewType: Int): View {
            binding = ViewListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return binding.root
        }

        override fun onCreateRightSwipeItemView(parent: ViewGroup): View {
            val binding = RightSwipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return  binding.root
        }

        override fun onCreateLeftSwipeItemView(parent: ViewGroup): View {
            val binding = LeftSwipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return binding.root
        }

        override fun onBindItemView(holder: SwipeToActionViewHolder, position: Int) {
            val item = items.getOrNull(position) ?: return
            binding.textView.text = item
        }

        override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean, position: Int) {
            Toast.makeText(swipeLayout.context, "onBeginSwipe at $position", Toast.LENGTH_SHORT).show()
        }

        override fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean, position: Int) {
            Toast.makeText(swipeLayout.context, "onSwipeClampReached $position", Toast.LENGTH_SHORT).show()
        }

        override fun onLeftStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean, position: Int) {
            Toast.makeText(swipeLayout.context, "onLeftStickyEdge $position", Toast.LENGTH_SHORT).show()
        }

        override fun onRightStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean, position: Int) {
            Toast.makeText(swipeLayout.context, "onRightStickyEdge $position", Toast.LENGTH_SHORT).show()
        }
        override fun getItemCount(): Int = items.size
    }
}

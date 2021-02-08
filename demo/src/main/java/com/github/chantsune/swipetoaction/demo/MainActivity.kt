package com.github.chantsune.swipetoaction.demo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.chantsune.swipetoaction.demo.databinding.ActivityMainBinding
import com.github.chantsune.swipetoaction.views.SwipeLayout

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.also { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = RecyclerAdapter()
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    this,
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showToast(toast: String) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }

    fun collapse(view: View?) {
        (binding.recyclerView.findViewHolderForAdapterPosition(0)!!.itemView as SwipeLayout).setItemState(
            SwipeLayout.ITEM_STATE_COLLAPSED,
            true
        )
    }

}

package com.github.chantsune.swipetoaction.demo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.views.SwipeLayout

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = RecyclerAdapter()
        recyclerView!!.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
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

    private fun showToast(toast: String) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }

    fun collapse(view: View?) {
        (recyclerView!!.findViewHolderForAdapterPosition(0)!!.itemView as SwipeLayout).setItemState(
            SwipeLayout.ITEM_STATE_COLLAPSED,
            true
        )
    }

}

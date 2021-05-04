package com.github.chantsune.swipetoaction.demo.mail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.databinding.ViewMailListItemBinding
import com.github.chantsune.swipetoaction.demo.mail.model.Mail
import com.github.chantsune.swipetoaction.views.SwipeLayout

abstract class MailAdapter : PagingDataAdapter<Mail, MailAdapter.ViewHolder>(DIFF_CALL_BACK) {
    class ViewHolder(
        val binding: ViewMailListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewMailListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(holder.absoluteAdapterPosition) ?: return
        binding.notificationIcon.visibility = if (item.isOpened) View.INVISIBLE else View.VISIBLE
        binding.senderName.text = item.sender
        binding.receivedTime.text = item.date
        binding.title.text = item.title
        binding.flag.visibility = if (item.flag) View.VISIBLE else View.INVISIBLE
        binding.body.text = item.body
        binding.root.setOnSwipeItemClickListener { view, left, index ->
            onItemSwipeItemClicked(binding.root, view, left, index, holder.absoluteAdapterPosition)
        }
    }

    abstract fun onItemSwipeItemClicked(swipeLayout: SwipeLayout, view: View, isLeft:Boolean, index: Int, position: Int)

    companion object {
        private val DIFF_CALL_BACK = object : DiffUtil.ItemCallback<Mail>() {
            override fun areItemsTheSame(oldItem: Mail, newItem: Mail): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Mail, newItem: Mail): Boolean {
                return oldItem == newItem
            }
        }
    }
}

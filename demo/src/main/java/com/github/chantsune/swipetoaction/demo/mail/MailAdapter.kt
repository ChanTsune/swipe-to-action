package com.github.chantsune.swipetoaction.demo.mail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.R
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
        binding.root.getSwipeItemAt(0, true).apply {
            icon = if (item.isOpened) R.drawable.ic_baseline_mark_email_unread_24
            else R.drawable.ic_baseline_email_24
        }
        binding.root.setOnSwipeItemClickListener { swipeItem, index ->
            onItemSwipeItemClicked(binding.root, swipeItem, index, holder.absoluteAdapterPosition)
        }
    }

    abstract fun onItemSwipeItemClicked(swipeLayout: SwipeLayout, swipeItem: SwipeLayout.SwipeItem, index: Int, position: Int)

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

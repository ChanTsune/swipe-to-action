package com.github.chantsune.swipetoaction.demo.mail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.databinding.ViewMailListItemBinding
import com.github.chantsune.swipetoaction.demo.mail.model.Mail
import com.github.chantsune.swipetoaction.views.SwipeLayout

abstract class MailAdapter(
    var items: List<Mail>
) : RecyclerView.Adapter<MailAdapter.ViewHolder>() {
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
        val item = items[holder.adapterPosition]
        binding.notificationIcon.visibility = if (item.isOpened) View.INVISIBLE else View.VISIBLE
        binding.senderName.text = item.sender
        binding.receivedTime.text = item.date
        binding.title.text = item.title
        binding.body.text = item.body
        binding.root.setOnSwipeItemClickListener { view, left, index ->
            onItemSwipeItemClicked(binding.root, view, left, index, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = items.size

    abstract fun onItemSwipeItemClicked(swipeLayout: SwipeLayout, view: View, isLeft:Boolean, index: Int, position: Int)
}

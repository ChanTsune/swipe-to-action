package com.github.chantsune.swipetoaction.demo.mail

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.base.BaseListFragment

class MailLayoutFragment : BaseListFragment() {

    private lateinit var viewModel: MailLayoutViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
        binding.recyclerView.apply {
            adapter = MailAdapter(viewModel.mails.value ?: listOf())
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        viewModel.mails.observe(viewLifecycleOwner) {
            (binding.recyclerView.adapter as? MailAdapter)?.items = it
        }
    }

}
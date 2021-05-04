package com.github.chantsune.swipetoaction.demo.mail

import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.github.chantsune.swipetoaction.demo.mail.model.Mail
import com.github.chantsune.swipetoaction.demo.mail.paging.MailDataSource
import kotlinx.coroutines.flow.Flow

class MailLayoutViewModel(
    val mailList: Flow<PagingData<Mail>> = Pager(
        PagingConfig(10),
        0) {
        MailDataSource()
    }.flow
) : ViewModel() {

    suspend fun remove(mail: Mail) {
        MailDataSource.remove(mail)
    }
}

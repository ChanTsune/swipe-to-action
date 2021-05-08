package com.github.chantsune.swipetoaction.demo.mail

import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.github.chantsune.swipetoaction.demo.mail.entity.Mail
import com.github.chantsune.swipetoaction.demo.mail.paging.MailDataSource
import com.github.chantsune.swipetoaction.demo.mail.repository.IMailRepository
import com.github.chantsune.swipetoaction.demo.mail.repository.MailRepository
import kotlinx.coroutines.flow.Flow

class MailLayoutViewModel(
    private val repository: IMailRepository = MailRepository(),
    val mailList: Flow<PagingData<Mail>> = Pager(
        PagingConfig(10),
        0) {
        MailDataSource(repository)
    }.flow
) : ViewModel() {

    suspend fun remove(mail: Mail) {
        repository.delete(mail)
    }
    suspend fun update(mail: Mail) {
        repository.update(mail)
    }
}

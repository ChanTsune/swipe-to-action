package com.github.chantsune.swipetoaction.demo.mail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.chantsune.swipetoaction.demo.mail.model.Mail

class MailLayoutViewModel(
    var mails: MutableLiveData<List<Mail>> = MutableLiveData(listOf(
        Mail("Apple Store", "Title", "Body ".repeat(10), "18:58", false, false),
        Mail("Apple Store", "Title", "Body ".repeat(20), "17:32", false, false),
        Mail("Apple Store", "Title", "Body ".repeat(30), "15:43", true, true),
    ))
) : ViewModel() {
    fun refresh() {
        mails.value?.toMutableList()?.let { mailList ->
            mailList.add(0, Mail("Apple Store", "Title", "Body ".repeat(30), "date", false, false))
            mails.postValue(mailList)
        }
    }
}


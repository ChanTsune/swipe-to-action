package com.github.chantsune.swipetoaction.demo.mail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.chantsune.swipetoaction.demo.mail.model.Mail

class MailLayoutViewModel(
    var mails: MutableLiveData<List<Mail>> = MutableLiveData(listOf(
        Mail("Apple Store", "Title", "Body ".repeat(10), "18:58", false),
        Mail("Apple Store", "Title", "Body ".repeat(20), "17:32", false),
        Mail("Apple Store", "Title", "Body ".repeat(30), "15:43", true),
    ))
) : ViewModel()


package com.github.chantsune.swipetoaction.demo.mail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.chantsune.swipetoaction.demo.mail.model.Mail

class MailLayoutViewModel(
    var mails: LiveData<List<Mail>> = liveData {
        emit(listOf(
            Mail("Apple Store", "Title", "Body ".repeat(10), "18:58", false),
            Mail("Apple Store", "Title", "Body ".repeat(20), "17:32", false),
            Mail("Apple Store", "Title", "Body ".repeat(30), "15:43", true),
        ))
    }
) : ViewModel()

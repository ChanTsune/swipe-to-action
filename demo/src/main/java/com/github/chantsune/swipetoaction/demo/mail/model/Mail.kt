package com.github.chantsune.swipetoaction.demo.mail.model

data class Mail(
    val sender: String,
    val title: String,
    val body: String,
    val date: String,
    var isOpened: Boolean,
    var flag: Boolean
)

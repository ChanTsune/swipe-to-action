package com.github.chantsune.swipetoaction.demo.mail.entity

data class Mail(
    val id: Int,
    val sender: String,
    val title: String,
    val body: String,
    val date: String,
    var isOpened: Boolean,
    var flag: Boolean
)

package com.github.chantsune.swipetoaction.demo.mail.repository

import com.github.chantsune.swipetoaction.demo.mail.model.Mail

interface IMailRepository {
    suspend fun getList(page: Int, size: Int): List<Mail>
    suspend fun delete(mail: Mail)
    suspend fun update(mail: Mail)
}

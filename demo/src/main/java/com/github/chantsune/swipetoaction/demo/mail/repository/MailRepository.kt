package com.github.chantsune.swipetoaction.demo.mail.repository

import com.github.chantsune.swipetoaction.demo.mail.entity.Mail

class MailRepository : IMailRepository {

    private val mailList: MutableList<Mail> = MutableList(50) {
        when (it) {
            0 -> Mail(it, "Apple Store", "Title", "Body ".repeat(10), "18:58", false, false)
            1 -> Mail(it, "Apple Store", "Title", "Body ".repeat(20), "17:32", false, false)
            2 -> Mail(it, "Apple Store", "Title", "Body ".repeat(30), "15:43", true, true)
            else -> Mail(it, "Apple Store", "Title", "Body ".repeat(30), "date", false, false)
        }
    }

    override suspend fun getList(page: Int, size: Int): List<Mail> {
        return mailList.subList(
            (page * size).coerceAtMost(mailList.size - 1),
            ((page + 1) * size).coerceAtMost(mailList.size - 1),
        ).toList()
    }

    override suspend fun delete(mail: Mail) {
        mailList.removeAll {
            it.id == mail.id
        }
    }

    override suspend fun update(mail: Mail) {
        when (val idx = mailList.indexOfFirst { it.id == mail.id }) {
            -1 -> {
            }
            else -> {
                mailList[idx] = mail
            }
        }
    }
}

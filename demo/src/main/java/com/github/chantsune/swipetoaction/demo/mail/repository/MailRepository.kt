package com.github.chantsune.swipetoaction.demo.mail.repository

import com.github.chantsune.swipetoaction.demo.mail.entity.Mail

class MailRepository : IMailRepository {

    private val mailList: MutableList<Mail> = MutableList(50) {
        createEmail(it)
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

    companion object {
        fun createEmail(id: Int): Mail {
            return Mail(id, "Apple Store", "Title", "Body ".repeat(id + 1), "18:%02d".format(id), false, false)
        }
    }
}

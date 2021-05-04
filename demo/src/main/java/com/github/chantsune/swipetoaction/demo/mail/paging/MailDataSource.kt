package com.github.chantsune.swipetoaction.demo.mail.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.chantsune.swipetoaction.demo.mail.model.Mail

class MailDataSource : PagingSource<Int, Mail>() {

    init {
        id = 0
    }

    override fun getRefreshKey(state: PagingState<Int, Mail>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Mail> {
        return when (val key = params.key) {
            null -> LoadResult.Error(Error())
            0 -> LoadResult.Page(
                listOf(
                    Mail(++id, "Apple Store", "Title", "Body ".repeat(10), "18:58", false, false),
                    Mail(++id, "Apple Store", "Title", "Body ".repeat(20), "17:32", false, false),
                    Mail(++id, "Apple Store", "Title", "Body ".repeat(30), "15:43", true, true),
                ).filter { it.id !in removedMail },
                null, key + 1,
            )
            else -> LoadResult.Page(
                List(15) {
                    Mail(++id, "Apple Store", "Title", "Body ".repeat(30), "date", false, false)
                }.filter { it.id !in removedMail },
                key - 1,
                key + 1,
            )
        }
    }

    companion object {
        private var id = 0
        private val removedMail: MutableList<Int> = mutableListOf()

        suspend fun remove(mail: Mail) {
            removedMail.add(mail.id)
        }
    }
}

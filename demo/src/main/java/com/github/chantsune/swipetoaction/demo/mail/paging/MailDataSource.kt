package com.github.chantsune.swipetoaction.demo.mail.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.chantsune.swipetoaction.demo.mail.entity.Mail
import com.github.chantsune.swipetoaction.demo.mail.repository.IMailRepository

class MailDataSource(private val repository: IMailRepository) : PagingSource<Int, Mail>() {

    override fun getRefreshKey(state: PagingState<Int, Mail>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Mail> {
        return when (val key = params.key) {
            null -> LoadResult.Error(Error())
            0 -> LoadResult.Page(
                repository.getList(key, params.loadSize),
                null, key + 1,
            )
            2 -> LoadResult.Page(
                repository.getList(key, params.loadSize),
                key -1, null,
            )
            else -> LoadResult.Page(
                repository.getList(key, params.loadSize),
                key - 1,
                key + 1,
            )
        }
    }
}

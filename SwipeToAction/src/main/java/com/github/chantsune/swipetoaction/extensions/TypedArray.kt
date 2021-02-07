package com.github.chantsune.swipetoaction.extensions

import android.content.res.TypedArray

operator fun TypedArray.iterator() = object : Iterator<Int> {
    private var index = 0
    override fun hasNext() = index < indexCount
    override fun next() = getIndex(index++)
}

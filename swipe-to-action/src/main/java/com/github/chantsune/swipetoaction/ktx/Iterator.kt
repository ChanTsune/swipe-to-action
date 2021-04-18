package com.github.chantsune.swipetoaction.ktx

internal fun <T> Iterator<T>.nextOrNull(): T? = if (hasNext()) next() else null

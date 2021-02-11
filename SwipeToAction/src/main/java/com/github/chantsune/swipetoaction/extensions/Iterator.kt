package com.github.chantsune.swipetoaction.extensions

fun <T> Iterator<T>.nextOrNull(): T? = if (hasNext()) next() else null

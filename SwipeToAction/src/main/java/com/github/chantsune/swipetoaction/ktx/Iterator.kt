package com.github.chantsune.swipetoaction.ktx

fun <T> Iterator<T>.nextOrNull(): T? = if (hasNext()) next() else null

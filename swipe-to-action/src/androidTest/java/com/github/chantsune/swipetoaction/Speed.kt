package com.github.chantsune.swipetoaction

fun execute(count: Int, runnable: Runnable): Long {
    val base = System.currentTimeMillis()
    for (i in 0 until count) {
        runnable.run()
    }
    return System.currentTimeMillis() - base
}

package com.github.chantsune.swipetoaction.views

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.chantsune.swipetoaction.execute
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SimpleSwipeLayoutTest {
    @Test
    fun test_SimpleSwipeLayout_performance() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val ms = execute(500) {
            SimpleSwipeLayout(appContext)
        }
        Log.i(TAG, "${ms}ms")
    }

    companion object {
        private val TAG = SimpleSwipeLayoutTest::class.java.simpleName
    }
}

package com.github.chantsune.swipetoaction.views

import android.util.Log
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.chantsune.swipetoaction.execute
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SwipeLayoutTest {
    @Test
    fun test_SwipeLayout_performance() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val ms = execute(500) {
            SwipeLayout(appContext)
        }
        Log.i(TAG, "${ms}ms")
    }
    @Test
    fun test_View_performance() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val ms = execute(500) {
            View(appContext)
        }
        Log.i(TAG, "${ms}ms")
    }

    companion object {
        private val TAG = SwipeLayoutTest::class.java.simpleName
    }
}

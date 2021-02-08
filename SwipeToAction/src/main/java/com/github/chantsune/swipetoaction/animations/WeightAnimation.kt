package com.github.chantsune.swipetoaction.animations

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import com.github.chantsune.swipetoaction.extensions.Utils

class WeightAnimation(private val endWeight: Float, val view: View) : Animation() {
    private var startWeight = -1f
    private var deltaWeight = -1f

    init {
        duration = 200
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        if (startWeight < 0) {
            startWeight = Utils.getViewWeight(view)
            deltaWeight = endWeight - startWeight
        }
        Utils.setViewWeight(view, startWeight + deltaWeight * interpolatedTime)
    }

    override fun willChangeBounds(): Boolean = true
}

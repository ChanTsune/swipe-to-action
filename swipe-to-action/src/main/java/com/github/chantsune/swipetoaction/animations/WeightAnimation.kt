package com.github.chantsune.swipetoaction.animations

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import com.github.chantsune.swipetoaction.ktx.viewWeight

class WeightAnimation(private val endWeight: Float, private val view: View) : Animation() {
    private var startWeight = -1f
    private var deltaWeight = -1f

    init {
        duration = 200
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        if (startWeight < 0) {
            startWeight = view.viewWeight
            deltaWeight = endWeight - startWeight
        }
        view.viewWeight = startWeight + deltaWeight * interpolatedTime
    }

    override fun willChangeBounds(): Boolean = true
}

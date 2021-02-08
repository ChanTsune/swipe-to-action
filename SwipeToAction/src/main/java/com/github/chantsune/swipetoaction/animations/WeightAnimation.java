package com.github.chantsune.swipetoaction.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.github.chantsune.swipetoaction.extensions.Utils;

/**
 * Created by Alexander Pataridze
 */

public class WeightAnimation extends Animation {

    private float startWeight = -1;
    private float deltaWeight = -1;
    private final float endWeight;
    private View view;

    public WeightAnimation(float endWeight, View view) {
        this.endWeight = endWeight;
        this.view = view;
        setDuration(200);
    }

    public View getView() {
        return view;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if (startWeight < 0) {
            startWeight = Utils.getViewWeight(view);
            deltaWeight = endWeight - startWeight;
        }

        Utils.setViewWeight(view, (startWeight + (deltaWeight * interpolatedTime)));
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}


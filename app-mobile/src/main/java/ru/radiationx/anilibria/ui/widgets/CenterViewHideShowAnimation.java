/*
 * Copyright (C) 2015 - 2016 ExoMedia Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.radiationx.anilibria.ui.widgets;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

/**
 * An animation used to slide {@link com.devbrackets.android.exomedia.ui.widget.VideoControls}
 * in and out from the bottom of the screen when changing visibilities.
 */
public class CenterViewHideShowAnimation extends AnimationSet {
    private final View animationView;
    private final boolean toVisible;

    public CenterViewHideShowAnimation(View view, boolean toVisible, long duration) {
        super(false);
        this.toVisible = toVisible;
        this.animationView = view;

        //Creates the Alpha animation for the transition
        float startAlpha = toVisible ? 0 : 1;
        float endAlpha = toVisible ? 1 : 0;

        AlphaAnimation alphaAnimation = new AlphaAnimation(startAlpha, endAlpha);
        alphaAnimation.setDuration(duration);


        //Creates the Scale animation for the transition
        float startScale = toVisible ? 0.8f : 1;
        float endScale = toVisible ? 1 : 0.8f;

        ScaleAnimation scaleAnimation = new ScaleAnimation(startScale, endScale, startScale, endScale, view.getWidth() / 2f, view.getHeight() / 2f);
        scaleAnimation.setInterpolator(toVisible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
        scaleAnimation.setDuration(duration);

        //Adds the animations to the set
        addAnimation(alphaAnimation);
        addAnimation(scaleAnimation);

        setAnimationListener(new Listener());
    }

    private class Listener implements AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            animationView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            animationView.setVisibility(toVisible ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            //Purposefully left blank
        }
    }
}

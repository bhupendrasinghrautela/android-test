package com.makaan.ui;

import android.animation.TimeInterpolator;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*Copyright (C) 2015 A.Akira

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/
public interface ExpandableLayoutMethods {

    /**
     * Duration of expand animation
     */
    int DEFAULT_DURATION = 300;
    /**
     * Visibility of the layout when the layout attaches
     */
    boolean DEFAULT_EXPANDED = false;
    /**
     * Orientation of child views
     */
    int HORIZONTAL = 0;
    /**
     * Orientation of child views
     */
    int VERTICAL = 1;

    /**
     * Orientation of layout
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HORIZONTAL, VERTICAL})
    @interface Orientation {
    }

    /**
     * Starts animation the state of the view to the inverse of its current state.
     */
    void toggle();

    /**
     * Starts expand animation.
     */
    void expand();

    /**
     * Starts collapse animation.
     */
    void collapse();

    /**
     * Initializes this layout.
     *
     * @param isMaintain The state of expanse is maintained if you set true.
     */
    void initLayout(final boolean isMaintain);

    /**
     * Sets the expandable layout listener.
     *
     * @param listener ExpandableLayoutListener
     */
    void setListener(@NonNull final ExpandableLayoutListener listener);

    /**
     * Sets the length of the animation.
     * The default duration is 300 milliseconds.
     *
     * @param duration
     */
    void setDuration(final int duration);

    /**
     * Sets state of expanse.
     *
     * @param expanded The layout is visible if expanded is true
     */
    void setExpanded(final boolean expanded);

    /**
     * Gets state of expanse.
     *
     * @return true if the layout is visible
     */
    boolean isExpanded();

    /**
     * The time interpolator used in calculating the elapsed fraction of this animation. The
     * interpolator determines whether the animation runs with linear or non-linear motion,
     * such as acceleration and deceleration.
     * The default value is  {@link android.view.animation.AccelerateDecelerateInterpolator}
     *
     * @param interpolator
     */
    void setInterpolator(@NonNull final TimeInterpolator interpolator);
}
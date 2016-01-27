package com.makaan.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.makaan.R;

import java.util.ArrayList;
import java.util.List;

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

/**
 * Created by aishwarya on 07/12/15.
 */
public class ExpandableLinearLayout extends LinearLayout implements ExpandableLayoutMethods {

    private int duration;
    private Context mContext;
    private View mView;
    private boolean isExpanded;
    private TimeInterpolator interpolator = new LinearInterpolator();
    private int orientation;
    /**
     * The close position is width from left of layout if orientation is horizontal.
     * The close position is height from top of layout if orientation is vertical.
     */
    private int closePosition = 0;

    private ExpandableLayoutListener listener;
    private int layoutSize = 0;
    private boolean isArranged = false;
    private boolean isCalculatedSize = false;
    private boolean isAnimating = false;
    private List<Integer> childPositionList = new ArrayList<>();
    public ExpandableLinearLayout(Context context) {
        this(context, null);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        final TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.expandableLayout, defStyleAttr, 0);
        duration = typedArray.getInteger(R.styleable.expandableLayout_ael_duration, DEFAULT_DURATION);
        isExpanded = typedArray.getBoolean(R.styleable.expandableLayout_ael_expanded, DEFAULT_EXPANDED);
        orientation = typedArray.getInteger(R.styleable.expandableLayout_ael_orientation, LinearLayout.VERTICAL);
        typedArray.recycle();
        setVisibility(GONE);
    }

    public void initView(View view){
        mView = view;
    }

    @Override
    public void toggle() {
        if (isAnimating) {
            return;
        }
        if(getVisibility() == View.VISIBLE) {
            collapse();
        } else {
            expand();
        }
    }

    @Override
    public void expand() {
        if (isAnimating) {
            return;
        }
        this.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int height = this.getMeasuredHeight();

        createExpandAnimator(0, height,
                duration, interpolator).start();
    }

    @Override
    public void collapse() {
        if (isAnimating) {
            return;
        }
        final int height = this.getMeasuredHeight();
        createExpandAnimator(height, 0,
                duration, interpolator).start();
    }

    @Override
    public void initLayout(boolean isMaintain) {
        closePosition = 0;
        layoutSize = 0;
        isArranged = isMaintain;
        isCalculatedSize = false;
        super.requestLayout();
    }

    @Override
    public void setListener(@NonNull ExpandableLayoutListener listener) {
        this.listener = listener;
    }

    @Override
    public void setDuration(int duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " +
                    duration);
        }
        this.duration = duration;
    }

    @Override
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
        isArranged = false;
        requestLayout();
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public void setInterpolator(@NonNull TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    /**
     * Creates value animator.
     * Expand the layout if {@param to} is bigger than {@param from}.
     * Collapse the layout if {@param from} is bigger than {@param to}.
     *
     * @param from
     * @param to
     * @param duration
     * @param interpolator
     * @return
     */
    private ValueAnimator createExpandAnimator(
            final int from, final int to, final long duration, final TimeInterpolator interpolator) {
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                getLayoutParams().height = (int) animator.getAnimatedValue();
                requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimating = true;
                if (listener != null) {
                    listener.onAnimationStarted(mView);
//
                    if (to > from) {
                        listener.onPreOpen(mView);
                    } else {
                        listener.onPreClose(mView);
                    }
                }
                if (getVisibility() == View.GONE) {
                    setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimating = false;
                isExpanded = to > from;

                if (isExpanded) {
                    getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                    if (listener != null) {
                        listener.onOpened(mView);
                    }
                } else {
                    setVisibility(View.GONE);
                    if (listener != null) {
                        listener.onClosed(mView);
                    }
                }
                if (listener != null) {
                    listener.onAnimationEnded(mView);
                }
            }
        });
        return valueAnimator;
    }
}

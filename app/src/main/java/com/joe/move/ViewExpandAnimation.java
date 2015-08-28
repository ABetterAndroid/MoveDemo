package com.joe.move;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout.LayoutParams;

/**
 * Created by qiaorongzhu on 2015/8/26.
 */
public class ViewExpandAnimation extends Animation {

    private View mAnimationView = null;
    private LayoutParams mViewLayoutParams = null;
    private int mStart = 0;
    private int mEnd = 0;

    public ViewExpandAnimation(View view){
        animationSettings(view, 300);
    }

    public ViewExpandAnimation(View view, int duration){
        animationSettings(view, duration);
    }

    private void animationSettings(View view, int duration){
        setDuration(duration);
        mAnimationView = view;
        mViewLayoutParams = (LayoutParams) view.getLayoutParams();
        mStart = mViewLayoutParams.leftMargin;
        mEnd = (mStart == 0 ? (0 - view.getWidth()) : 0);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        Log.i("time", interpolatedTime+"");

        if(interpolatedTime < 1.0f){
            mViewLayoutParams.leftMargin = mStart + (int) ((mEnd - mStart) * interpolatedTime);
            // invalidate
            mAnimationView.requestLayout();
        }else{
            mViewLayoutParams.leftMargin = mEnd;
            mAnimationView.requestLayout();
            if(mEnd != 0){
                mAnimationView.setVisibility(View.GONE);
            }
        }
    }

}
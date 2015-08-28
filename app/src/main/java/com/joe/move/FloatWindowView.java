package com.joe.move;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

public class FloatWindowView extends LinearLayout {

	/**
	 * 记录悬浮窗的宽度
	 */
	public static int viewWidth;

	/**
	 * 记录悬浮窗的高度
	 */
	public static int viewHeight;

	/**
	 * 记录系统状态栏的高度
	 */
	 private static int statusBarHeight;

	/**
	 * 用于更新悬浮窗的位置
	 */
	private WindowManager windowManager;

	/**
	 * 悬浮窗的参数
	 */
	private WindowManager.LayoutParams mParams;

	/**
	 * 记录当前手指位置在屏幕上的横坐标值
	 */
	private float xInScreen;

	/**
	 * 记录当前手指位置在屏幕上的纵坐标值
	 */
	private float yInScreen;

	/**
	 * 记录手指按下时在屏幕上的横坐标的值
	 */
	private float xDownInScreen;

	/**
	 * 记录手指按下时在屏幕上的纵坐标的值
	 */
	private float yDownInScreen;

	/**
	 * 记录手指按下时在悬浮窗的View上的横坐标的值
	 */
	private float xInView;

	/**
	 * 记录手指按下时在悬浮窗的View上的纵坐标的值
	 */
	private float yInView;

	private View expandView;

	/**
	 * 隐藏动画
	 */
	private final TranslateAnimation hideAnimation;
	/**
	 * 显示动画
	 */
	private final TranslateAnimation showAnimation;

	public FloatWindowView(Context context) {
		super(context);
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window, this);
		View view = findViewById(R.id.small_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		expandView = findViewById(R.id.expand_view);
		hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		Interpolator interpolator = new DecelerateInterpolator();
		hideAnimation.setInterpolator(interpolator);
		hideAnimation.setDuration(500);
		hideAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				expandView.setVisibility(View.GONE);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		showAnimation.setInterpolator(interpolator);
		showAnimation.setDuration(500);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY() - getStatusBarHeight();
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			break;
		case MotionEvent.ACTION_MOVE:
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			// 手指移动的时候更新悬浮窗的位置
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			// 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
			if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
				// TODO: 2015/8/26 点击悬浮窗
				if (expandView.getVisibility() == View.GONE) {
					expandView.setVisibility(View.VISIBLE);
					expandView.startAnimation(showAnimation);
				} else {
					expandView.startAnimation(hideAnimation);
				}
//				expandView.startAnimation(new ViewExpandAnimation(expandView));

			}
			break;
			default:
			break;
		}
		return true;
	}

	/**
	 * 将悬浮窗的参数传入，用于更新悬浮窗的位置。
	 * 
	 * @param params
	 *            悬浮窗的参数
	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	/**
	 * 更新悬浮窗在屏幕中的位置。
	 */
	private void updateViewPosition() {
		mParams.x = (int) (xInScreen - xInView);
		mParams.y = (int) (yInScreen - yInView);
		windowManager.updateViewLayout(this, mParams);
	}

	/**
	 * 用于获取状态栏的高度。
	 * 
	 * @return 返回状态栏高度的像素值。
	 */
	private int getStatusBarHeight() {
		if (statusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}

}

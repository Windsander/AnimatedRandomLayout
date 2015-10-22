package com.special.animatedrandomlayout.random_layout;

import android.view.View;

import com.nineoldandroids.view.ViewPropertyAnimator;


public class AnimatorUtil {

	private ViewPropertyAnimator animate;
	private int duration;
	private View view;
	
	public AnimatorUtil(View view, int duration) {
		super();
		this.view = view;
		this.duration = duration;
		animate = ViewPropertyAnimator.animate(view);
	}
	
	
	public ViewPropertyAnimator getAnimate() {
		return animate;
	}

	
	public AnimatorUtil addScaleAnimationBy(float value){
		animate.scaleXBy(value).scaleYBy(value)
		       .setDuration(duration);
		return this;
	}
	
	public AnimatorUtil addTranslationAnimationBy(float valueX, float valueY){
		animate.translationXBy(valueX).translationYBy(valueY)
			   .setDuration(duration);
		return this;
	}
	
	public AnimatorUtil addRotationAnimationBy(float degree){
		animate.rotationBy(degree)
			   .setDuration(duration);
		return this;
	}
	
	public AnimatorUtil addAlphaAnimationBy(float value){
		animate.alphaBy(value)
			   .setDuration(duration);
		return this;
	}
	
	/**
	 * 此方法用于供使用云布局的编程人员，实现自定义特效
	 * @attention
	 * 内部移除了默认特效，使用时必须内部调用父类方法{@link resetAnimation}
	 */
	public void setSelfAnimator(){
		
	}
	
	/**
	 * 清除当前定义到指定 view 上的所有特效，并重新初始化
	 */
	public void resetAnimation(){
		animate.cancel();
		view.clearAnimation();
		animate = ViewPropertyAnimator.animate(view);
	}
	
	public void startAnimator(){
		animate.start();
	}
	
}














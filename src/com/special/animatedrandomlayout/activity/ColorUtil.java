package com.special.animatedrandomlayout.activity;

import java.util.Random;

import android.graphics.Color;

public class ColorUtil {
	/**
	 * 随机生成漂亮的颜色
	 * @return
	 */
	public static int randomColor(){
		Random random = new Random();
		//如果值太大，会偏白，太小则会偏黑，所以需要对颜色的值进行范围限定
		int red = random.nextInt(150)+50;//50-199
		int green = random.nextInt(150)+50;//50-199
		int blue = random.nextInt(150)+50;//50-199
		return Color.rgb(red, green, blue);//根据rgb混合生成一种新的颜色
	}
}

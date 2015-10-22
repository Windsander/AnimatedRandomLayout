package com.special.animatedrandomlayout.random_layout;

import android.graphics.Point;

/**
 * 几何图形工具
 */
public class GeometryUtil {
	
	/**
	 * As meaning of method name.
	 * 获得两点之间的距离
	 * @param x1 
	 * @param y1 
	 * @param x2 
	 * @param y2 
	 * @return
	 */
	public static float getDistanceBetween2Points(ChildViewBound params, Point p1) {
		int x1 = params.getChildLeft();
		int y1 = params.getChildTop();
		Point p0 = new Point(x1, y1);
		float distance = (float) Math.sqrt(Math.pow(p0.y - p1.y, 2) + Math.pow(p0.x - p1.x, 2));
		return distance;
	}
	
	public static float getDistanceBetween2Points(int x, int y, Point p1) {
		Point p0 = new Point(x, y);
		float distance = (float) Math.sqrt(Math.pow(p0.y - p1.y, 2) + Math.pow(p0.x - p1.x, 2));
		return distance;
	}
	
	/**
	 * 根据分度值，计算从start到end中，fraction位置的值。fraction范围为0 -> 1
	 * @param fraction
	 * @param start
	 * @param end
	 * @return
	 */
	public static float evaluateValue(float fraction, Number start, Number end){
		return start.floatValue() + (end.floatValue() - start.floatValue()) * fraction;
	}
	
	public static float caculateDx(ChildViewBound params, Point point){
		int x1 = params.getChildLeft();
		int x2 = point.x;
		float dx = (float)(x2 - x1);
		return dx;
	}
	
	public static float caculateDy(ChildViewBound params, Point point){
		int y1 = params.getChildTop();
		int y2 = point.y;
		float dy = (float)(y2 - y1);
		return dy;
	}
	
}
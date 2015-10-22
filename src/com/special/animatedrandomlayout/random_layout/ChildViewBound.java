package com.special.animatedrandomlayout.random_layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ChildViewBound extends FrameLayout.LayoutParams{
	private int childLeft ;
	private int childTop ;
	private int childRight ;
	private int childBottom ;
	
	public ChildViewBound(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}
	public ChildViewBound(int w, int h) {
		super(w, h);
	}
	public ChildViewBound(ViewGroup.LayoutParams source) {
		super(source);
	}

	public int getChildLeft() {
		return childLeft;
	}
	public void setChildLeft(int childLeft) {
		this.childLeft = childLeft;
	}
	public int getChildTop() {
		return childTop;
	}
	public void setChildTop(int childTop) {
		this.childTop = childTop;
	}
	public int getChildRight() {
		return childRight;
	}
	public void setChildRight(int childRight) {
		this.childRight = childRight;
	}
	public int getChildBottom() {
		return childBottom;
	}
	public void setChildBottom(int childBottom) {
		this.childBottom = childBottom;
	}
	
	public void clear(){
		this.childLeft = 0;
		this.childTop = 0;
		this.childRight = 0;
		this.childBottom = 0;
	}
	
	
	public void setChildViewBound(int childLeft, int childTop, int childRight,
			int childBottom) {
		this.childLeft = childLeft;
		this.childTop = childTop;
		this.childRight = childRight;
		this.childBottom = childBottom;
	}
	
	@Override
	public String toString() {
		return "ChildViewBound [childLeft=" + childLeft + ", childTop="
				+ childTop + ", childRight=" + childRight + ", childBottom="
				+ childBottom + "]";
	}
	
}

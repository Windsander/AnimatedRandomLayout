package com.special.animatedrandomlayout.activity;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	private static Toast toast;
	public static void showToast(Context context, String text){
		if(toast==null){
			toast = Toast.makeText(context,text,Toast.LENGTH_SHORT);
		}
		toast.setText(text);
		toast.show();
	}
}

package com.special.animatedrandomlayout.activity;

import android.util.Log;

public class LogUtil {
	
	private static boolean isDebug = true;
	
	public static void LOGW(String tag, String str){
		if(isDebug){
			Log.w(tag, str);
		}
	}

}

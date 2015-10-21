package com.special.simplecloudview.activity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.special.simplecloudview.R;
import com.special.simplecloudview.random_layout.CloudRandomLayout;
import com.special.simplecloudview.random_layout.CloudRandomLayout.OnCreateItemViewListener;

public class MainActivity extends Activity {

	private List<String> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		CloudRandomLayout cloudRandomLayout = (CloudRandomLayout) findViewById(R.id.rl_cloud);
		
		
		String[] str = {"1","2","3","4","5","6","7","8","9","10","a","b","c","d","e","f","g","h","i",
						"j","k","l","m","n","o","p","r","s","u","v","w","x","y","z"};
		list = Arrays.asList(str);
		
		
		cloudRandomLayout.setRegularity(15, 15);
		cloudRandomLayout.setItemShowCount(2);
		cloudRandomLayout.setLooperDuration(10);
		cloudRandomLayout.setDefaultDruation(20000);
		cloudRandomLayout.setOnCreateItemViewListener(new OnCreateItemViewListener() {
			
			@Override
			public int getCount() {
				return list.size();
			}
			
			@Override
			public View createItemView(int position, View convertView) {
				final TextView textView = new TextView(getApplicationContext());
				//1.设置文本数据
				int listPosition = position;
				textView.setText(list.get(listPosition) + "");
				//2.设置随机的字体
				Random random = new Random();
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,random.nextInt(8)+24);//14-21
				//3.上色，设置随机字体颜色
				textView.setTextColor(ColorUtil.randomColor());
				//4.设置点击事件
				textView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ToastUtil.showToast(getApplicationContext(), textView.getText().toString());
					}
				});
				
				return textView;
			}
		});
		
		cloudRandomLayout.start();
		
	}


}

package com.wzf.slippingmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ScrollLayoutMainActivity extends Activity {
	private Button layout3Screen ;
	private Button layout3ScreenWidth ;
	private Button layout3ScreenCover ;
	private Button layout3ScreenAutoCover ;
	private Button layout2Screen ;
	private Button layout2ScreenCover ;
	private Button layout2ScreenCanvas ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroll_layout_main);
		
		layout2Screen = (Button)findViewById(R.id.layout_2_screen);
		layout2ScreenCover = (Button)findViewById(R.id.layout_2_screen_cover);
		layout2ScreenCanvas = (Button)findViewById(R.id.layout_2_screen_canvas);
		layout3Screen = (Button)findViewById(R.id.layout_3_screen);
		layout3ScreenWidth = (Button)findViewById(R.id.layout_3_screenwidth);
		layout3ScreenCover = (Button)findViewById(R.id.layout_3_screencover);
		layout3ScreenAutoCover = (Button)findViewById(R.id.layout_3_screenautocover);
		
		layout2Screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ScrollLayoutMainActivity.this, ScrollLayout2ScreenActivity.class);
				startActivity(intent);
			}
		});
		layout2ScreenCover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ScrollLayoutMainActivity.this, ScrollLayout2ScreenCoverActivity.class);
				startActivity(intent);
			}
		});
		layout2ScreenCanvas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ScrollLayoutMainActivity.this, ScrollLayout2ScreenCanvasNavigationActivity.class);
				startActivity(intent);
			}
		});
		layout3Screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ScrollLayoutMainActivity.this, ScrollLayout3ScreenActivity.class);
				startActivity(intent);
			}
		});
		layout3ScreenWidth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ScrollLayoutMainActivity.this, ScrollLayout3ScreenWidthActivity.class);
				startActivity(intent);
			}
		});
		layout3ScreenCover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ScrollLayoutMainActivity.this, ScrollLayout3ScreenCoverActivity.class);
				startActivity(intent);
			}
		});
		layout3ScreenAutoCover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ScrollLayoutMainActivity.this, ScrollLayout3ScreenAutoCoverActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

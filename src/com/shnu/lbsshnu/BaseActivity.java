package com.shnu.lbsshnu;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	LBSApplication lbsApplication;
	SimpleSideDrawer simpleSideDrawer;
	private long exitTime = 0;
	public static boolean isPopUp = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lbsApplication = (LBSApplication) getApplication();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * 设置slideractionbar
	 */
	public void setSliderActionBar() {
		simpleSideDrawer = new SimpleSideDrawer(this);
		simpleSideDrawer.setLeftBehindContentView(R.layout.userpref);
		simpleSideDrawer.setRightBehindContentView(R.layout.actionmore);
		ImageView userImageView = (ImageView) findViewById(R.id.userpref);
		ImageView moreImageView = (ImageView) findViewById(R.id.actionmore);
		userImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				simpleSideDrawer.toggleLeftDrawer();
			}
		});
		moreImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				simpleSideDrawer.toggleRightDrawer();
				// FileIO fileIO = new FileIO();
				// fileIO.getDateFromXML();
			}
		});
	}

	/*
	 * 设置退出
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次后退键退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

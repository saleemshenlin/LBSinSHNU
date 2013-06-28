package com.shnu.lbsshnu;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class BaseActivity extends Activity {
	LBSApplication lbsApplication;
	SimpleSideDrawer simpleSideDrawer;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lbsApplication = (LBSApplication) getApplication();
		setSliderActionBar();

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

	private void setSliderActionBar() {
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.actionbar);
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
			}
		});
	}

}

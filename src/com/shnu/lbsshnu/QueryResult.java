package com.shnu.lbsshnu;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;

public class QueryResult extends BaseActivity {
	private static final String TAG = "QueryResult";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.queryresult);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		String text = getIntent().getStringExtra("Query");
		Log.e(TAG, text);
	}
}

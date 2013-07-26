package com.shnu.lbsshnu;

import android.app.ActionBar;
import android.os.Bundle;

public class QueryResult extends BaseActivity {
	private static final String TAG = "QueryResult";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.queryresult);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		initResultBar("list");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}

package com.shnu.lbsshnu;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * ��AboutActivity <br>
 * ������ʾ��������
 * 
 */
public class AboutActivity extends BaseActivity {
	/**
	 * ʵ��һ��ActionBar
	 */
	private ActionBar mActionBar;

	/**
	 * ����AboutActivity<br>
	 * ���巽������:<br>
	 * 1)��ȡActionBar,����ActionBar��������,��icon,��������ϼ�...<br>
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutactivity_view);
		mActionBar = getActionBar();
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setIcon(R.drawable.ic_action_back);
		mActionBar.setCustomView(R.layout.about_actionbar_title);
		mActionBar.setHomeButtonEnabled(true);
	}

	/**
	 * ���ڵ��ActionBar�ķ��ذ�ť,���ص�HomeActivity
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			this.overridePendingTransition(R.anim.anim_in_left2right,
					R.anim.anim_out_left2right);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

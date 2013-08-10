package com.shnu.lbsshnu;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * 类AboutActivity <br>
 * 用于显示关于我们
 * 
 */
public class AboutActivity extends BaseActivity {
	/**
	 * 实例一个ActionBar
	 */
	private ActionBar mActionBar;

	/**
	 * 创建AboutActivity<br>
	 * 具体方法如下:<br>
	 * 1)获取ActionBar,并对ActionBar进行设置,如icon,点击返回上级...<br>
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
	 * 用于点击ActionBar的返回按钮,返回到HomeActivity
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

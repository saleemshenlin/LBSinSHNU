package com.shnu.lbsshnu;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

/**
 * 类ViewPagerAdapter<br>
 * 用于给GuideActivity绑内容
 */
public class ViewPagerAdapter extends PagerAdapter {

	/**
	 * 实例一个List<View>,用来加载需要显示的view
	 */
	private List<View> views;
	/**
	 * 实例一个Activity
	 */
	private Activity activity;
	/**
	 * 定义一个常量,用于表示SharedPreferences的名称
	 */
	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	/**
	 * 用于构造ViewPagerAdapter
	 * 
	 * @param views
	 *            需要显示的view列表
	 * @param activity
	 *            用的 Activity
	 */
	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	}

	/**
	 * 用于销毁不显示的view
	 */

	@Override
	public void destroyItem(View view, int position, Object object) {
		((ViewPager) view).removeView(views.get(position));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	/**
	 * 用于获得当前界面数
	 */
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	/**
	 * 用于初始化position位置的界面<br>
	 * 1)如果view是最后一个view时,初始化进入应用的按钮<br>
	 * 2)调用setGuided()更新SharedPreferences<br>
	 * 3)调用goHome()进入Home
	 */
	@Override
	public Object instantiateItem(View view, int position) {
		((ViewPager) view).addView(views.get(position), 0);
		if (position == views.size() - 1) {
			ImageView mImageView = (ImageView) view.findViewById(R.id.imgStart);
			mImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					setGuided();
					goHome();

				}

			});
		}
		return views.get(position);
	}

	/**
	 * 用于判断是否由对象生成界面
	 */
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	/**
	 * 用于跳转进入HomeActivity
	 */
	private void goHome() {
		Intent intent = new Intent(activity, HomeActivity.class);
		activity.startActivity(intent);
		activity.finish();
		activity.overridePendingTransition(R.anim.anim_in_right2left,
				R.anim.anim_out_left2right);
	}

	/**
	 * 用于更新SharedPreferences，下次启动不用再次引导
	 */
	private void setGuided() {
		SharedPreferences preferences = activity.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}

}

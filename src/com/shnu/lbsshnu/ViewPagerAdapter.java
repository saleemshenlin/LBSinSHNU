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
 * ��ViewPagerAdapter<br>
 * ���ڸ�GuideActivity������
 */
public class ViewPagerAdapter extends PagerAdapter {

	/**
	 * ʵ��һ��List<View>,����������Ҫ��ʾ��view
	 */
	private List<View> views;
	/**
	 * ʵ��һ��Activity
	 */
	private Activity activity;
	/**
	 * ����һ������,���ڱ�ʾSharedPreferences������
	 */
	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	/**
	 * ���ڹ���ViewPagerAdapter
	 * 
	 * @param views
	 *            ��Ҫ��ʾ��view�б�
	 * @param activity
	 *            �õ� Activity
	 */
	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	}

	/**
	 * �������ٲ���ʾ��view
	 */

	@Override
	public void destroyItem(View view, int position, Object object) {
		((ViewPager) view).removeView(views.get(position));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	/**
	 * ���ڻ�õ�ǰ������
	 */
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	/**
	 * ���ڳ�ʼ��positionλ�õĽ���<br>
	 * 1)���view�����һ��viewʱ,��ʼ������Ӧ�õİ�ť<br>
	 * 2)����setGuided()����SharedPreferences<br>
	 * 3)����goHome()����Home
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
	 * �����ж��Ƿ��ɶ������ɽ���
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
	 * ������ת����HomeActivity
	 */
	private void goHome() {
		Intent intent = new Intent(activity, HomeActivity.class);
		activity.startActivity(intent);
		activity.finish();
		activity.overridePendingTransition(R.anim.anim_in_right2left,
				R.anim.anim_out_left2right);
	}

	/**
	 * ���ڸ���SharedPreferences���´����������ٴ�����
	 */
	private void setGuided() {
		SharedPreferences preferences = activity.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}

}

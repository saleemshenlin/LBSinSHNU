package com.shnu.lbsshnu;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * �״μ����������棬ͨ�^ViewPagerʵ��
 */
public class GuideActivity extends BaseActivity implements OnPageChangeListener {
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private List<View> views;

	/**
	 * �ײ�С��ͼƬ
	 */
	private ImageView[] dots;

	/**
	 * ��¼��ǰѡ��λ��
	 */
	private int intCurrentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_view);
		initViews();
		initDots();

	}

	public void onPageScrollStateChanged(int arg0) {
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	/**
	 * ���µ�ҳ�汻ѡ��ʱ���ã����õײ�С��ѡ��״̬
	 */
	public void onPageSelected(int arg0) {
		setCurrentDot(arg0);
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initViews() {
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		// ��ʼ������ͼƬ�б�
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_one, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_two, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_three, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_four, null));

		// ��ʼ��Adapter
		mViewPagerAdapter = new ViewPagerAdapter(views, this);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mViewPagerAdapter);
		// �󶨻ص�
		mViewPager.setOnPageChangeListener(this);
	}

	/**
	 * ��ʼ���ײ�С��
	 */
	private void initDots() {
		LinearLayout lnlGuide = (LinearLayout) findViewById(R.id.lnlGuide);

		dots = new ImageView[views.size()];

		// ѭ��ȡ��С��ͼƬ
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) lnlGuide.getChildAt(i);
			dots[i].setEnabled(true);// ����Ϊ��ɫ
		}

		intCurrentIndex = 0;
		dots[intCurrentIndex].setEnabled(false);// ����Ϊ��ɫ����ѡ��״̬
	}

	/**
	 * ���õ�ǰDot
	 * 
	 * @param position
	 *            ��ǰλ��
	 */
	private void setCurrentDot(int position) {
		if (position < 0 || position > views.size() - 1
				|| intCurrentIndex == position) {
			return;
		}

		dots[position].setEnabled(false);
		dots[intCurrentIndex].setEnabled(true);

		intCurrentIndex = position;
	}

}

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
 * ��GuideActivity<br>
 * �����״μ���ʱ����������<br>
 * ��һ��Activity��,����4��View<br>
 * ͨ��ViewPagerʵ��4��ҳ�������໥�л�
 */
public class GuideActivity extends BaseActivity implements OnPageChangeListener {
	/**
	 * ����һ������,���ڼ�¼��ǰѡ��λ��
	 */
	private int intCurrentIndex;
	/**
	 * ʵ��һ��ViewPager
	 */
	private ViewPager mViewPager;
	/**
	 * ʵ��һ��ViewPagerAdapter
	 */
	private ViewPagerAdapter mViewPagerAdapter;
	/**
	 * ʵ��һ��List<View>,���ڴ��4��View
	 */
	private List<View> views;

	/**
	 * ʵ��һ��ImageView[],���ڴ�ŵײ��л���С��
	 */
	private ImageView[] dots;

	/**
	 * ����GuideActivity<br>
	 * 1)����initViews(),��ʼ��View<br>
	 * 2)����initDots(),��ʼ���ײ�С��<br>
	 */
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
	 * ���ڵ��µ�ҳ�汻ѡ��ʱ������setCurrentDot(),���õײ�С��ѡ��״̬
	 */
	public void onPageSelected(int arg0) {
		setCurrentDot(arg0);
	}

	/**
	 * ���ڳ�ʼ��ҳ��<br>
	 * ���巽������:<br>
	 * 1)��ʼ������ͼƬ�б�views;<br>
	 * 2)��ʼ��mViewPagerAdapter<br>
	 * 3)mViewPager��OnPageChange�¼�<br>
	 */
	private void initViews() {
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_one, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_two, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_three, null));
		views.add(mLayoutInflater.inflate(R.layout.guide_pager_four, null));

		mViewPagerAdapter = new ViewPagerAdapter(views, this);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	/**
	 * ���� ��ʼ���ײ�С��<br>
	 * ���巽������:<br>
	 * 1)����view������ʼ��С��ͼƬ�б�dots<br>
	 * 2)ѭ��ȡ��С��ͼƬ,��С�㶼��Ϊ��ɫ<br>
	 * 3)view����ѡ��״̬ʱ,��С������Ϊ��ɫ<br>
	 */
	private void initDots() {
		LinearLayout lnlGuide = (LinearLayout) findViewById(R.id.lnlGuide);

		dots = new ImageView[views.size()];

		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) lnlGuide.getChildAt(i);
			dots[i].setEnabled(true);
		}

		intCurrentIndex = 0;
		dots[intCurrentIndex].setEnabled(false);
	}

	/**
	 * ����view�л�ʱ,���õ�ǰС�����ɫ
	 * 
	 * @param position
	 *            view��ǰλ��
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

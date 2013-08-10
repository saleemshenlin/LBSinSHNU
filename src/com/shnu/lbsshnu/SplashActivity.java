package com.shnu.lbsshnu;

import java.text.ParseException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 类SplashActivity<br>
 * 作为应用首页,用于判断是否是首次加载,分别指向不同的Activity<br>
 * 通过SharedPreferences来记录是否是首次加入
 * 
 */
public class SplashActivity extends BaseActivity {
	/**
	 * 定义一个常量,用来纪录时候是否是首次加载,
	 */
	boolean isFirstIn = false;
	/**
	 * 定义一个常数,用于表示进入HomeActivity
	 */
	private static final int GO_HOME = 1000;
	/**
	 * 定义一个常数,用于表示进入GuideActivity
	 */
	private static final int GO_GUIDE = 1001;
	/**
	 * 定义一个常数,用于表示等待时间,值为1s
	 */
	private static final long SPLASH_DELAY_MILLIS = 1000;
	/**
	 * 定义一个常量,用于表示SharedPreferences的名称
	 */
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	/**
	 * 实例一个进度条,用来表示正在加载数据
	 */
	private ProgressBar prbLoad;
	/**
	 * 定义一个Handler,用来表示跳转到不同界面
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 创建SplashActivity<br>
	 * 1)初始化prbLoad<br>
	 * 2)多线程导入数据到数据库<br>
	 * 3)多线程导入地图数据到SDcard<br>
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashview);
		prbLoad = (ProgressBar) findViewById(R.id.prbLoadData);
		new InitDataBaseData().execute();
		new InitSuperMapData().execute();
	}

	/**
	 * 用于通过SharedPreferences记录是否属于首次打开<br>
	 * 首次进入发送Handler信息GO_GUIDE 不是首次发送Handler信息GO_HOME
	 */
	private void init() {
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		isFirstIn = preferences.getBoolean("isFirstIn", true);
		if (!isFirstIn) {
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}

	}

	/**
	 * 用于跳转进入HomeActivity
	 */
	private void goHome() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
		SplashActivity.this.overridePendingTransition(
				R.anim.anim_in_right2left, R.anim.anim_out_left2right);
	}

	/**
	 * 用于跳转j进入GuideActivity
	 */
	private void goGuide() {
		Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
		SplashActivity.this.overridePendingTransition(
				R.anim.anim_in_right2left, R.anim.anim_out_left2right);
	}

	/**
	 * 类InitSuperMapData<br>
	 * 用于在首次加载时调用FileIO.copyMapData()<br>
	 * 采用多线程载入地图数据
	 */
	class InitSuperMapData extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			FileIO fileIO = new FileIO();
			fileIO.copyMapData(LbsApplication.getContext());
			return null;
		}

	}

	/**
	 * 类InitDataBaseData<br>
	 * 用于在首次加载时调用FileIO.getDateFromXML()<br>
	 * 采用多线程导入数据、更新課程日期，<br>
	 * 因导入数据时间比导入地图数据时间长,在结束之后调用init();
	 */
	class InitDataBaseData extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			prbLoad.setVisibility(View.GONE);
			Toast.makeText(SplashActivity.this, result, Toast.LENGTH_SHORT)
					.show();
			init();
		}

		@Override
		protected String doInBackground(String... params) {
			FileIO fileIO = new FileIO();
			fileIO.getDateFromXML();
			try {
				LbsApplication.getEventData().updateCourseDate();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "加载完毕";
		}

	}

}

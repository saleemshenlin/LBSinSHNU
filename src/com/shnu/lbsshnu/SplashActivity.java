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
 * ��SplashActivity<br>
 * ��ΪӦ����ҳ,�����ж��Ƿ����״μ���,�ֱ�ָ��ͬ��Activity<br>
 * ͨ��SharedPreferences����¼�Ƿ����״μ���
 * 
 */
public class SplashActivity extends BaseActivity {
	/**
	 * ����һ������,������¼ʱ���Ƿ����״μ���,
	 */
	boolean isFirstIn = false;
	/**
	 * ����һ������,���ڱ�ʾ����HomeActivity
	 */
	private static final int GO_HOME = 1000;
	/**
	 * ����һ������,���ڱ�ʾ����GuideActivity
	 */
	private static final int GO_GUIDE = 1001;
	/**
	 * ����һ������,���ڱ�ʾ�ȴ�ʱ��,ֵΪ1s
	 */
	private static final long SPLASH_DELAY_MILLIS = 1000;
	/**
	 * ����һ������,���ڱ�ʾSharedPreferences������
	 */
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	/**
	 * ʵ��һ��������,������ʾ���ڼ�������
	 */
	private ProgressBar prbLoad;
	/**
	 * ����һ��Handler,������ʾ��ת����ͬ����
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
	 * ����SplashActivity<br>
	 * 1)��ʼ��prbLoad<br>
	 * 2)���̵߳������ݵ����ݿ�<br>
	 * 3)���̵߳����ͼ���ݵ�SDcard<br>
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
	 * ����ͨ��SharedPreferences��¼�Ƿ������״δ�<br>
	 * �״ν��뷢��Handler��ϢGO_GUIDE �����״η���Handler��ϢGO_HOME
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
	 * ������ת����HomeActivity
	 */
	private void goHome() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
		SplashActivity.this.overridePendingTransition(
				R.anim.anim_in_right2left, R.anim.anim_out_left2right);
	}

	/**
	 * ������תj����GuideActivity
	 */
	private void goGuide() {
		Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
		SplashActivity.this.overridePendingTransition(
				R.anim.anim_in_right2left, R.anim.anim_out_left2right);
	}

	/**
	 * ��InitSuperMapData<br>
	 * �������״μ���ʱ����FileIO.copyMapData()<br>
	 * ���ö��߳������ͼ����
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
	 * ��InitDataBaseData<br>
	 * �������״μ���ʱ����FileIO.getDateFromXML()<br>
	 * ���ö��̵߳������ݡ������n�����ڣ�<br>
	 * ��������ʱ��ȵ����ͼ����ʱ�䳤,�ڽ���֮�����init();
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
			return "�������";
		}

	}

}

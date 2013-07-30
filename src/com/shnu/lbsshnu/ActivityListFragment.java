package com.shnu.lbsshnu;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ActivityListFragment extends Fragment {
	private static final String TAG = "CommonUI";
	private ListView activityListView;
	private View rootView;
	private int indexTab;
	private static ActivityClass activity = new ActivityClass();
	private SimpleCursorAdapter adapter;
	private final String[] FROM = { ActivityData.C_NAME,
			ActivityData.C_LOCATION, ActivityData.C_DATE };
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime };
	private OnFragmeng2ActivityListener onFragmeng2ActivityListener;
	private ActivityProvider activityProvider;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		indexTab = getArguments().getInt("Index");
		rootView = inflater
				.inflate(R.layout.activitylistview, container, false);
		activityListView = (ListView) rootView.findViewById(R.id.activityList);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onFragmeng2ActivityListener = (OnFragmeng2ActivityListener) activity;
			activityProvider = new ActivityProvider();
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		try {
			Cursor itemCursor = null;
			itemCursor = activityProvider.query(ActivityProvider.CONTENT_URI,
					null, getQuerySection(indexTab), null, getOrderBy());
			int num = itemCursor.getCount();
			Log.i(TAG, "ActivityProvider cursor" + num);
			adapter = new SimpleCursorAdapter(LbsApplication.getContext(),
					R.layout.row, itemCursor, FROM, TO,
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			adapter.setViewBinder(LIST_VIEW_BINDER);
			activityListView.setAdapter(adapter);
			activityListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					showPopupwindows(id);
				}
			});
			if (itemCursor.isClosed()) {
				itemCursor.close();
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			LbsApplication.getActivityData().closeDatabase();
		}
		if (ActivityListView.getActivityId() != 0) {
			showPopupwindows(ActivityListView.getActivityId());
			ActivityListView.setActivityId(0);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/*
	 * 查询条件
	 */
	private String getQuerySection(int index) {
		String sql;
		switch (index) {
		case 0:
			sql = ActivityData.C_DATE + " > (SELECT DATE('now')) and "
					+ ActivityData.C_DATE
					+ " < (SELECT DATE('now', '+7 day')) and "
					+ ActivityData.C_TIME + " > (SELECT TIME('now')) and "
					+ ActivityData.C_TYPE + " = 2";
			return sql;
		case 1:
			sql = ActivityData.C_DATE + " > (SELECT DATE('now')) and "
					+ ActivityData.C_DATE
					+ " < (SELECT DATE('now', '+7 day')) and "
					+ ActivityData.C_TIME + " > (SELECT TIME('now')) and "
					+ ActivityData.C_TYPE + " = 1";
			return sql;
		case 2:
			sql = ActivityData.C_TYPE + " = 3";
			return sql;
		case 3:
			sql = ActivityData.C_DATE + " > (SELECT DATE('now')) and "
					+ ActivityData.C_ISLIKE + " = 1";
			return sql;
		default:
			return null;
		}
	}

	/*
	 * order by条件
	 */
	private String getOrderBy() {
		String sql = null;
		sql = ActivityData.C_DATE + " ASC";
		return sql;
	}

	/*
	 * 显示popupwindow
	 */
	@SuppressWarnings({ "static-access" })
	private void showPopupwindows(final long id) {
		try {
			LayoutInflater layoutInflater = (LayoutInflater) getActivity()
					.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.popupwindow, null);
			final PopupWindow popupWindow = new PopupWindow(view,
					LbsApplication.getScreenWidth() * 3 / 4,
					LbsApplication.getScreenHeight() * 3 / 4, true);
			bindPopupData(id, view, popupWindow);
			popupWindow
					.setBackgroundDrawable(new BitmapDrawable(getResources()));
			popupWindow.showAtLocation(rootView, Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.setAnimationStyle(R.anim.popupanimation);
			ColorDrawable dw = new ColorDrawable(-00000);
			popupWindow.setBackgroundDrawable(dw);
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.update();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	/*
	 * 容器Activity必须实现这个接口，用来传递消息
	 */
	public interface OnFragmeng2ActivityListener {
		public void onArticleSelected(ActivityClass activity);
	}

	/*
	 * 定义ViewBinder，ListView中转换时间数据
	 */
	static final ViewBinder LIST_VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() == R.id.txtDateTime) {
				String date = cursor.getString(columnIndex);
				String time = cursor.getString(columnIndex + 1);
				((TextView) view).setText("时间：" + date + " " + time);
				return true;
			} else if (view.getId() == R.id.txtLocation) {
				String place = cursor.getString(columnIndex);
				((TextView) view).setText("地点：" + place);
				return true;
			} else {
				return false;
			}
		}

	};

	/*
	 * popupwindow绑数据
	 */
	private void bindPopupData(final long id, View view,
			final PopupWindow popupwindow) {
		Cursor detailCursor = null;
		try {
			TextView txtDes = (TextView) view.findViewById(R.id.txtDecription);
			TextView txtTitle = (TextView) view
					.findViewById(R.id.txtActivityTitle);
			TextView txtSpeak = (TextView) view.findViewById(R.id.txtSpeaker);
			TextView txtSpeakTitle = (TextView) view
					.findViewById(R.id.txtSpeakerTitle);
			TextView txtDate = (TextView) view.findViewById(R.id.txtDate);
			TextView txtPlace = (TextView) view.findViewById(R.id.txtPlace);
			ImageView mapImageView = (ImageView) view
					.findViewById(R.id.imageMap);
			mapImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					popupwindow.dismiss();
					onFragmeng2ActivityListener.onArticleSelected(activity);
				}
			});
			final Uri queryUri = Uri.parse(ActivityProvider.CONTENT_URI
					.toString() + "/" + id);
			detailCursor = activityProvider.query(queryUri, null, null, null,
					getOrderBy());
			if (detailCursor.moveToFirst()) {
				activity.setActivityId((int) id);
				activity.setActivityName(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_NAME)));
				activity.setActivityDate(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_DATE)));
				activity.setActivityTime(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_TIME)));
				activity.setActivityLocation(detailCursor
						.getString(detailCursor
								.getColumnIndex(ActivityData.C_LOCATION)));
				activity.setActivityBuilding(detailCursor.getInt(detailCursor
						.getColumnIndex(ActivityData.C_BUILDING)));
				activity.setActivityType(detailCursor.getInt(detailCursor
						.getColumnIndex(ActivityData.C_TYPE)));
				activity.setActivitySpeaker(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_SPEAKER)));
				activity.setActivitySpeakerTitle(detailCursor
						.getString(detailCursor
								.getColumnIndex(ActivityData.C_SPEAKERTITLE)));
				activity.setActivityDescription(detailCursor
						.getString(detailCursor
								.getColumnIndex(ActivityData.C_DESCRIPTION)));
				txtTitle.setText(activity.getActivityName());
				txtSpeak.setText(activity.getActivitySpeaker());
				txtSpeakTitle.setText(activity.getActivitySpeakerTitle());
				txtDate.setText(activity.getActivityDate() + " "
						+ activity.getActivityTime());
				txtPlace.setText(activity.getActivityLocation());
				txtDes.setText(activity.getActivityDescription());
				if (detailCursor.getInt(detailCursor
						.getColumnIndex(ActivityData.C_ISLIKE)) == 1) {
					activity.setActivityIsLike(true);
				} else {
					activity.setActivityIsLike(false);
				}
			}
			final ImageView likeImageView = (ImageView) view
					.findViewById(R.id.imageLike);
			if (activity.isActivityIsLike()) {
				likeImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_rate));
			}
			likeImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (activity.isActivityIsLike()) {
						likeImageView.setImageDrawable(getResources()
								.getDrawable(R.drawable.ic_unrate));
						ContentValues values = new ContentValues();
						values.put(ActivityData.C_ISLIKE, 0);
						int num = activityProvider.update(queryUri, values,
								null, null);
						Log.d(TAG, num + " rows changed");
					} else {
						likeImageView.setImageDrawable(getResources()
								.getDrawable(R.drawable.ic_rate));
						ContentValues values = new ContentValues();
						values.put(ActivityData.C_ISLIKE, 1);
						int num = activityProvider.update(queryUri, values,
								null, null);
						Log.d(TAG, num + " rows changed");
					}
					Intent intent = new Intent();
					intent.setAction(LbsService.NEW_STATUS_INTENT);
					getActivity().sendBroadcast(intent);
				}
			});
			txtDes.setMovementMethod(ScrollingMovementMethod.getInstance());
			if (!detailCursor.isClosed()) {
				detailCursor.close();
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			LbsApplication.getActivityData().closeDatabase();
		}
	}
}

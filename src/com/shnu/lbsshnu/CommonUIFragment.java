package com.shnu.lbsshnu;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class CommonUIFragment extends Fragment {
	private static final String TAG = "CommonUI";
	private ListView activityListView;
	private View rootView;
	private Cursor cursor;
	private boolean isLike = false;
	private int index;
	private int buildingNum = 0;
	private SimpleCursorAdapter adapter;
	private final String[] FROM = { ActivityData.C_NAME,
			ActivityData.C_LOCATION, ActivityData.C_DATE };
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime };
	private OnFragmeng2ActivityListener onFragmeng2ActivityListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		index = getArguments().getInt("Index");
		rootView = inflater
				.inflate(R.layout.activitylistview, container, false);
		activityListView = (ListView) rootView.findViewById(R.id.activityList);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			onFragmeng2ActivityListener = (OnFragmeng2ActivityListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initCursorLoader(FROM, getQuerySection(index));
		try {
			ActivityProvider activityProvider = new ActivityProvider();
			cursor = activityProvider.query(ActivityProvider.CONTENT_URI, null,
					getQuerySection(index), null, getOrderBy());
			int num = cursor.getCount();
			Log.i(TAG, "ActivityProvider cursor" + num);
			adapter = new SimpleCursorAdapter(LBSApplication.getContext(),
					R.layout.row, cursor, FROM, TO,
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			adapter.setViewBinder(LIST_VIEW_BINDER);
			activityListView.setAdapter(adapter);
			activityListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// Toast.makeText(getActivity(), position + " , " + id,
					// Toast.LENGTH_SHORT).show();
					showPopupwindows(id);
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LBSApplication.getActivityData().closeDatabase();
	}

	public void initCursorLoader(final String[] projection,
			final String selection) {
		getActivity().getSupportLoaderManager().initLoader(0, null,
				new LoaderCallbacks<Cursor>() {

					@Override
					public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
						Log.d(TAG, "on create loader");
						CursorLoader cursorLoader = new CursorLoader(
								getActivity(), ActivityProvider.CONTENT_URI,
								projection, selection, null, getOrderBy());
						return cursorLoader;
					}

					@Override
					public void onLoadFinished(Loader<Cursor> loader,
							Cursor cursor) {
						if (cursor != null) {
							adapter.swapCursor(cursor);
						}
					}

					@Override
					public void onLoaderReset(Loader<Cursor> loader) {
						// TODO Auto-generated method stub
						Log.d(TAG, "on loader reset");
						adapter.swapCursor(null);
					}
				});
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
					LBSApplication.getScreenWidth() * 3 / 4,
					LBSApplication.getScreenHeight() * 3 / 4, true);
			bindPopupData(id, view, popupWindow);
			popupWindow
					.setBackgroundDrawable(new BitmapDrawable(getResources()));
			popupWindow.showAtLocation(rootView, Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.setAnimationStyle(R.anim.popupanimation);
			ColorDrawable dw = new ColorDrawable(-00000);
			popupWindow.setBackgroundDrawable(dw);
			popupWindow.update();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	/*
	 * 容器Activity必须实现这个接口，用来传递消息
	 */
	public interface OnFragmeng2ActivityListener {
		public void onArticleSelected(long position);
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
	private void bindPopupData(long id, View view, final PopupWindow popupwindow) {
		try {
			TextView txtDes = (TextView) view.findViewById(R.id.txtDecription);
			TextView txtTitle = (TextView) view
					.findViewById(R.id.txtActivityTitle);
			TextView txtSpeak = (TextView) view.findViewById(R.id.txtSpeaker);
			TextView txtSpeakTitle = (TextView) view
					.findViewById(R.id.txtSpeakerTitle);
			TextView txtDate = (TextView) view.findViewById(R.id.txtDate);
			TextView txtPlace = (TextView) view.findViewById(R.id.txtPlace);
			final ActivityProvider activityProvider = new ActivityProvider();
			ImageView mapImageView = (ImageView) view
					.findViewById(R.id.imageMap);
			mapImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onFragmeng2ActivityListener.onArticleSelected(buildingNum);
					popupwindow.dismiss();
				}
			});
			final Uri queryUri = Uri.parse(ActivityProvider.CONTENT_URI
					.toString() + "/" + id);
			Cursor detailCursor = activityProvider.query(queryUri, null, null,
					null, getOrderBy());
			if (detailCursor.moveToFirst()) {
				txtTitle.setText(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_NAME)));
				txtSpeak.setText(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_SPEAKER)));
				txtSpeakTitle.setText(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_SPEAKERTITLE)));
				txtDate.setText(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_DATE))
						+ " "
						+ cursor.getString(detailCursor
								.getColumnIndex(ActivityData.C_TIME)));
				txtPlace.setText(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_LOCATION)));
				txtDes.setText(detailCursor.getString(detailCursor
						.getColumnIndex(ActivityData.C_DESCRIPTION)));
				buildingNum = detailCursor.getInt(detailCursor
						.getColumnIndex(ActivityData.C_BUILDING));
				if (detailCursor.getInt(detailCursor
						.getColumnIndex(ActivityData.C_ISLIKE)) == 1) {
					isLike = true;
				}
			}
			detailCursor.close();
			final ImageView likeImageView = (ImageView) view
					.findViewById(R.id.imageLike);
			if (isLike) {
				likeImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_rate));
			}
			likeImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (isLike) {
						likeImageView.setImageDrawable(getResources()
								.getDrawable(R.drawable.ic_unrate));
						ContentValues values = new ContentValues();
						values.put(ActivityData.C_ISLIKE, 0);
						int num = activityProvider.update(queryUri, values,
								null, null);
						Log.d(TAG, num + " rows changed");
						isLike = false;
					} else {
						likeImageView.setImageDrawable(getResources()
								.getDrawable(R.drawable.ic_rate));
						ContentValues values = new ContentValues();
						values.put(ActivityData.C_ISLIKE, 1);
						int num = activityProvider.update(queryUri, values,
								null, null);
						Log.d(TAG, num + " rows changed");
						isLike = true;
					}
				}
			});
			txtDes.setMovementMethod(ScrollingMovementMethod.getInstance());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
}

package com.shnu.lbsshnu;

import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class CommonUIFragment extends Fragment {
	private static final String TAG = "CommonUI";
	private ListView activityListView;
	private View rootView;
	private Cursor cursor;
	private int index;
	private CursorAdapter adapter;
	private final String[] FROM = { ActivityData.C_NAME,
			ActivityData.C_SPEAKER, ActivityData.C_LOCATION };
	private final int[] TO = { R.id.txtTitle, R.id.txtDateTime,
			R.id.txtLocation };

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
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initCursorLoader();
		try {
			ActivityProvider activityProvider = new ActivityProvider();
			cursor = activityProvider.query(ActivityProvider.CONTENT_URI, null,
					getQuerySection(index), null, null);
			int num = cursor.getCount();
			Log.i(TAG, "ActivityProvider cursor" + num);
			adapter = new SimpleCursorAdapter(LBSApplication.getContext(),
					R.layout.row, cursor, FROM, TO,
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			activityListView.setAdapter(adapter);
			activityListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Toast.makeText(getActivity(), position + " , " + id,
							Toast.LENGTH_SHORT).show();
					showPopupwindows();
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

	public void initCursorLoader() {
		getActivity().getSupportLoaderManager().initLoader(0, null,
				new LoaderCallbacks<Cursor>() {

					@Override
					public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
						Log.d(TAG, "on create loader");
						CursorLoader cursorLoader = new CursorLoader(
								getActivity(), ActivityProvider.CONTENT_URI,
								FROM, getQuerySection(index), null, null);
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
					+ ActivityData.C_TYPE + " = 2";
			return sql;
		case 1:
			sql = ActivityData.C_DATE + " > (SELECT DATE('now')) and "
					+ ActivityData.C_DATE
					+ " < (SELECT DATE('now', '+7 day')) and "
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
	 * 显示popupwindow
	 */
	@SuppressWarnings({ "static-access", "deprecation" })
	private void showPopupwindows() {
		PopupWindow popupWindow;
		try {
			LayoutInflater layoutInflater = (LayoutInflater) getActivity()
					.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.popupwindow, null);
			popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true);
			popupWindow
					.setBackgroundDrawable(new BitmapDrawable(getResources()));
			popupWindow.showAtLocation(rootView, Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.setAnimationStyle(R.anim.popupanimation);
			// 加上下面两行可以用back键关闭popupwindow，否则必须调用dismiss();
			ColorDrawable dw = new ColorDrawable(-00000);
			popupWindow.setBackgroundDrawable(dw);
			popupWindow.update();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
}

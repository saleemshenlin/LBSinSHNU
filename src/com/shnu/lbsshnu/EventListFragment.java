package com.shnu.lbsshnu;

import com.shnu.lbsshnu.Query.OnFragmengToActivityListener;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * EventListView的Fragment，用于显示每一个Tab
 */
public class EventListFragment extends Fragment {
	private static final String TAG = "CommonUI";
	private ListView listEvent;
	/**
	 * Fragment的root view
	 */
	private View rootView;
	private int intIndex;
	private SimpleCursorAdapter adapter;
	private final String[] FROM = { EventData.C_NAME, EventData.C_LOCATION,
			EventData.C_DATE };
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime };
	private EventProvider mEventProvider;
	private Query mQuery;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		intIndex = getArguments().getInt("Index");
		rootView = inflater.inflate(R.layout.event_list_view, container, false);
		listEvent = (ListView) rootView.findViewById(R.id.listEvent);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mQuery = new Query();
		try {
			mQuery.onFragmengToActivityListener = (OnFragmengToActivityListener) activity;
			mEventProvider = new EventProvider();
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
		super.onStart();
		Cursor itemCursor = null;
		try {
			itemCursor = mEventProvider.query(EventProvider.CONTENT_URI, null,
					mQuery.getSectionViaType(intIndex), null,
					mQuery.getSortOrder());
			int num = itemCursor.getCount();
			Log.i(TAG, "ActivityProvider cursor" + num);
			adapter = new SimpleCursorAdapter(LbsApplication.getContext(),
					R.layout.row, itemCursor, FROM, TO,
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			adapter.setViewBinder(LIST_VIEW_BINDER);
			listEvent.setAdapter(adapter);
			listEvent.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mQuery.showPopupwindows(getActivity(), rootView, id,
							!EventListView.isFromWidget);
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (itemCursor.isClosed()) {
				itemCursor.close();
			}
			LbsApplication.getActivityData().closeDatabase();
		}
		if (EventListView.intEventId != 0) {
			mQuery.showPopupwindows(getActivity(), rootView,
					EventListView.intEventId, !EventListView.isFromWidget);
			EventListView.intEventId = 0;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/*
	 * 定义ViewBinder，ListView中转换时间数据
	 */
	private static final ViewBinder LIST_VIEW_BINDER = new ViewBinder() {

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

}

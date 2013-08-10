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
 * 类EventListFragment<br>
 * 用于EventListView的Fragment,用于显示EventList,每个Fragment代表EventListView的一个Tab
 */
public class EventListFragment extends Fragment {
	/**
	 * 定义一个标签,在LogCat内表示EventListFragment
	 */
	private static final String TAG = "EventListFragment";
	/**
	 * ListView实例,用于显示Event列表
	 */
	private ListView listEvent;
	/**
	 * 定义Fragment的根view
	 */
	private View rootView;
	/**
	 * 用于表示在intent中传入的Tab数
	 */
	private int intIndex;
	/**
	 * 实例一个SimpleCursorAdapter,用与给listEvent绑定数据
	 */
	private SimpleCursorAdapter mSimpleCursorAdapter;
	/**
	 * 定义一个String[],用于指明绑定数据的哪些字段<br>
	 * C_NAME,C_LOCATION,C_DATE
	 */
	private final String[] FROM = { EventData.C_NAME, EventData.C_LOCATION,
			EventData.C_DATE };
	/**
	 * 定义一个int[],对应row.xml中的控件id,分别映射FROM中的元素
	 */
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime };
	/**
	 * 实例一个EventProvider,用于开启访问数据的入口
	 */
	private EventProvider mEventProvider;
	/**
	 * 实例一个Query
	 */
	private Query mQuery;

	/**
	 * 用于创建一个view<br>
	 * 1)获取Tab的Index <br>
	 * 2)设置listEvent的view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		intIndex = getArguments().getInt("Index");
		rootView = inflater.inflate(R.layout.event_list_fragment, container, false);
		listEvent = (ListView) rootView.findViewById(R.id.listEvent);
		return rootView;
	}

	/**
	 * 用于调用Query.onFragmengToActivityListener,
	 * 建立EventListFragment与EventListView直接的数据联系
	 */
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

	/**
	 * 给listEvent绑数据<br>
	 * 具体方法如下:<br>
	 * 1)建立查询,获取查询结果itemCursor,查询条件intIndex<br>
	 * 2)将itemCursor赋值给mSimpleCursorAdapter<br>
	 * 3)通过实例一个LIST_VIEW_BINDER绑定Event的时间和地点绑定数据<br>
	 * 4)设置点击listEvent中一个item的事件,点击item调用Query.initPopupwindows()<br>
	 * 5)最后根据EventListView.intEventId,判断是否需要显示Event详细(即调用Query.initPopupwindows(
	 * ))
	 */
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
			mSimpleCursorAdapter = new SimpleCursorAdapter(
					LbsApplication.getContext(), R.layout.row, itemCursor,
					FROM, TO, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			mSimpleCursorAdapter.setViewBinder(LIST_VIEW_BINDER);
			listEvent.setAdapter(mSimpleCursorAdapter);
			listEvent.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mQuery.initPopupwindows(getActivity(), rootView, id,
							!EventListActivity.isFromWidget);
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (itemCursor.isClosed()) {
				itemCursor.close();
			}
			LbsApplication.getEventData().closeDatabase();
		}
		if (EventListActivity.intEventId != 0) {
			mQuery.initPopupwindows(getActivity(), rootView,
					EventListActivity.intEventId,
					!EventListActivity.isFromWidget);
			EventListActivity.intEventId = 0;
		}
	}

	/**
	 * 定义一个常量,用于按给定的格式绑Event的时间和地点数据<br>
	 * 具体方法如下:<br>
	 * 1)当给控件txtDateTime绑数据时,按格式"时间：" + C_DATE + " " + C_TIME<br>
	 * 2)当给控件txtLocation绑数据时,按格式"地点：" +C_LOCATION
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

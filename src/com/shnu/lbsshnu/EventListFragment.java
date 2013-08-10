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
 * ��EventListFragment<br>
 * ����EventListView��Fragment,������ʾEventList,ÿ��Fragment����EventListView��һ��Tab
 */
public class EventListFragment extends Fragment {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾEventListFragment
	 */
	private static final String TAG = "EventListFragment";
	/**
	 * ListViewʵ��,������ʾEvent�б�
	 */
	private ListView listEvent;
	/**
	 * ����Fragment�ĸ�view
	 */
	private View rootView;
	/**
	 * ���ڱ�ʾ��intent�д����Tab��
	 */
	private int intIndex;
	/**
	 * ʵ��һ��SimpleCursorAdapter,�����listEvent������
	 */
	private SimpleCursorAdapter mSimpleCursorAdapter;
	/**
	 * ����һ��String[],����ָ�������ݵ���Щ�ֶ�<br>
	 * C_NAME,C_LOCATION,C_DATE
	 */
	private final String[] FROM = { EventData.C_NAME, EventData.C_LOCATION,
			EventData.C_DATE };
	/**
	 * ����һ��int[],��Ӧrow.xml�еĿؼ�id,�ֱ�ӳ��FROM�е�Ԫ��
	 */
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime };
	/**
	 * ʵ��һ��EventProvider,���ڿ����������ݵ����
	 */
	private EventProvider mEventProvider;
	/**
	 * ʵ��һ��Query
	 */
	private Query mQuery;

	/**
	 * ���ڴ���һ��view<br>
	 * 1)��ȡTab��Index <br>
	 * 2)����listEvent��view
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
	 * ���ڵ���Query.onFragmengToActivityListener,
	 * ����EventListFragment��EventListViewֱ�ӵ�������ϵ
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
	 * ��listEvent������<br>
	 * ���巽������:<br>
	 * 1)������ѯ,��ȡ��ѯ���itemCursor,��ѯ����intIndex<br>
	 * 2)��itemCursor��ֵ��mSimpleCursorAdapter<br>
	 * 3)ͨ��ʵ��һ��LIST_VIEW_BINDER��Event��ʱ��͵ص������<br>
	 * 4)���õ��listEvent��һ��item���¼�,���item����Query.initPopupwindows()<br>
	 * 5)������EventListView.intEventId,�ж��Ƿ���Ҫ��ʾEvent��ϸ(������Query.initPopupwindows(
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
	 * ����һ������,���ڰ������ĸ�ʽ��Event��ʱ��͵ص�����<br>
	 * ���巽������:<br>
	 * 1)�����ؼ�txtDateTime������ʱ,����ʽ"ʱ�䣺" + C_DATE + " " + C_TIME<br>
	 * 2)�����ؼ�txtLocation������ʱ,����ʽ"�ص㣺" +C_LOCATION
	 */
	private static final ViewBinder LIST_VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() == R.id.txtDateTime) {
				String date = cursor.getString(columnIndex);
				String time = cursor.getString(columnIndex + 1);
				((TextView) view).setText("ʱ�䣺" + date + " " + time);
				return true;
			} else if (view.getId() == R.id.txtLocation) {
				String place = cursor.getString(columnIndex);
				((TextView) view).setText("�ص㣺" + place);
				return true;
			} else {
				return false;
			}
		}

	};

}

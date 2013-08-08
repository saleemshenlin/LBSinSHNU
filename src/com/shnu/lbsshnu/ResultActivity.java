package com.shnu.lbsshnu;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shnu.lbsshnu.Query.Place;
import com.supermap.data.GeoRegion;

/**
 * 查询结果显示Activity，分为缓冲区查询和普通查询，定位开启时调用缓存区查询。/n 缓存区查询先创建缓冲区Query.getQueryBuffer()
 * 查出缓冲区内的地点Query.queryViaBuffer()
 * 再根据每个结果进行查询getQueryResult获得每个结果SimpleCursorAdapter
 * 再把每个地点的名称和距离+这个地点SimpleCursorAdapter组合在一起，并存入SectionedAdapter
 * ListView绑上SectionedAdapter。/n 普通查询先根据类别"我关注的", "电影演出", "学术讲座", "精品课程"
 * 进行getQueryResult获得SimpleCursorAdapter
 * 再把每个类别的名称+这个这个SimpleCursorAdapter组合在一起，并存入SectionedAdapter
 * ListView绑上SectionedAdapter。
 */
public class ResultActivity extends BaseActivity {
	private static final String TAG = "QueryResult";
	/**
	 * 使用者输入的查询内容
	 */
	private static String strQuery = "";
	private SimpleCursorAdapter adapter;
	private View rootView;
	private final String[] FROM = { EventData.C_NAME, EventData.C_LOCATION,
			EventData.C_DATE, EventData.C_ID };
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime, R.id.txtId };
	/**
	 * 查询结果的ListView
	 */
	private ListView listQueryResult;
	/**
	 * 沒有数据时ListAdapter
	 */
	private NullResultAdapter nullResultAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultactivity_view);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		initResultActionbar(false);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		rootView = layoutInflater.inflate(R.layout.resultactivity_view, null);
		// 每次onCreate都要清空EventList
		if (!events.isEmpty()) {
			events = new ArrayList<Event>();
		}
		strQuery = getIntent().getStringExtra("QueryString");
		listQueryResult = (ListView) findViewById(R.id.listQeuryResult);
		nullResultAdapter = new NullResultAdapter(LbsApplication.getContext());
		if (getIntent().getAction() != null) {
			Log.d(TAG, getIntent().getAction());
			queryViaLocation();
		} else {
			queryViaNormal();
			swtResult.setEnabled(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 根据查询内容进行查询并生成每个组的SimpleCursorAdapter
	 * 
	 * @param seclection
	 *            查询内容
	 * @param itemCursor
	 *            外部传入Cursor 方便销毁
	 * @return SimpleCursorAdapter
	 */
	private SimpleCursorAdapter getQueryResult(String seclection,
			Cursor itemCursor) {
		EventProvider mEventProvider = new EventProvider();
		itemCursor = mEventProvider.query(EventProvider.CONTENT_URI, null,
				seclection, null, mQuery.getSortOrder());
		int sum = itemCursor.getCount();
		if (sum > 0) {
			itemCursor.moveToFirst();
			Event mEvent = new Event();
			int columnIndex = itemCursor.getColumnIndex(EventData.C_BUILDING);
			mEvent.setEventBuilding(itemCursor.getInt(columnIndex));
			events.add(mEvent);
		}
		adapter = new SimpleCursorAdapter(LbsApplication.getContext(),
				R.layout.row, itemCursor, FROM, TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(LIST_VIEW_BINDER);
		return adapter;
	}

	/**
	 * 缓冲区查询
	 */
	private void queryViaLocation() {
		Cursor itemCursor = null;
		try {
			GeoRegion mGeoRegion = mQuery.getQueryBuffer();
			List<Place> places = mQuery.queryViaBuffer(mGeoRegion);
			if (!places.isEmpty()) {
				for (Place place : places) {
					String caption = place.strBuildingName;
					String distance = place.strDistance + "m";
					String selection = mQuery.getQuerySection(place, strQuery);
					SimpleCursorAdapter adapter = getQueryResult(selection,
							itemCursor);
					if (adapter.getCount() > 0) {
						sectionedAdapter.addSection(caption, distance, adapter);
					} else {
						sectionedAdapter.addSection(caption, distance,
								nullResultAdapter);
					}
				}
				listQueryResult.setAdapter(sectionedAdapter);
			} else {
				listQueryResult.setAdapter(nullResultAdapter);
			}
			listQueryResult.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView txtView = (TextView) view.findViewById(R.id.txtId);
					Log.i(TAG, txtView.getText().toString());
					long activityId = Long.parseLong(txtView.getText()
							.toString());
					mQuery.showPopupwindows(ResultActivity.this, rootView,
							activityId, false);
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (itemCursor != null) {
				itemCursor.close();
			}
			LbsApplication.getActivityData().closeDatabase();
		}

	}

	/**
	 * 普通查询
	 */
	private void queryViaNormal() {
		Cursor itemCursor = null;
		try {
			String[] items = { "我关注的", "电影演出", "学术讲座", "精品课程" };
			for (int i = 0; i < items.length; i++) {
				String caption = items[i];
				String distance = "";
				String selection = mQuery.getQuerySection(i, strQuery);
				SimpleCursorAdapter adapter = getQueryResult(selection,
						itemCursor);
				if (adapter.getCount() > 0) {
					sectionedAdapter.addSection(caption, distance, adapter);
				} else {
					sectionedAdapter.addSection(caption, distance,
							nullResultAdapter);
				}
			}
			listQueryResult.setAdapter(sectionedAdapter);
			listQueryResult.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView txtView = (TextView) view.findViewById(R.id.txtId);
					if (txtView != null) {
						long activityId = Long.parseLong(txtView.getText()
								.toString());
						mQuery.showPopupwindows(ResultActivity.this, rootView,
								activityId, false);
					}
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (itemCursor != null)
				itemCursor.close();
			LbsApplication.getActivityData().closeDatabase();
		}
	}

	/**
	 * 此类用于绑定查询结果所在位置的名称和距离
	 */
	private SectionedAdapter sectionedAdapter = new SectionedAdapter() {
		protected View getHeaderView(String caption, String distance,
				int index, View convertView, ViewGroup parent) {
			View result = null;
			try {
				LayoutInflater inflater = (LayoutInflater) LbsApplication
						.getContext().getSystemService(
								Context.LAYOUT_INFLATER_SERVICE);
				result = inflater.inflate(R.layout.query_result_list_header,
						null);
				TextView titleTextView = (TextView) result
						.findViewById(R.id.txtHeader);
				TextView distanceTextView = (TextView) result
						.findViewById(R.id.txtDistance);
				titleTextView.setText(caption);
				distanceTextView.setText(distance);
			} catch (Exception e) {
				Log.e("Tag", e.toString());
			}
			return result;
		}
	};

	/**
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

	/**
	 * 此类用于查询结果为null的时候
	 */
	private class NullResultAdapter extends BaseAdapter {
		private LayoutInflater inflater = null;

		private NullResultAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem(int positon) {
			return positon;
		}

		@Override
		public long getItemId(int id) {
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView result = (TextView) convertView;
			try {
				if (convertView == null) {
					result = (TextView) inflater.inflate(
							R.layout.null_result_row, null);
				}
				result.setText("对不起，未找到信息！");
			} catch (Exception e) {
				Log.e("Tag", e.toString());
			}
			return (result);
		}

	}

}
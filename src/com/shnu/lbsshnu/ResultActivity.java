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
 * 类ResultActivity<br>
 * 用于显示查询结果的Activity，分为缓冲区查询和普通查询 <br>
 * 定位开启时调用缓存区查询,未开启时调用普通查询<br>
 */
public class ResultActivity extends BaseActivity {
	/**
	 * 定义一个标签,在LogCat内表示Result
	 */
	private static final String TAG = "ResultActivity";
	/**
	 * 定义一个String[],用于指明绑定数据的哪些字段<br>
	 * C_NAME,C_LOCATION,C_DATE,C_ID
	 */
	private final String[] FROM = { EventData.C_NAME, EventData.C_LOCATION,
			EventData.C_DATE, EventData.C_ID };
	/**
	 * 定义一个int[],对应row.xml中的控件id,分别映射FROM中的元素
	 */
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime, R.id.txtId };
	/**
	 * 定义一个常量,用了使用者输入的查询内容
	 */
	private static String strQuery = "";
	/**
	 * 实例一个SimpleCursorAdapter
	 */
	private SimpleCursorAdapter mSimpleCursorAdapter;
	/**
	 * 实例一个View,用于PopupWindow的根View
	 */
	private View rootView;

	/**
	 * 实例一个ListView,用于表示查询结果的ListView
	 */
	private ListView listQueryResult;
	/**
	 * 实例一个NullResultAdapter,用于沒有数据时ListAdapter
	 */
	private NullResultAdapter nullResultAdapter;

	/**
	 * 创建一个ResultActivity<br>
	 * 1)初始化ActionBar; <br>
	 * 2)给rootView绑定layout;<br>
	 * 3)清空events,防止"Name is existed"<br>
	 * 4)根据是否开启定位,调用queryViaLocation()执行缓冲区查询<br>
	 * 5)或者调用queryViaNormal();执行普通查询<br>
	 * 6)在普通查询下,无法使用地图显示查询结果
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultactivity_view);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		initResultActionbar(false);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		rootView = layoutInflater.inflate(R.layout.resultactivity_view, null);
		if (!events.isEmpty()) {
			events = new ArrayList<Event>();
		}
		strQuery = getIntent().getStringExtra("QueryString");
		listQueryResult = (ListView) findViewById(R.id.listQeuryResult);
		nullResultAdapter = new NullResultAdapter(LbsApplication.getContext());
		if (LbsApplication.isQueryViaLocation) {
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
	 * 用于根据查询内容进行查询并生成每个组的SimpleCursorAdapter<br>
	 * 具体方法如下:<br>
	 * 1)获取查询结果itemCursor<br>
	 * 2)将查询结果赋值给Event<br>
	 * 3)将Event存入List<Event>中<br>
	 * 4)给SimpleCursorAdapter绑定数据并返回<br>
	 * 
	 * @param seclection
	 *            查询内容
	 * @param itemCursor
	 *            外部传入Cursor 方便销毁
	 * @return SimpleCursorAdapter 绑定数据的SimpleCursorAdapter
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
		mSimpleCursorAdapter = new SimpleCursorAdapter(
				LbsApplication.getContext(), R.layout.row, itemCursor, FROM,
				TO, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mSimpleCursorAdapter.setViewBinder(LIST_VIEW_BINDER);
		return mSimpleCursorAdapter;
	}

	/**
	 * 用于缓冲区查询<br>
	 * 具体方法如下:<br>
	 * 1)调用Query.getQueryBuffer()创建缓冲区<br>
	 * 2)调用Query.queryViaBuffer()查询符合条件的Place<br>
	 * 3)遍历每个Place,调用getQueryResult()对每个Place进行查询,并将结果绑入SimpleCursorAdapter<br>
	 * 4)如果没有结果调用nullResultAdapter<br>
	 * 5)将每个Place和其SimpleCursorAdapter作为一个组<br>
	 * 6)调用SectionedAdapter.addSection(),将每个组加入到SectionedAdapter中<br>
	 * 7)listQueryResult绑上listQueryResult,并设置item的onClick事件,显示Event的详细<br>
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
					if (txtView != null) {
						long activityId = Long.parseLong(txtView.getText()
								.toString());
						mQuery.initPopupwindows(ResultActivity.this, rootView,
								activityId, false);
					}
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (itemCursor != null) {
				itemCursor.close();
			}
			LbsApplication.getEventData().closeDatabase();
		}

	}

	/**
	 * 用于普通查询<br>
	 * 具体方法如下: <br>
	 * 1)普通查询先根据类别"我关注的", "电影演出", "学术讲座", "精品课程"<br>
	 * 2)调用getQueryResult()对每个类别进行查询,并将结果绑入SimpleCursorAdapter<br>
	 * 3)如果没有结果调用nullResultAdapter<br>
	 * 4)将每个类别和其SimpleCursorAdapter作为一个组<br>
	 * 5)调用SectionedAdapter.addSection(),将每个组加入到SectionedAdapter中<br>
	 * 6)listQueryResult绑上listQueryResult,并设置item的onClick事件,显示Event的详细<br>
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
						mQuery.initPopupwindows(ResultActivity.this, rootView,
								activityId, false);
					}
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (itemCursor != null)
				itemCursor.close();
			LbsApplication.getEventData().closeDatabase();
		}
	}

	/**
	 * 定义一个SectionedAdapter <br>
	 * 用于在将每个组加入到SectionedAdapter时,绑定每个组的组名<br>
	 * 如果是缓冲区查询,需附上Place与当前位置间的距离
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

	/**
	 * 类NullResultAdapter<br>
	 * 用于查询结果为null的时候
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
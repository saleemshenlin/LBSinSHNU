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
 * ��ResultActivity<br>
 * ������ʾ��ѯ�����Activity����Ϊ��������ѯ����ͨ��ѯ <br>
 * ��λ����ʱ���û�������ѯ,δ����ʱ������ͨ��ѯ<br>
 */
public class ResultActivity extends BaseActivity {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾResult
	 */
	private static final String TAG = "ResultActivity";
	/**
	 * ����һ��String[],����ָ�������ݵ���Щ�ֶ�<br>
	 * C_NAME,C_LOCATION,C_DATE,C_ID
	 */
	private final String[] FROM = { EventData.C_NAME, EventData.C_LOCATION,
			EventData.C_DATE, EventData.C_ID };
	/**
	 * ����һ��int[],��Ӧrow.xml�еĿؼ�id,�ֱ�ӳ��FROM�е�Ԫ��
	 */
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime, R.id.txtId };
	/**
	 * ����һ������,����ʹ��������Ĳ�ѯ����
	 */
	private static String strQuery = "";
	/**
	 * ʵ��һ��SimpleCursorAdapter
	 */
	private SimpleCursorAdapter mSimpleCursorAdapter;
	/**
	 * ʵ��һ��View,����PopupWindow�ĸ�View
	 */
	private View rootView;

	/**
	 * ʵ��һ��ListView,���ڱ�ʾ��ѯ�����ListView
	 */
	private ListView listQueryResult;
	/**
	 * ʵ��һ��NullResultAdapter,���ڛ]������ʱListAdapter
	 */
	private NullResultAdapter nullResultAdapter;

	/**
	 * ����һ��ResultActivity<br>
	 * 1)��ʼ��ActionBar; <br>
	 * 2)��rootView��layout;<br>
	 * 3)���events,��ֹ"Name is existed"<br>
	 * 4)�����Ƿ�����λ,����queryViaLocation()ִ�л�������ѯ<br>
	 * 5)���ߵ���queryViaNormal();ִ����ͨ��ѯ<br>
	 * 6)����ͨ��ѯ��,�޷�ʹ�õ�ͼ��ʾ��ѯ���
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
	 * ���ڸ��ݲ�ѯ���ݽ��в�ѯ������ÿ�����SimpleCursorAdapter<br>
	 * ���巽������:<br>
	 * 1)��ȡ��ѯ���itemCursor<br>
	 * 2)����ѯ�����ֵ��Event<br>
	 * 3)��Event����List<Event>��<br>
	 * 4)��SimpleCursorAdapter�����ݲ�����<br>
	 * 
	 * @param seclection
	 *            ��ѯ����
	 * @param itemCursor
	 *            �ⲿ����Cursor ��������
	 * @return SimpleCursorAdapter �����ݵ�SimpleCursorAdapter
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
	 * ���ڻ�������ѯ<br>
	 * ���巽������:<br>
	 * 1)����Query.getQueryBuffer()����������<br>
	 * 2)����Query.queryViaBuffer()��ѯ����������Place<br>
	 * 3)����ÿ��Place,����getQueryResult()��ÿ��Place���в�ѯ,�����������SimpleCursorAdapter<br>
	 * 4)���û�н������nullResultAdapter<br>
	 * 5)��ÿ��Place����SimpleCursorAdapter��Ϊһ����<br>
	 * 6)����SectionedAdapter.addSection(),��ÿ������뵽SectionedAdapter��<br>
	 * 7)listQueryResult����listQueryResult,������item��onClick�¼�,��ʾEvent����ϸ<br>
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
	 * ������ͨ��ѯ<br>
	 * ���巽������: <br>
	 * 1)��ͨ��ѯ�ȸ������"�ҹ�ע��", "��Ӱ�ݳ�", "ѧ������", "��Ʒ�γ�"<br>
	 * 2)����getQueryResult()��ÿ�������в�ѯ,�����������SimpleCursorAdapter<br>
	 * 3)���û�н������nullResultAdapter<br>
	 * 4)��ÿ��������SimpleCursorAdapter��Ϊһ����<br>
	 * 5)����SectionedAdapter.addSection(),��ÿ������뵽SectionedAdapter��<br>
	 * 6)listQueryResult����listQueryResult,������item��onClick�¼�,��ʾEvent����ϸ<br>
	 */
	private void queryViaNormal() {
		Cursor itemCursor = null;
		try {
			String[] items = { "�ҹ�ע��", "��Ӱ�ݳ�", "ѧ������", "��Ʒ�γ�" };
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
	 * ����һ��SectionedAdapter <br>
	 * �����ڽ�ÿ������뵽SectionedAdapterʱ,��ÿ���������<br>
	 * ����ǻ�������ѯ,�踽��Place�뵱ǰλ�ü�ľ���
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

	/**
	 * ��NullResultAdapter<br>
	 * ���ڲ�ѯ���Ϊnull��ʱ��
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
				result.setText("�Բ���δ�ҵ���Ϣ��");
			} catch (Exception e) {
				Log.e("Tag", e.toString());
			}
			return (result);
		}

	}

}
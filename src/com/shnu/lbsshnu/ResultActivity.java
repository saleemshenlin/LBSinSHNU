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
 * ��ѯ�����ʾActivity����Ϊ��������ѯ����ͨ��ѯ����λ����ʱ���û�������ѯ��/n ��������ѯ�ȴ���������Query.getQueryBuffer()
 * ����������ڵĵص�Query.queryViaBuffer()
 * �ٸ���ÿ��������в�ѯgetQueryResult���ÿ�����SimpleCursorAdapter
 * �ٰ�ÿ���ص�����ƺ;���+����ص�SimpleCursorAdapter�����һ�𣬲�����SectionedAdapter
 * ListView����SectionedAdapter��/n ��ͨ��ѯ�ȸ������"�ҹ�ע��", "��Ӱ�ݳ�", "ѧ������", "��Ʒ�γ�"
 * ����getQueryResult���SimpleCursorAdapter
 * �ٰ�ÿ����������+������SimpleCursorAdapter�����һ�𣬲�����SectionedAdapter
 * ListView����SectionedAdapter��
 */
public class ResultActivity extends BaseActivity {
	private static final String TAG = "QueryResult";
	/**
	 * ʹ��������Ĳ�ѯ����
	 */
	private static String strQuery = "";
	private SimpleCursorAdapter adapter;
	private View rootView;
	private final String[] FROM = { EventData.C_NAME, EventData.C_LOCATION,
			EventData.C_DATE, EventData.C_ID };
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime, R.id.txtId };
	/**
	 * ��ѯ�����ListView
	 */
	private ListView listQueryResult;
	/**
	 * �]������ʱListAdapter
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
		// ÿ��onCreate��Ҫ���EventList
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
	 * ���ݲ�ѯ���ݽ��в�ѯ������ÿ�����SimpleCursorAdapter
	 * 
	 * @param seclection
	 *            ��ѯ����
	 * @param itemCursor
	 *            �ⲿ����Cursor ��������
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
	 * ��������ѯ
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
	 * ��ͨ��ѯ
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
	 * �������ڰ󶨲�ѯ�������λ�õ����ƺ;���
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
	 * ����ViewBinder��ListView��ת��ʱ������
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
	 * �������ڲ�ѯ���Ϊnull��ʱ��
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
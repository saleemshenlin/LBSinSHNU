package com.shnu.lbsshnu;

import java.util.List;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shnu.lbsshnu.Query.Place;
import com.supermap.analyst.BufferAnalystGeometry;
import com.supermap.analyst.BufferAnalystParameter;
import com.supermap.analyst.BufferEndType;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometry;
import com.supermap.data.PrjCoordSys;

public class BufferQueryResult extends BaseActivity {
	private static final String TAG = "QueryResult";
	private static String QER_STRING = "";
	private Query bufferQuery = new Query();
	private SimpleCursorAdapter adapter;
	private final String[] FROM = { ActivityData.C_NAME,
			ActivityData.C_LOCATION, ActivityData.C_DATE, ActivityData.C_ID };
	private final int[] TO = { R.id.txtTitle, R.id.txtLocation,
			R.id.txtDateTime, R.id.txtId };
	private ListView queryList;
	private ActivityClass activity = new ActivityClass();
	private NullResultAdapter nullResultAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.queryresult);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		initResultBar("list");
		QER_STRING = getIntent().getStringExtra("QueryString");
		queryList = (ListView) findViewById(R.id.listResult);
		nullResultAdapter = new NullResultAdapter(LbsApplication.getContext());
		if (getIntent().getAction() != null) {
			Log.d(TAG, getIntent().getAction());
			queryViaLocation();
		} else {
			queryViaNormal();
			resultSwitch.setEnabled(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/*
	 * 绘制缓冲区半径1000m
	 */
	private GeoRegion addQueryBuffer() {
		GeoRegion geometryBuffer = new GeoRegion();
		try {
			BufferAnalystParameter bufferAnalystParam = new BufferAnalystParameter();
			bufferAnalystParam.setEndType(BufferEndType.ROUND);
			bufferAnalystParam.setLeftDistance(100);
			bufferAnalystParam.setRightDistance(100);
			Geometry geoForBuffer = new GeoPoint(
					LbsApplication.getLastlocationPoint2d());
			PrjCoordSys prj = LbsApplication.getmMapControl().getMap()
					.getPrjCoordSys();
			geometryBuffer = BufferAnalystGeometry.createBuffer(geoForBuffer,
					bufferAnalystParam, prj);
		} catch (Exception e) {
			Log.e("addQueryBuffer", e.toString());
		}
		return geometryBuffer;
	}

	private SimpleCursorAdapter getQueryResult(String seclection,
			Cursor itemCursor) {
		ActivityProvider activityProvider = new ActivityProvider();
		itemCursor = activityProvider.query(ActivityProvider.CONTENT_URI, null,
				seclection, null, getOrderBy());
		int sum = itemCursor.getCount();
		if (sum > 0) {
			itemCursor.moveToFirst();
			int columnIndex = itemCursor
					.getColumnIndex(ActivityData.C_BUILDING);
			int id = itemCursor.getInt(columnIndex);
			int num = sum;
			Result result = new Result(id, num);
			if (!isSearch) {
				results.add(result);
			}
		}
		adapter = new SimpleCursorAdapter(LbsApplication.getContext(),
				R.layout.row, itemCursor, FROM, TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(LIST_VIEW_BINDER);
		// itemCursor.close();
		return adapter;
	}

	private String getQuerySection(Place place) {
		String sql = null;
		sql = ActivityData.C_BUILDING + " = " + place.buildingNum + " and ";
		sql = sql + ActivityData.C_DATE + " > (SELECT DATE('now')) and "
				+ ActivityData.C_DATE
				+ " < (SELECT DATE('now', '+7 day')) and ";
		sql = sql + "( " + ActivityData.C_NAME + " Like '%" + QER_STRING
				+ "%' OR " + ActivityData.C_SPEAKER + " Like '%" + QER_STRING
				+ "%' OR " + ActivityData.C_SPEAKERTITLE + " Like '%"
				+ QER_STRING + "%' OR " + ActivityData.C_DESCRIPTION
				+ " Like '%" + QER_STRING + "%' )";
		return sql;
	}

	private String getQuerySection(int num) {
		String sql = null;
		sql = ActivityData.C_TYPE + " = " + num + " and ";
		sql = sql + ActivityData.C_DATE + " > (SELECT DATE('now')) and "
				+ ActivityData.C_DATE
				+ " < (SELECT DATE('now', '+7 day')) and ";
		sql = sql + "( " + ActivityData.C_NAME + " Like '%" + QER_STRING
				+ "%' OR " + ActivityData.C_SPEAKER + " Like '%" + QER_STRING
				+ "%' OR " + ActivityData.C_SPEAKERTITLE + " Like '%"
				+ QER_STRING + "%' OR " + ActivityData.C_DESCRIPTION
				+ " Like '%" + QER_STRING + "%' )";
		return sql;
	}

	private String getOrderBy() {
		String sql = null;
		sql = ActivityData.C_DATE + " ASC";
		return sql;
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

	SectionedAdapter sectionedAdapter = new SectionedAdapter() {
		protected View getHeaderView(String caption, String distance,
				int index, View convertView, ViewGroup parent) {
			View result = convertView;
			try {
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) LbsApplication
							.getContext().getSystemService(
									Context.LAYOUT_INFLATER_SERVICE);
					result = inflater.inflate(R.layout.listheader, null);
					TextView titleTextView = (TextView) result
							.findViewById(R.id.txtHeader);
					TextView distanceTextView = (TextView) result
							.findViewById(R.id.txtDistance);
					titleTextView.setText(caption);
					distanceTextView.setText(distance);
				}
			} catch (Exception e) {
				Log.e("Tag", e.toString());
			}
			return result;
		}
	};

	private void showPopupwindows(final long id) {
		try {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View view = layoutInflater.inflate(R.layout.popupwindow, null);
			View rootView = layoutInflater.inflate(R.layout.queryresult, null);
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
			popupWindow.setAnimationStyle(R.style.popupAnimation);
			popupWindow.update();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

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
			final ActivityProvider activityProvider = new ActivityProvider();
			ImageView mapImageView = (ImageView) view
					.findViewById(R.id.imageMap);
			mapImageView.setVisibility(View.GONE);
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
				}
			});
			txtDes.setMovementMethod(ScrollingMovementMethod.getInstance());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (detailCursor != null) {
				detailCursor.close();
			}
			LbsApplication.getActivityData().closeDatabase();
		}
	}

	private void queryViaLocation() {
		Cursor itemCursor = null;
		try {
			List<Place> places = bufferQuery.queryByBuffer(addQueryBuffer());
			if (!places.isEmpty()) {
				for (Place place : places) {
					String caption = place.buildingName;
					String distance = place.distance + "m";
					String selection = getQuerySection(place);
					SimpleCursorAdapter adapter = getQueryResult(selection,
							itemCursor);
					if (adapter.getCount() > 0) {
						sectionedAdapter.addSection(caption, distance, adapter);
					} else {
						sectionedAdapter.addSection(caption, distance,
								nullResultAdapter);
					}
				}
				queryList.setAdapter(sectionedAdapter);
			} else {
				queryList.setAdapter(nullResultAdapter);
			}
			queryList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView txtView = (TextView) view.findViewById(R.id.txtId);
					Log.i(TAG, txtView.getText().toString());
					long activityId = Long.parseLong(txtView.getText()
							.toString());
					showPopupwindows(activityId);
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

	private void queryViaNormal() {
		Cursor itemCursor = null;
		try {
			String[] items = { "电影演出", "学术讲座", "精品课程" };
			for (int i = 1; i <= items.length; i++) {
				String caption = items[i - 1];
				String distance = "";
				String selection = getQuerySection(i);
				SimpleCursorAdapter adapter = getQueryResult(selection,
						itemCursor);
				if (adapter.getCount() > 0) {
					sectionedAdapter.addSection(caption, distance, adapter);
				} else {
					sectionedAdapter.addSection(caption, distance,
							nullResultAdapter);
				}
			}
			queryList.setAdapter(sectionedAdapter);
			queryList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView txtView = (TextView) view.findViewById(R.id.txtId);
					if (txtView != null) {
						long activityId = Long.parseLong(txtView.getText()
								.toString());
						showPopupwindows(activityId);
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

	public static List<Result> getResults() {
		return results;
	}

	class NullResultAdapter extends BaseAdapter {
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
					result = (TextView) inflater.inflate(R.layout.nullresult,
							null);
				}
				result.setText("对不起，未找到信息！");
			} catch (Exception e) {
				Log.e("Tag", e.toString());
			}
			return (result);
		}

	}

}
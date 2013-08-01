package com.shnu.lbsshnu;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class LbsWidget extends AppWidgetProvider {
	private static final String TAG = "LbsWidget";
	private ActivityClass activity;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.d(TAG, "onUpdate");
		ActivityProvider activityProvider = new ActivityProvider();
		Cursor cursor = null;
		try {
			cursor = activityProvider.query(ActivityProvider.CONTENT_URI, null,
					getSelection(), null, getSortOrder());
			if (cursor.moveToFirst()) {
				activity = getActivityClass(cursor);
				CharSequence title = activity.getActivityName();
				CharSequence date = activity.getActivityDate();
				CharSequence time = activity.getActivityTime();
				int type = activity.getActivityType();
				for (int appWidgetId : appWidgetIds) {
					Log.d(TAG, "Updating widget: " + appWidgetId);
					RemoteViews views = new RemoteViews(
							context.getPackageName(), R.layout.lbswidget);
					if (title.length() > 10) {
						title = title.subSequence(0, 10) + "...";
					}
					views.setTextViewText(R.id.txtWTitle, title);
					views.setTextViewText(R.id.txtWDateTime, date + " " + time);
					switch (type) {
					case 1:
						views.setImageViewResource(R.id.imgWLocation,
								R.drawable.ic_play_pin);
						break;
					case 2:
						views.setImageViewResource(R.id.imgWLocation,
								R.drawable.ic_mic_pin);
						break;
					case 3:
						views.setImageViewResource(R.id.imgWLocation,
								R.drawable.ic_book_pin);
						break;
					default:
						break;
					}
					Log.e(TAG, title + " , " + date + " , " + time);
					setWidgetDetail(context, views, activity);
					setWidgetLocation(context, views, activity);
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString() + " " + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			LbsApplication.getActivityData().closeDatabase();
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		Log.d(TAG, "onReceive");
		if (intent.getAction().equals(LbsService.NEW_STATUS_INTENT)) {
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			ComponentName mComponentName = new ComponentName(context,
					LbsWidget.class);
			int[] appWidgets = appWidgetManager.getAppWidgetIds(mComponentName);
			if (appWidgets.length > 0) {
				this.onUpdate(context, appWidgetManager, appWidgets);
			}
		}
	}

	/*
	 * where 条件
	 */
	private String getSelection() {
		String sql = ActivityData.C_DATE + " > (SELECT DATE('now')) and "
				+ ActivityData.C_ISLIKE + " = 1";
		return sql;
	}

	/*
	 * order by条件
	 */
	private String getSortOrder() {
		String sql = null;
		sql = ActivityData.C_DATE + " ASC";
		return sql;
	}

	public LbsWidget() {
		// TODO Auto-generated constructor stub
	}

	private void setWidgetDetail(Context context, RemoteViews views,
			ActivityClass activity) {
		Intent intent = new Intent(context, ActivityListView.class);
		intent.setAction("Form_Widget");
		Bundle bundle = new Bundle();
		bundle.putParcelable("activity", activity);
		intent.putExtras(bundle);
		views.setOnClickPendingIntent(R.id.imgWDetail, PendingIntent
				.getActivity(context, LbsApplication.getRequestCode(), intent,
						PendingIntent.FLAG_CANCEL_CURRENT));
	}

	private void setWidgetLocation(Context context, RemoteViews views,
			ActivityClass activity) {
		Intent intent = new Intent(context, HomeActivity.class);
		intent.setAction("Form_Widget");
		Bundle bundle = new Bundle();
		bundle.putParcelable("activity", activity);
		intent.putExtras(bundle);
		views.setOnClickPendingIntent(R.id.imgWLocation, PendingIntent
				.getActivity(context, LbsApplication.getRequestCode(), intent,
						PendingIntent.FLAG_UPDATE_CURRENT));
	}

	private ActivityClass getActivityClass(Cursor detailCursor) {
		ActivityClass activity = new ActivityClass();
		activity.setActivityId(detailCursor.getInt(detailCursor
				.getColumnIndex(ActivityData.C_ID)));
		activity.setActivityName(detailCursor.getString(detailCursor
				.getColumnIndex(ActivityData.C_NAME)));
		activity.setActivityDate(detailCursor.getString(detailCursor
				.getColumnIndex(ActivityData.C_DATE)));
		activity.setActivityTime(detailCursor.getString(detailCursor
				.getColumnIndex(ActivityData.C_TIME)));
		activity.setActivityLocation(detailCursor.getString(detailCursor
				.getColumnIndex(ActivityData.C_LOCATION)));
		activity.setActivityBuilding(detailCursor.getInt(detailCursor
				.getColumnIndex(ActivityData.C_BUILDING)));
		activity.setActivityType(detailCursor.getInt(detailCursor
				.getColumnIndex(ActivityData.C_TYPE)));
		activity.setActivitySpeaker(detailCursor.getString(detailCursor
				.getColumnIndex(ActivityData.C_SPEAKER)));
		activity.setActivitySpeakerTitle(detailCursor.getString(detailCursor
				.getColumnIndex(ActivityData.C_SPEAKERTITLE)));
		activity.setActivityDescription(detailCursor.getString(detailCursor
				.getColumnIndex(ActivityData.C_DESCRIPTION)));
		return activity;
	}
}

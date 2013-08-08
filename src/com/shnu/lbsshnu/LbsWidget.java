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
	private Event mEvent;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.d(TAG, "onUpdate");
		EventProvider mEventProvider = new EventProvider();
		Query mQuery = new Query();
		Cursor cursor = null;
		try {
			cursor = mEventProvider.query(EventProvider.CONTENT_URI, null,
					mQuery.getSectionViaType(3), null, mQuery.getSortOrder());
			if (cursor.moveToFirst()) {
				mEvent = new Event(cursor);
				CharSequence charTitle = mEvent.getEventName();
				CharSequence charDate = mEvent.getEventDate();
				CharSequence charTime = mEvent.getEventTime();
				int intEventType = mEvent.getEventType();
				for (int appWidgetId : appWidgetIds) {
					Log.d(TAG, "Updating widget: " + appWidgetId);
					if (charTitle.length() > 10) {
						charTitle = charTitle.subSequence(0, 10) + "...";
					}
					RemoteViews mRemoteViews = new RemoteViews(
							context.getPackageName(), R.layout.widget);
					mRemoteViews
							.setTextViewText(R.id.txtWidgetTitle, charTitle);
					mRemoteViews.setTextViewText(R.id.txtWidgetDateTime,
							charDate + " " + charTime);
					// 根据类型使用不同的icon_pin
					switch (intEventType) {
					case 1:
						mRemoteViews.setImageViewResource(
								R.id.imgWidgetLocation, R.drawable.ic_pin_play);
						break;
					case 2:
						mRemoteViews.setImageViewResource(
								R.id.imgWidgetLocation,
								R.drawable.ic_pin_speech);
						break;
					case 3:
						mRemoteViews.setImageViewResource(
								R.id.imgWidgetLocation,
								R.drawable.ic_pin_course);
						break;
					default:
						break;
					}
					Log.e(TAG, charTitle + " , " + charDate + " , " + charTime);
					setWidgetDetail(context, mRemoteViews, mEvent);
					setWidgetLocation(context, mRemoteViews, mEvent);
					appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
				}
			} else {
				for (int appWidgetId : appWidgetIds) {
					RemoteViews views = new RemoteViews(
							context.getPackageName(), R.layout.widget);
					views.setTextViewText(R.id.txtWidgetTitle, "请先设置我所关注的活动");
					views.setTextViewText(R.id.txtWidgetDateTime, "");
					views.setImageViewResource(R.id.imgWidgetLocation,
							R.drawable.ic_pin_info);
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString() + " " + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
				LbsApplication.getActivityData().closeDatabase();
			}
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

	public LbsWidget() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 通过设置PendingIntent，在主应用地图上定位widget信息。
	 * 
	 * @param context
	 * @param remoteViews
	 * @param event
	 */
	private void setWidgetDetail(Context context, RemoteViews remoteViews,
			Event event) {
		Intent intent = new Intent(context, EventListView.class);
		intent.setAction("Form_Widget");
		Bundle bundle = new Bundle();
		bundle.putParcelable("activity", event);
		intent.putExtras(bundle);
		remoteViews.setOnClickPendingIntent(R.id.imgWidgetDetail, PendingIntent
				.getActivity(context, LbsApplication.GET_EVENT, intent,
						PendingIntent.FLAG_CANCEL_CURRENT));
	}

	/**
	 * 通过设置PendingIntent，在主应用中显示详细信息。
	 * 
	 * @param context
	 * @param remoteViews
	 * @param event
	 */
	private void setWidgetLocation(Context context, RemoteViews remoteViews,
			Event event) {
		Intent intent = new Intent(context, HomeActivity.class);
		intent.setAction("Form_Widget");
		Bundle bundle = new Bundle();
		bundle.putParcelable("activity", event);
		intent.putExtras(bundle);
		remoteViews.setOnClickPendingIntent(R.id.imgWidgetLocation,
				PendingIntent.getActivity(context, LbsApplication.GET_EVENT,
						intent, PendingIntent.FLAG_UPDATE_CURRENT));
	}

}

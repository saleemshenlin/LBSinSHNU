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

/**
 * ��LbsWidget<br>
 * ����ʵ������С����
 */
public class LbsWidget extends AppWidgetProvider {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾLbsWidget
	 */
	private static final String TAG = "LbsWidget";
	/**
	 * ʵ��һ��Event
	 */
	private Event mEvent;

	/**
	 * ����Widget����<br>
	 * 1)��ѯ�Ƿ���cursor���<br>
	 * 2)����н��,����ÿһ��Widget<br>
	 * 3)��������<br>
	 * 4)����setWidgetDetail()���ô�Widget����Event��ϸ�ķ���<br>
	 * 5)����setWidgetLocation()���ô�Widget����Event��λ�ķ���<br>
	 * 6)���û�н��,����ÿһ��Widget,��������,�������������ַ���
	 */
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
					views.setTextViewText(R.id.txtWidgetTitle, "��������������ע�Ļ");
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
				LbsApplication.getEventData().closeDatabase();
			}
		}
	}

	/**
	 * ���ڽ��ո���Widget�Ĺ㲥,������onUpdate()����Widget
	 */
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
	 * ����ͨ������PendingIntent������Ӧ�õ�ͼ�϶�λwidget��Ϣ��
	 * 
	 * @param context
	 *            ������
	 * @param remoteViews
	 *            Widget��View
	 * @param event
	 *            Widget��ʾ��Event
	 */
	private void setWidgetDetail(Context context, RemoteViews remoteViews,
			Event event) {
		Intent intent = new Intent(context, EventListActivity.class);
		intent.setAction("Form_Widget");
		Bundle bundle = new Bundle();
		bundle.putParcelable("activity", event);
		intent.putExtras(bundle);
		remoteViews.setOnClickPendingIntent(R.id.imgWidgetDetail, PendingIntent
				.getActivity(context, LbsApplication.GET_EVENT, intent,
						PendingIntent.FLAG_CANCEL_CURRENT));
	}

	/**
	 * ����ͨ������PendingIntent������Ӧ������ʾ��ϸ��Ϣ��
	 * 
	 * @param context
	 *            ������
	 * @param remoteViews
	 *            Widget��View
	 * @param event
	 *            Widget��ʾ��Event
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

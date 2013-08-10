package com.shnu.lbsshnu;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * ��EventProvider<br>
 * ��EventData�������ṩ�����������������ݿ��ý��
 */
public class EventProvider extends ContentProvider {
	/**
	 * ����һ��"���",��manifest����ƥ��
	 */
	public static final String AUTHORITY = "com.shnu.lbsshnu.eventprovider";
	/**
	 * ����һ��Uri
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/activity");
	/**
	 * ����һ��������ѯ��MIME����
	 */
	public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.shnu.lbsinshnu.status";
	/**
	 * ����һ�������ѯ��MIME����
	 */
	public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.shnu.lbsinshnu.mstatus";
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾEventListActivity
	 */
	private static final String TAG = "EventProvider";

	@Override
	public boolean onCreate() {
		return true;
	}

	/**
	 * (non-Javadoc)ɾ������(��ʵ��)
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 *      java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		long id = this.getId(uri);
		SQLiteDatabase db = LbsApplication.getEventData().getDbHelper()
				.getWritableDatabase();
		try {
			if (id < 0) {
				return db.delete(EventData.TABLE, selection, selectionArgs);
			} else {
				return db.delete(EventData.TABLE, EventData.C_ID + "=" + id,
						null);
			}
		} finally {
			db.close();
		}
	}

	/**
	 * (non-Javadoc)��������(��ʵ��)
	 * 
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 *      android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = LbsApplication.getEventData().getDbHelper()
				.getWritableDatabase();
		try {
			long id = db.insertOrThrow(EventData.TABLE, null, values); //
			if (id == -1) {
				throw new RuntimeException(
						String.format(
								"%s: Failed to insert [%s] to [%s] for unknown reasons.",
								TAG, values, uri)); //
			} else {
				return ContentUris.withAppendedId(uri, id); //
			}
		} finally {
			db.close(); //
		}
	}

	/**
	 * (non-Javadoc)��ѯ����<br>
	 * ����Uri�Ƿ���id���е�����ѯ���߶����ѯ
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 *      java.lang.String[], java.lang.String, java.lang.String[],
	 *      java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		long id = this.getId(uri); //
		SQLiteDatabase db = LbsApplication.getEventData().getDbHelper()
				.getReadableDatabase(); //
		try {
			if (id < 0) {
				return db.query(EventData.TABLE, projection, selection,
						selectionArgs, null, null, sortOrder); //
			} else {
				Cursor cursor = db.query(EventData.TABLE, projection,
						EventData.C_ID + " = " + id, null, null, null, null);
				return cursor; //
			}
		} catch (Exception e) {
			Log.e("TAG", e.toString());
			return null;
		}
	}

	/**
	 * (non-Javadoc)��������(��ʵ��)
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 *      android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		long id = this.getId(uri);
		SQLiteDatabase db = LbsApplication.getEventData().getDbHelper()
				.getWritableDatabase();
		try {
			if (id < 0) {
				return db.update(EventData.TABLE, values, selection,
						selectionArgs);
			} else {
				return db.update(EventData.TABLE, values, EventData.C_ID + "="
						+ id, null);
			}
		} finally {
			db.close();
		}
	}

	/**
	 * ����ȷ����ѯ����,�����Ƿ����id, ������Ӧ��MIME����
	 */
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return this.getId(uri) < 0 ? MULTIPLE_RECORDS_MIME_TYPE
				: SINGLE_RECORD_MIME_TYPE;
	}

	/**
	 * �ж��ǲ�ѯUri���Ƿ���id
	 * 
	 * @param uri
	 *            ��ѯUri
	 * @return boolean �Ƿ����
	 */
	private long getId(Uri uri) {
		String lastPathSegment = uri.getLastPathSegment(); //
		if (lastPathSegment != null) {
			try {
				return Long.parseLong(lastPathSegment); //
			} catch (NumberFormatException e) { //
				// at least we tried
			}
		}
		return -1; //
	}
}

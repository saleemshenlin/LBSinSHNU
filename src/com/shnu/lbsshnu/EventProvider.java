package com.shnu.lbsshnu;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * 类EventProvider<br>
 * 是EventData的内容提供器，用于外界访问数据库的媒介
 */
public class EventProvider extends ContentProvider {
	/**
	 * 定义一个"典据",与manifest中相匹配
	 */
	public static final String AUTHORITY = "com.shnu.lbsshnu.eventprovider";
	/**
	 * 定义一个Uri
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/activity");
	/**
	 * 定义一个单个查询的MIME类型
	 */
	public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.shnu.lbsinshnu.status";
	/**
	 * 定义一个多个查询的MIME类型
	 */
	public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.shnu.lbsinshnu.mstatus";
	/**
	 * 定义一个标签,在LogCat内表示EventListActivity
	 */
	private static final String TAG = "EventProvider";

	@Override
	public boolean onCreate() {
		return true;
	}

	/**
	 * (non-Javadoc)删除数据(无实用)
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
	 * (non-Javadoc)插入数据(无实用)
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
	 * (non-Javadoc)查询数据<br>
	 * 根据Uri是否有id进行单个查询或者多个查询
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
	 * (non-Javadoc)更新数据(无实用)
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
	 * 用于确定查询类型,根据是否存在id, 返回相应的MIME类型
	 */
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return this.getId(uri) < 0 ? MULTIPLE_RECORDS_MIME_TYPE
				: SINGLE_RECORD_MIME_TYPE;
	}

	/**
	 * 判断是查询Uri中是否有id
	 * 
	 * @param uri
	 *            查询Uri
	 * @return boolean 是否存在
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

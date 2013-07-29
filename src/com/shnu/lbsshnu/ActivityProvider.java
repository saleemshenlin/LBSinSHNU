package com.shnu.lbsshnu;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ActivityProvider extends ContentProvider {
	public static final String AUTHORITY = "com.shnu.lbsshnu.activityprovider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/activity");
	public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.shnu.lbsinshnu.status";
	public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.shnu.lbsinshnu.mstatus";
	private static final String TAG = "ActivityProvider";

	@Override
	public boolean onCreate() {
		return true;
	}

	/*
	 * (non-Javadoc)删除数据
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		long id = this.getId(uri);
		SQLiteDatabase db = LbsApplication.getActivityData().getDbHelper()
				.getWritableDatabase();
		try {
			if (id < 0) {
				return db.delete(ActivityData.TABLE, selection, selectionArgs);
			} else {
				return db.delete(ActivityData.TABLE, ActivityData.C_ID + "="
						+ id, null);
			}
		} finally {
			db.close();
		}
	}

	/*
	 * (non-Javadoc)插入数据
	 * 
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 * android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = LbsApplication.getActivityData().getDbHelper()
				.getWritableDatabase();
		try {
			long id = db.insertOrThrow(ActivityData.TABLE, null, values); //
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

	/*
	 * (non-Javadoc)查询数据
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 * java.lang.String[], java.lang.String, java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		long id = this.getId(uri); //
		SQLiteDatabase db = LbsApplication.getActivityData().getDbHelper()
				.getReadableDatabase(); //
		try {
			if (id < 0) {
				return db.query(ActivityData.TABLE, projection, selection,
						selectionArgs, null, null, sortOrder); //
			} else {
				Cursor cursor = db.query(ActivityData.TABLE, projection,
						ActivityData.C_ID + " = " + id, null, null, null, null);
				return cursor; //
			}
		} catch (Exception e) {
			Log.e("TAG", e.toString());
			return null;
		}
	}

	/*
	 * (non-Javadoc)更新数据
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 * android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		long id = this.getId(uri);
		SQLiteDatabase db = LbsApplication.getActivityData().getDbHelper()
				.getWritableDatabase();
		try {
			if (id < 0) {
				return db.update(ActivityData.TABLE, values, selection,
						selectionArgs);
			} else {
				return db.update(ActivityData.TABLE, values, ActivityData.C_ID
						+ "=" + id, null);
			}
		} finally {
			db.close();
		}
	}

	/*
	 * 获取查询Uri中是的有id
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

	/*
	 * (non-Javadoc)根据id判读查询类型
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return this.getId(uri) < 0 ? MULTIPLE_RECORDS_MIME_TYPE
				: SINGLE_RECORD_MIME_TYPE;
	}
}

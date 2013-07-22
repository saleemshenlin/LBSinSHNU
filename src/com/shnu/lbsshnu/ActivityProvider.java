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
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		long id = this.getId(uri);
		SQLiteDatabase db = LBSApplication.getActivityData().getDbHelper()
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

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return this.getId(uri) < 0 ? MULTIPLE_RECORDS_MIME_TYPE
				: SINGLE_RECORD_MIME_TYPE;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = LBSApplication.getActivityData().getDbHelper()
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

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		long id = this.getId(uri); //
		SQLiteDatabase db = LBSApplication.getActivityData().getDbHelper()
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

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		long id = this.getId(uri);
		SQLiteDatabase db = LBSApplication.getActivityData().getDbHelper()
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

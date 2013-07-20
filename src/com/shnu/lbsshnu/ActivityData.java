package com.shnu.lbsshnu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ActivityData {
	static final String TAG = "DbHelper";
	static final int VERSION = 1;
	static final String DATABASE = "data.db";
	static final String TABLE = "activity";

	public static final String C_ID = "_id";
	public static final String C_NAME = "_name";
	public static final String C_DATE = "_date";
	public static final String C_TIME = "_time";
	public static final String C_LOCATION = "_location";
	public static final String C_BUILDING = "_building";
	public static final String C_TYPE = "_type";
	public static final String C_SPEAKER = "_speaker";
	public static final String C_SPEAKERTITLE = "_speakertitle";
	public static final String C_ISLIKE = "_islike";
	public static final String C_PRICE = "_price";
	public static final String C_DESCRIPTION = "_description";

	// private static String ORDERBY = " ORDER BY " + C_TIME + " ASC";
	// private static String GROUPBY = " GROUP BY " + C_DATE;

	class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
			// TODO Auto-generated constructor stub
		}

		/*
		 * 创建数据库
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database
		 * .sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.i(TAG, "Creating database: " + DATABASE);
			db.execSQL("create TABLE " + TABLE + "(" + C_ID
					+ " INTEGER PRIMARY KEY," + C_NAME + " VARCHAR(50),"
					+ C_DATE + " DATE," + C_TIME + " TIME," + C_LOCATION
					+ " VARCHAR(50)," + C_BUILDING + " TINYINT," + C_TYPE
					+ " TINYINT," + C_SPEAKER + " VARCHAR(50),"
					+ C_SPEAKERTITLE + " VARCHAR(128)," + C_ISLIKE
					+ " TINYINT," + C_PRICE + " TINYINT," + C_DESCRIPTION
					+ " TEXT)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table " + TABLE);
			this.onCreate(db);
		}
	}

	private final DbHelper dbHelper;

	public ActivityData(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "initialized data");
	}

	public void closeDatabase() {
		this.dbHelper.close();
	}

	public void insertOrIgnore(ContentValues values) {
		Log.d(TAG, "insert " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try {
			db.insertWithOnConflict(TABLE, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
		} finally {
			db.close();
		}
	}

	/*
	 * Table 是否存在
	 */
	public boolean tabIsExist() {
		boolean result = false;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ TABLE.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.close();
		}
		return result;
	}

	/*
	 * Table 是否有数据
	 */
	public boolean tableIsNull() {
		boolean result = false;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			String sql = "select * from " + TABLE;
			cursor = db.rawQuery(sql, null);
			int num = cursor.getCount();
			if (num < 1) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.close();
		}
		return result;
	}

	public DbHelper getDbHelper() {
		return this.dbHelper;
	}

}

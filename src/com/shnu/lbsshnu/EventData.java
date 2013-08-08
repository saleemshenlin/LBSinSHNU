package com.shnu.lbsshnu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Event��DbHelper
 */
@SuppressLint("SimpleDateFormat")
public class EventData {
	private static final String TAG = "DbHelper";
	private static long DAY = 24 * 3600 * 1000;
	static final int VERSION = 1;
	static final String DATABASE = "data.db";
	static final String TABLE = "activity";

	static final String C_ID = "_id";
	static final String C_NAME = "_name";
	static final String C_DATE = "_date";
	static final String C_TIME = "_time";
	static final String C_LOCATION = "_location";
	static final String C_BUILDING = "_building";
	static final String C_TYPE = "_type";
	static final String C_SPEAKER = "_speaker";
	static final String C_SPEAKERTITLE = "_speakertitle";
	static final String C_ISLIKE = "_islike";
	static final String C_PRICE = "_price";
	static final String C_DESCRIPTION = "_description";

	class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
			// TODO Auto-generated constructor stub
		}

		/**
		 * �������ݿ�
		 * 
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database
		 *      .sqlite.SQLiteDatabase)
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

	public EventData(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "initialized data");
	}

	/**
	 * �ر����ݿ�
	 */
	public void closeDatabase() {
		this.dbHelper.close();
	}

	/**
	 * ��������
	 * 
	 * @param values
	 *            ����ƥ���
	 */
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

	/**
	 * �ж��Ƿ���ڱ�
	 * 
	 * @return boolean
	 */
	public boolean tabIsExist() {
		boolean isExist = false;
		SQLiteDatabase db = null;
		Cursor mCursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			String strSQL = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ TABLE.trim() + "' ";
			mCursor = db.rawQuery(strSQL, null);
			if (mCursor.moveToNext()) {
				int count = mCursor.getInt(0);
				if (count > 0) {
					isExist = true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
			db.close();
		}
		return isExist;
	}

	/**
	 * �жϱ��Ƿ�Ϊ��
	 * 
	 * @return boolean
	 */
	public boolean tableIsNull() {
		boolean isNull = false;
		SQLiteDatabase db = null;
		Cursor mCursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			String strSQL = "select * from " + TABLE;
			mCursor = db.rawQuery(strSQL, null);
			int num = mCursor.getCount();
			if (num < 1) {
				return true;
			}
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
			db.close();
		}
		return isNull;
	}

	/**
	 * �������ݿγ������ҵ��������ݵ�ʱ�������һ������ ��ȡ���� ���ں��� ������Աȣ�������7����������пγ��������ڼ�7��
	 * 
	 * @throws ParseException
	 */
	public void updateCourseDate() throws ParseException {
		boolean isUpdate = false;
		SQLiteDatabase db = null;
		Cursor mCursor = null;
		Calendar mCalendar = Calendar.getInstance();
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date mDateNow = mCalendar.getTime();
		try {
			db = this.dbHelper.getReadableDatabase();
			String strSQL = "select * from " + TABLE + " where " + C_TYPE
					+ " = 3 ORDER BY " + C_DATE + " ASC";
			mCursor = db.rawQuery(strSQL, null);
			if (mCursor.moveToFirst()) {
				Date mDateCursor = mSimpleDateFormat.parse(mCursor
						.getString(mCursor.getColumnIndex(C_DATE)));
				int intLackDays = (int) Math
						.abs(((mDateNow.getTime() - mDateCursor.getTime()) / DAY));
				isUpdate = intLackDays >= 7;
				if (isUpdate) {
					for (int i = 0; i < mCursor.getCount(); i++) {
						ContentValues mContentValues = new ContentValues();
						Date mDateOld = mSimpleDateFormat.parse(mCursor
								.getString(mCursor.getColumnIndex(C_DATE)));
						int id = mCursor.getInt(mCursor.getColumnIndex(C_ID));
						String strDataNew = mSimpleDateFormat.format(new Date(
								mDateOld.getTime() + intLackDays * DAY));
						mContentValues.put(C_DATE, strDataNew);
						int intRow = db.updateWithOnConflict(TABLE,
								mContentValues, C_ID + " = '" + id + "'", null,
								SQLiteDatabase.CONFLICT_IGNORE);
						Log.d("Update " + id, intRow + "row is changed");
						mCursor.moveToNext();
					}
				}
			}
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
			db.close();
		}
	}

	/**
	 * ��ȡ���ݿ�
	 * 
	 * @return DbHelper
	 */
	public DbHelper getDbHelper() {
		return this.dbHelper;
	}

}

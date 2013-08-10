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
 * ��EventData<br>
 * ������ sqlite3���ݿ⽻��
 */
@SuppressLint("SimpleDateFormat")
public class EventData {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾEventData
	 */
	private static final String TAG = "EventData";
	/**
	 * ����һ���㶨ʱ��,Ϊһ��
	 */
	private static long DAY = 24 * 3600 * 1000;
	/**
	 * ����һ�����ݿ�汾
	 */
	static final int VERSION = 1;
	/**
	 * ���ڱ�ʾһ�����ݿ���
	 */
	static final String DATABASE = "data.db";
	/**
	 * ���ڱ�ʾ����
	 */
	static final String TABLE = "event";
	/**
	 * ���ڱ�ʾ�ֶ�Event��id
	 */
	static final String C_ID = "_id";
	/**
	 * ���ڱ�ʾ�ֶ�Event������
	 */
	static final String C_NAME = "_name";
	/**
	 * ���ڱ�ʾ�ֶ�Event�ķ�������
	 */
	static final String C_DATE = "_date";
	/**
	 * ���ڱ�ʾ�ֶ�Event�ķ���ʱ��
	 */
	static final String C_TIME = "_time";
	/**
	 * ���ڱ�ʾ�ֶ�Event��λ������
	 */
	static final String C_LOCATION = "_location";
	/**
	 * ���ڱ�ʾ�ֶ�Event��λ��id�����ͼ����idƥ��
	 */
	static final String C_BUILDING = "_building";
	/**
	 * ���ڱ�ʾ�ֶ�Event�����ͣ�1Ϊѧ��������2Ϊ��Ӱ�ݳ���3Ϊ��Ʒ�γ�
	 */
	static final String C_TYPE = "_type";
	/**
	 * ���ڱ�ʾ�ֶ�Event����Ҫ������
	 */
	static final String C_SPEAKER = "_speaker";
	/**
	 * ���ڱ�ʾ�ֶ�Event�ı�����ͷ��
	 */
	static final String C_SPEAKERTITLE = "_speakertitle";
	/**
	 * ���ڱ�ʾ�ֶ�Event�Ƿ񱻹�ע
	 */
	static final String C_ISLIKE = "_islike";
	/**
	 * ���ڱ�ʾ�ֶ�Event��Ʊ��(��ʵ��)
	 */
	static final String C_PRICE = "_price";
	/**
	 * ���ڱ�ʾ�ֶ�Event�ļ��
	 */
	static final String C_DESCRIPTION = "_description";

	/**
	 * ��DbHelper<br>
	 * ���sqlite3���ݿ����
	 */
	class DbHelper extends SQLiteOpenHelper {
		/**
		 * ����ͨ���������ƺͰ汾����һ��DbHelp��
		 * 
		 * @param context
		 *            ������
		 */
		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}

		/**
		 * ���ڴ���һ�������ݿ��һ���±�
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

		/**
		 * �����ڸ������ݿ�ʱ,ֱ��ɾ�������ݿ�
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists " + TABLE);
			this.onCreate(db);
		}
	}

	private final DbHelper dbHelper;

	/**
	 * �ڳ�ʼ��EventDataʱ,ͬʱ��ʼ��һ��DbHelper
	 * 
	 * @param context
	 *            ������
	 */
	public EventData(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "initialized data");
	}

	/**
	 * ���ڹر����ݿ�
	 */
	public void closeDatabase() {
		this.dbHelper.close();
	}

	/**
	 * ���ڴ�Ӧ��ʱ��������<br>
	 * ����tabIsExist()��tableIsNull()�Ľ�������Ƿ�ִ��<br>
	 * ���巽������:<br>
	 * ʹ��insertWithOnConflict(),�����ݿ��Լ������ͻ
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
	 * �����ж��Ƿ���ڱ�<br>
	 * ���巽������:<br>
	 * 1)ִ��sql���select count(*) as c from sqlite_master where type ='table' and
	 * name='event' name ='activity'<br>
	 * 2)���ݽ��Cursor���ж�
	 * 
	 * @return boolean �Ƿ����
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
	 * �����жϱ��Ƿ�Ϊ�� <br>
	 * ���巽������:<br>
	 * 1)ִ��sql���select * from event<br>
	 * 2)���ݽ��Cursor���ж�
	 * 
	 * @return boolean �Ƿ����
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
	 * ���ڴ�Ӧ��ʱ���¿γ�����<br>
	 * ���巽������:<br>
	 * 1)�ҵ��������ݵ�ʱ�������һ������<br>
	 * 2)��ȡ�������ں��������ڶԱȣ�������7����������пγ���������<br>
	 * 3)��������:ԭ�����ں��������ڵ��������,�ټ�7��<br>
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
	 * ���ڻ�ȡ���ݿ�DbHelper��
	 * 
	 * @return DbHelper DbHelper��
	 */
	public DbHelper getDbHelper() {
		return this.dbHelper;
	}

}

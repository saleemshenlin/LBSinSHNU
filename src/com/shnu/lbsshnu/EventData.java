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
 * 类EventData<br>
 * 用于与 sqlite3数据库交互
 */
@SuppressLint("SimpleDateFormat")
public class EventData {
	/**
	 * 定义一个标签,在LogCat内表示EventData
	 */
	private static final String TAG = "EventData";
	/**
	 * 定义一个恒定时间,为一天
	 */
	private static long DAY = 24 * 3600 * 1000;
	/**
	 * 定义一个数据库版本
	 */
	static final int VERSION = 1;
	/**
	 * 用于表示一个数据库名
	 */
	static final String DATABASE = "data.db";
	/**
	 * 用于表示表名
	 */
	static final String TABLE = "event";
	/**
	 * 用于表示字段Event的id
	 */
	static final String C_ID = "_id";
	/**
	 * 用于表示字段Event的名称
	 */
	static final String C_NAME = "_name";
	/**
	 * 用于表示字段Event的发生日期
	 */
	static final String C_DATE = "_date";
	/**
	 * 用于表示字段Event的发生时间
	 */
	static final String C_TIME = "_time";
	/**
	 * 用于表示字段Event的位置名称
	 */
	static final String C_LOCATION = "_location";
	/**
	 * 用于表示字段Event的位置id，与地图属性id匹配
	 */
	static final String C_BUILDING = "_building";
	/**
	 * 用于表示字段Event的类型，1为学术讲座，2为电影演出，3为精品课程
	 */
	static final String C_TYPE = "_type";
	/**
	 * 用于表示字段Event的主要表演者
	 */
	static final String C_SPEAKER = "_speaker";
	/**
	 * 用于表示字段Event的表演者头衔
	 */
	static final String C_SPEAKERTITLE = "_speakertitle";
	/**
	 * 用于表示字段Event是否被关注
	 */
	static final String C_ISLIKE = "_islike";
	/**
	 * 用于表示字段Event的票价(无实用)
	 */
	static final String C_PRICE = "_price";
	/**
	 * 用于表示字段Event的简介
	 */
	static final String C_DESCRIPTION = "_description";

	/**
	 * 类DbHelper<br>
	 * 针对sqlite3数据库操作
	 */
	class DbHelper extends SQLiteOpenHelper {
		/**
		 * 用于通过数据名称和版本构造一个DbHelp类
		 * 
		 * @param context
		 *            上下文
		 */
		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}

		/**
		 * 用于创建一个新数据库和一个新表
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
		 * 用于在更新数据库时,直接删除旧数据库
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
	 * 在初始化EventData时,同时初始化一个DbHelper
	 * 
	 * @param context
	 *            上下文
	 */
	public EventData(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "initialized data");
	}

	/**
	 * 用于关闭数据库
	 */
	public void closeDatabase() {
		this.dbHelper.close();
	}

	/**
	 * 用于打开应用时插入数据<br>
	 * 根据tabIsExist()和tableIsNull()的结果决定是否执行<br>
	 * 具体方法如下:<br>
	 * 使用insertWithOnConflict(),让数据库自己解决冲突
	 * 
	 * @param values
	 *            数据匹配对
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
	 * 用于判断是否存在表<br>
	 * 具体方法如下:<br>
	 * 1)执行sql语句select count(*) as c from sqlite_master where type ='table' and
	 * name='event' name ='activity'<br>
	 * 2)根据结果Cursor来判断
	 * 
	 * @return boolean 是否存在
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
	 * 用于判断表是否为空 <br>
	 * 具体方法如下:<br>
	 * 1)执行sql语句select * from event<br>
	 * 2)根据结果Cursor来判断
	 * 
	 * @return boolean 是否存在
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
	 * 用于打开应用时更新课程数据<br>
	 * 具体方法如下:<br>
	 * 1)找到所有数据的时间最早的一条数据<br>
	 * 2)获取它的日期和现在日期对比，若大于7天则更新所有课程数据日期<br>
	 * 3)更新天数:原有日期和现在日期的相差天数,再加7天<br>
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
	 * 用于获取数据库DbHelper类
	 * 
	 * @return DbHelper DbHelper类
	 */
	public DbHelper getDbHelper() {
		return this.dbHelper;
	}

}

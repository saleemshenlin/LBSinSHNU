package com.shnu.lbsshnu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.util.Log;

/**
 * 类FileIO<br>
 * 用于数据与文件夹间的交互
 */
public class FileIO {
	/**
	 * 定义一个标签,在LogCat内表示FileIO
	 */
	private static final String TAG = "FileIO";

	/**
	 * 从Data.xml获取数据存入sqlite3<br>
	 * 具体方法如下:<br>
	 * 1)判断时候需要导入数据 <br>
	 * 2)使用pull方式解析XML<br>
	 * 3)遍历每一条数据,然后调用EventData.insertOrIgnore()存入数据库
	 */
	public void getDateFromXML() {
		if (!LbsApplication.getEventData().tableIsNull()) {
			return;
		}

		XmlResourceParser mXmlResourceParser = LbsApplication.getContext()
				.getResources().getXml(R.xml.data);
		int intEventType;
		StringBuffer mStringBuffer = new StringBuffer();
		ContentValues mContentValues = new ContentValues();
		String strRowName = "";
		try {
			intEventType = mXmlResourceParser.getEventType();
			while (intEventType != XmlResourceParser.END_DOCUMENT) {
				if (intEventType == XmlResourceParser.START_TAG) {
					String tagName = mXmlResourceParser.getName().toString()
							.trim();
					if (!tagName.equals("root")) {
						mStringBuffer.append(mXmlResourceParser.getName());
						if (tagName.equals("row")) {
							mStringBuffer.append("(");
						} else {
							mStringBuffer.append(":");
							strRowName = tagName;
						}
					}
				} else if (intEventType == XmlResourceParser.END_TAG) {
					String tagName = mXmlResourceParser.getName().toString()
							.trim();
					if (tagName.equals("row")) {
						mStringBuffer.append(")");
						Log.d(TAG, mStringBuffer.toString());
						LbsApplication.getEventData().insertOrIgnore(
								mContentValues);
						mStringBuffer.delete(0, mStringBuffer.length() - 1);
					} else if (tagName.equals("root")) {
						Log.d(TAG, "end");
					} else {
						mStringBuffer.append(", ");
					}
				} else if (intEventType == XmlResourceParser.TEXT) {
					String tagText = mXmlResourceParser.getText().toString()
							.trim();
					mStringBuffer.append(mXmlResourceParser.getText()
							.toString().trim());
					if (strRowName.equals("id")) {
						mContentValues.put(EventData.C_ID, tagText);
					} else if (strRowName.equals("name")) {
						mContentValues.put(EventData.C_NAME, tagText);
					} else if (strRowName.equals("date")) {
						mContentValues.put(EventData.C_DATE, tagText);
					} else if (strRowName.equals("time")) {
						mContentValues.put(EventData.C_TIME, tagText);
					} else if (strRowName.equals("location")) {
						mContentValues.put(EventData.C_LOCATION, tagText);
					} else if (strRowName.equals("building")) {
						mContentValues.put(EventData.C_BUILDING, tagText);
					} else if (strRowName.equals("type")) {
						mContentValues.put(EventData.C_TYPE, tagText);
					} else if (strRowName.equals("speaker")) {
						mContentValues.put(EventData.C_SPEAKER, tagText);
					} else if (strRowName.equals("speakertitle")) {
						mContentValues.put(EventData.C_SPEAKERTITLE, tagText);
					} else if (strRowName.equals("islike")) {
						mContentValues.put(EventData.C_ISLIKE, tagText);
					} else if (strRowName.equals("price")) {
						mContentValues.put(EventData.C_PRICE, tagText);
					} else if (strRowName.equals("description")) {
						mContentValues.put(EventData.C_DESCRIPTION, tagText);
					}
				}
				intEventType = mXmlResourceParser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 用于从assets复制地图数据到SDcard<br>
	 * 具体方法如下: <br>
	 * 1)创建SDcard路径 <br>
	 * 2)使用AssetManager读取地图数据<br>
	 * 3)调用copyFile,复制文件到SDcard<br>
	 * 
	 * @param context
	 *            上下文
	 */
	public void copyMapData(Context context) {
		createPath(context, "temp");
		createPath(context, "cache");
		AssetManager mAssetManager = context.getAssets();
		String[] files = null;
		try {
			files = mAssetManager.list("");
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		for (String filename : files) {
			if (filename.equals("MapData.smwu")
					|| filename.equals("MapData.udb")
					|| filename.equals("MapData.udd")
					|| filename.equals("SuperMap iMobile Trial(635284888092595000).slm")) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = mAssetManager.open(filename);
					File outFile = null;
					if (filename.equals("SuperMap iMobile Trial(635284888092595000).slm")) {
						outFile = new File(
								context.getExternalFilesDir("license"),
								filename);
					} else {
						outFile = new File(context.getExternalFilesDir("data"),
								filename);
					}
					if (!outFile.exists()) {
						out = new FileOutputStream(outFile);
						copyFile(in, out);
						in.close();
						in = null;
						out.flush();
						out.close();
						out = null;
					}
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
		}
	}

	/**
	 * 用于文件的复制
	 * 
	 * @param in
	 *            复制源
	 * @param out
	 *            复制目的地
	 * @throws IOException
	 */
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int intRead;
		while ((intRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, intRead);
		}
	}

	/**
	 * 用于根据路径是否存在,创建存储路径
	 * 
	 * @param context
	 *            上下文
	 * @param path
	 *            创建路径
	 */
	private static void createPath(Context context, String path) {
		try {
			File mFile = new File(context.getExternalFilesDir(null), path);
			if (!mFile.exists()) {
				mFile.mkdir();
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

}

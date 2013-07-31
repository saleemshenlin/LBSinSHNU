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

public class FileIO {
	private static final String TAG = "FileIO";

	/*
	 * 从data.xml获取数据
	 */
	public void getDateFromXML() {
		if (!LbsApplication.getActivityData().tableIsNull()) {
			return;
		}

		XmlResourceParser xrp = LbsApplication.getContext().getResources()
				.getXml(R.xml.data);
		int eventType;
		StringBuffer sb = new StringBuffer();
		ContentValues values = new ContentValues();
		String rowTagName = "";
		try {
			eventType = xrp.getEventType();
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					// Log.d(TAG, "^^^^^^ Start tag " + xrp.getName());
					String tagName = xrp.getName().toString().trim();
					if (!tagName.equals("root")) {
						sb.append(xrp.getName());
						if (tagName.equals("row")) {
							sb.append("(");
						} else {
							sb.append(":");
							rowTagName = tagName;
						}
					}
				} else if (eventType == XmlResourceParser.END_TAG) {
					String tagName = xrp.getName().toString().trim();
					if (tagName.equals("row")) {
						sb.append(")");
						Log.d(TAG, sb.toString());
						LbsApplication.getActivityData().insertOrIgnore(values);//
						sb.delete(0, sb.length() - 1);
					} else if (tagName.equals("root")) {
						Log.d(TAG, "end");
					} else {
						sb.append(", ");
					}
				} else if (eventType == XmlResourceParser.TEXT) {
					String tagText = xrp.getText().toString().trim();
					sb.append(xrp.getText().toString().trim());
					if (rowTagName.equals("id")) {
						values.put(ActivityData.C_ID, tagText);
					} else if (rowTagName.equals("name")) {
						values.put(ActivityData.C_NAME, tagText);
					} else if (rowTagName.equals("date")) {
						values.put(ActivityData.C_DATE, tagText);
					} else if (rowTagName.equals("time")) {
						values.put(ActivityData.C_TIME, tagText);
					} else if (rowTagName.equals("location")) {
						values.put(ActivityData.C_LOCATION, tagText);
					} else if (rowTagName.equals("building")) {
						values.put(ActivityData.C_BUILDING, tagText);
					} else if (rowTagName.equals("type")) {
						values.put(ActivityData.C_TYPE, tagText);
					} else if (rowTagName.equals("speaker")) {
						values.put(ActivityData.C_SPEAKER, tagText);
					} else if (rowTagName.equals("speakertitle")) {
						values.put(ActivityData.C_SPEAKERTITLE, tagText);
					} else if (rowTagName.equals("islike")) {
						values.put(ActivityData.C_ISLIKE, tagText);
					} else if (rowTagName.equals("price")) {
						values.put(ActivityData.C_PRICE, tagText);
					} else if (rowTagName.equals("description")) {
						values.put(ActivityData.C_DESCRIPTION, tagText);
					}
				}
				eventType = xrp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * copy地图数据到sdcard
	 */
	public void copyMapData(Context context) {
		createPath(context, "temp");
		createPath(context, "cache");
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		for (String filename : files) {
			if (filename.equals("MapData.smwu")
					|| filename.equals("MapData.udb")
					|| filename.equals("MapData.udd")
					|| filename.equals("imobile-GISGame.slm")) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = assetManager.open(filename);
					File outFile = null;
					if (filename.equals("imobile-GISGame.slm")) {
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

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	/**
	 * 创建Environment目录
	 */
	private static void createPath(Context context, String path) {
		try {
			File file = new File(context.getExternalFilesDir(null), path);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

}

package com.shnu.lbsshnu;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class FileIO {
	private static final String TAG = "FileIO";
	private ActivityData activityData = new ActivityData(
			LBSApplication.getContext());
	/*
	 * 从data.xml获取数据
	 */
	public void getDateFromXML() {
		if (activityData.tabIsExist()) {
			return;
		}
		XmlResourceParser xrp = LBSApplication.getContext().getResources()
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
						activityData.insertOrIgnore(values);//
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
}

package com.shnu.lbsshnu;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Event实体类，通过Android使用了自定义的Parcelable，实现使用Bundle传递对象的方法。 read 和 write 的顺序要一致 *
 */
public class Event implements Parcelable {
	/**
	 * Event的id
	 */
	private int eventId;
	/**
	 * Event的发生日期
	 */
	private String eventName;
	/**
	 * Event的id
	 */
	private String eventDate;
	/**
	 * Event的发生时间
	 */
	private String eventTime;
	/**
	 * Event的位置名称
	 */
	private String eventLocation;
	/**
	 * Event的位置id，与地图属性id匹配
	 */
	private int eventBuilding;
	/**
	 * Event的类型，1为学术讲座，2为电影演出，3为精品课程
	 */
	private int eventType;
	/**
	 * Event的主要表演者
	 */
	private String eventSpeaker;
	/**
	 * Event的次要表演者
	 */
	private String eventSpeakerTitle;
	/**
	 * Event是否被关注
	 */
	private boolean eventIsLike;
	/**
	 * Event的简介
	 */
	private String eventDescription;

	public Event() {
		super();
	}

	/**
	 * 普通初始化Event
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventDate
	 * @param eventTime
	 * @param eventLocation
	 * @param eventBuilding
	 * @param eventType
	 * @param eventSpeaker
	 * @param eventSpeakerTitle
	 * @param eventIsLike
	 * @param eventDescription
	 */
	public Event(int eventId, String eventName, String eventDate,
			String eventTime, String eventLocation, int eventBuilding,
			int eventType, String eventSpeaker, String eventSpeakerTitle,
			boolean eventIsLike, String eventDescription) {
		this.eventId = eventId;
		this.eventName = eventName;
		this.eventDate = eventDate;
		this.eventTime = eventTime;
		this.eventLocation = eventLocation;
		this.eventBuilding = eventBuilding;
		this.eventType = eventType;
		this.eventSpeaker = eventSpeakerTitle;
		this.eventIsLike = eventIsLike;
		this.eventDescription = eventDescription;
	}

	/**
	 * 通过Cursor给初始化Event
	 * 
	 * @param cursor
	 *            传入一个cursor
	 */
	public Event(Cursor cursor) {
		this.setEventId(cursor.getInt(cursor.getColumnIndex(EventData.C_ID)));
		this.setEventName(cursor.getString(cursor
				.getColumnIndex(EventData.C_NAME)));
		this.setEventDate(cursor.getString(cursor
				.getColumnIndex(EventData.C_DATE)));
		this.setEventTime(cursor.getString(cursor
				.getColumnIndex(EventData.C_TIME)));
		this.setEventLocation(cursor.getString(cursor
				.getColumnIndex(EventData.C_LOCATION)));
		this.setEventBuilding(cursor.getInt(cursor
				.getColumnIndex(EventData.C_BUILDING)));
		this.setEventType(cursor.getInt(cursor.getColumnIndex(EventData.C_TYPE)));
		this.setEventSpeaker(cursor.getString(cursor
				.getColumnIndex(EventData.C_SPEAKER)));
		this.setEventSpeakerTitle(cursor.getString(cursor
				.getColumnIndex(EventData.C_SPEAKERTITLE)));
		this.setEventDescription(cursor.getString(cursor
				.getColumnIndex(EventData.C_DESCRIPTION)));
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public String getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	public int getEventBuilding() {
		return eventBuilding;
	}

	public void setEventBuilding(int eventBuilding) {
		this.eventBuilding = eventBuilding;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public String getEventSpeaker() {
		return eventSpeaker;
	}

	public void setEventSpeaker(String eventSpeaker) {
		this.eventSpeaker = eventSpeaker;
	}

	public String getEventSpeakerTitle() {
		return eventSpeakerTitle;
	}

	public void setEventSpeakerTitle(String eventSpeakerTitle) {
		this.eventSpeakerTitle = eventSpeakerTitle;
	}

	public boolean isEventIsLike() {
		return eventIsLike;
	}

	public void setEventIsLike(boolean eventIsLike) {
		this.eventIsLike = eventIsLike;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	@Override
	public String toString() {
		return "event:[" + eventId + " ; " + eventName + " ; " + eventDate
				+ " ; " + eventTime + " ; " + eventBuilding + " ; "
				+ eventLocation + " ; " + eventSpeaker + " ; ";
	}

	public static final Parcelable.Creator<Event> CREATOR = new Creator<Event>() {

		@Override
		public Event createFromParcel(Parcel source) {
			Event event = new Event();
			event.eventId = source.readInt();
			event.eventName = source.readString();
			event.eventDate = source.readString();
			event.eventTime = source.readString();
			event.eventLocation = source.readString();
			event.eventBuilding = source.readInt();
			event.eventType = source.readInt();
			switch (source.readInt()) {
			case 1:
				event.eventIsLike = true;
			default:
				event.eventIsLike = false;
			}
			event.eventSpeaker = source.readString();
			event.eventSpeakerTitle = source.readString();
			event.eventDescription = source.readString();
			return event;
		}

		@Override
		public Event[] newArray(int size) {
			return new Event[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(eventId);
		parcel.writeString(eventName);
		parcel.writeString(eventDate);
		parcel.writeString(eventTime);
		parcel.writeString(eventLocation);
		parcel.writeInt(eventBuilding);
		parcel.writeInt(eventType);
		if (eventIsLike) {
			parcel.writeInt(1);
		} else {
			parcel.writeInt(0);
		}
		parcel.writeString(eventSpeaker);
		parcel.writeString(eventSpeakerTitle);
		parcel.writeString(eventDescription);
	}
}

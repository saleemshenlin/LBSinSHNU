package com.shnu.lbsshnu;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 类Event<br>
 * 是Event实体类，使用了自定义的Parcelable，实现使用Bundle传递对象的方法<br>
 */
public class Event implements Parcelable {
	/**
	 * Event的id
	 */
	private int eventId;
	/**
	 * Event的名称
	 */
	private String eventName;
	/**
	 * Event的发生日期
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
	 * Event的表演者头衔
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
	 * 通过Cursor给初始化Event
	 * 
	 * @param cursor
	 *            查询结果
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

	/**
	 * 用于获取Event的id
	 * 
	 * @return int Event的id
	 */
	public int getEventId() {
		return eventId;
	}

	/**
	 * 用于给Event的id赋值
	 * 
	 * @param eventId
	 *            Event的id
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	/**
	 * 用于获取Event的名称
	 * 
	 * @return String Event的名称
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * 用于给Event的名称赋值
	 * 
	 * @param eventName
	 *            Event的名称
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * 用于获取Event的发生日期
	 * 
	 * @return String Event的发生日期
	 */
	public String getEventDate() {
		return eventDate;
	}

	/**
	 * 用于给Event的发生日期赋值
	 * 
	 * @param eventDate
	 *            Event的发生日期
	 */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	/**
	 * 用于获取Event的发生时间
	 * 
	 * @return String Event的发生时间
	 */
	public String getEventTime() {
		return eventTime;
	}

	/**
	 * 用于给Event的发生时间赋值
	 * 
	 * @param eventTime
	 *            Event的发生时间
	 */
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	/**
	 * 用于获取Event的位置名称
	 * 
	 * @return String Event的位置名称
	 */
	public String getEventLocation() {
		return eventLocation;
	}

	/**
	 * 用于给Event的位置名称赋值
	 * 
	 * @param eventLocation
	 *            Event的位置名称
	 */
	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	/**
	 * 用于获取Event的位置id
	 * 
	 * @return int Event的位置id，与地图属性id匹配
	 */
	public int getEventBuilding() {
		return eventBuilding;
	}

	/**
	 * 用于给 Event的位置id赋值
	 * 
	 * @param eventBuilding
	 *            Event的位置id，与地图属性id匹配
	 */
	public void setEventBuilding(int eventBuilding) {
		this.eventBuilding = eventBuilding;
	}

	/**
	 * 用于获取Event的类型
	 * 
	 * @return int Event的类型，1为学术讲座，2为电影演出，3为精品课程
	 */
	public int getEventType() {
		return eventType;
	}

	/**
	 * 用于给Event的类型赋值
	 * 
	 * @param eventType
	 *            Event的类型，1为学术讲座，2为电影演出，3为精品课程
	 */
	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	/**
	 * 用于获取Event的主要表演者
	 * 
	 * @return String Event的主要表演者
	 */
	public String getEventSpeaker() {
		return eventSpeaker;
	}

	/**
	 * 用于给Event的主要表演者赋值
	 * 
	 * @param eventSpeaker
	 *            Event的主要表演者
	 */
	public void setEventSpeaker(String eventSpeaker) {
		this.eventSpeaker = eventSpeaker;
	}

	/**
	 * 用于获取Event的表演者头衔
	 * 
	 * @return String Event的表演者头衔
	 */
	public String getEventSpeakerTitle() {
		return eventSpeakerTitle;
	}

	/**
	 * 用于给Event的表演者头衔赋值
	 * 
	 * @param eventSpeakerTitle
	 *            Event的表演者头衔
	 */
	public void setEventSpeakerTitle(String eventSpeakerTitle) {
		this.eventSpeakerTitle = eventSpeakerTitle;
	}

	/**
	 * 用于获取Event是否被关注
	 * 
	 * @return boolean Event是否被关注
	 */
	public boolean isEventIsLike() {
		return eventIsLike;
	}

	/**
	 * 用于给Event是否被关注赋值
	 * 
	 * @param eventIsLike
	 *            Event是否被关注
	 */
	public void setEventIsLike(boolean eventIsLike) {
		this.eventIsLike = eventIsLike;
	}

	/**
	 * 用于获取Event的简介
	 * 
	 * @return String Event的简介
	 */
	public String getEventDescription() {
		return eventDescription;
	}

	/**
	 * 用于给Event的简介赋值
	 * 
	 * @param eventDescription
	 *            Event的简介
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	/**
	 * 用于在LogCat输出时,打印一个Event信息
	 */
	@Override
	public String toString() {
		return "event:[" + eventId + " ; " + eventName + " ; " + eventDate
				+ " ; " + eventTime + " ; " + eventBuilding + " ; "
				+ eventLocation + " ; " + eventSpeaker + " ; ";
	}

	/**
	 * 定义一个类Parcelable,用于在通过Intent传值时,将Event传入Bundle
	 */
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

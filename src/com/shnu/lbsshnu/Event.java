package com.shnu.lbsshnu;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * ��Event<br>
 * ��Eventʵ���࣬ʹ�����Զ����Parcelable��ʵ��ʹ��Bundle���ݶ���ķ���<br>
 */
public class Event implements Parcelable {
	/**
	 * Event��id
	 */
	private int eventId;
	/**
	 * Event������
	 */
	private String eventName;
	/**
	 * Event�ķ�������
	 */
	private String eventDate;
	/**
	 * Event�ķ���ʱ��
	 */
	private String eventTime;
	/**
	 * Event��λ������
	 */
	private String eventLocation;
	/**
	 * Event��λ��id�����ͼ����idƥ��
	 */
	private int eventBuilding;
	/**
	 * Event�����ͣ�1Ϊѧ��������2Ϊ��Ӱ�ݳ���3Ϊ��Ʒ�γ�
	 */
	private int eventType;
	/**
	 * Event����Ҫ������
	 */
	private String eventSpeaker;
	/**
	 * Event�ı�����ͷ��
	 */
	private String eventSpeakerTitle;
	/**
	 * Event�Ƿ񱻹�ע
	 */
	private boolean eventIsLike;
	/**
	 * Event�ļ��
	 */
	private String eventDescription;

	public Event() {
		super();
	}

	/**
	 * ͨ��Cursor����ʼ��Event
	 * 
	 * @param cursor
	 *            ��ѯ���
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
	 * ���ڻ�ȡEvent��id
	 * 
	 * @return int Event��id
	 */
	public int getEventId() {
		return eventId;
	}

	/**
	 * ���ڸ�Event��id��ֵ
	 * 
	 * @param eventId
	 *            Event��id
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	/**
	 * ���ڻ�ȡEvent������
	 * 
	 * @return String Event������
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * ���ڸ�Event�����Ƹ�ֵ
	 * 
	 * @param eventName
	 *            Event������
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * ���ڻ�ȡEvent�ķ�������
	 * 
	 * @return String Event�ķ�������
	 */
	public String getEventDate() {
		return eventDate;
	}

	/**
	 * ���ڸ�Event�ķ������ڸ�ֵ
	 * 
	 * @param eventDate
	 *            Event�ķ�������
	 */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	/**
	 * ���ڻ�ȡEvent�ķ���ʱ��
	 * 
	 * @return String Event�ķ���ʱ��
	 */
	public String getEventTime() {
		return eventTime;
	}

	/**
	 * ���ڸ�Event�ķ���ʱ�丳ֵ
	 * 
	 * @param eventTime
	 *            Event�ķ���ʱ��
	 */
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	/**
	 * ���ڻ�ȡEvent��λ������
	 * 
	 * @return String Event��λ������
	 */
	public String getEventLocation() {
		return eventLocation;
	}

	/**
	 * ���ڸ�Event��λ�����Ƹ�ֵ
	 * 
	 * @param eventLocation
	 *            Event��λ������
	 */
	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	/**
	 * ���ڻ�ȡEvent��λ��id
	 * 
	 * @return int Event��λ��id�����ͼ����idƥ��
	 */
	public int getEventBuilding() {
		return eventBuilding;
	}

	/**
	 * ���ڸ� Event��λ��id��ֵ
	 * 
	 * @param eventBuilding
	 *            Event��λ��id�����ͼ����idƥ��
	 */
	public void setEventBuilding(int eventBuilding) {
		this.eventBuilding = eventBuilding;
	}

	/**
	 * ���ڻ�ȡEvent������
	 * 
	 * @return int Event�����ͣ�1Ϊѧ��������2Ϊ��Ӱ�ݳ���3Ϊ��Ʒ�γ�
	 */
	public int getEventType() {
		return eventType;
	}

	/**
	 * ���ڸ�Event�����͸�ֵ
	 * 
	 * @param eventType
	 *            Event�����ͣ�1Ϊѧ��������2Ϊ��Ӱ�ݳ���3Ϊ��Ʒ�γ�
	 */
	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	/**
	 * ���ڻ�ȡEvent����Ҫ������
	 * 
	 * @return String Event����Ҫ������
	 */
	public String getEventSpeaker() {
		return eventSpeaker;
	}

	/**
	 * ���ڸ�Event����Ҫ�����߸�ֵ
	 * 
	 * @param eventSpeaker
	 *            Event����Ҫ������
	 */
	public void setEventSpeaker(String eventSpeaker) {
		this.eventSpeaker = eventSpeaker;
	}

	/**
	 * ���ڻ�ȡEvent�ı�����ͷ��
	 * 
	 * @return String Event�ı�����ͷ��
	 */
	public String getEventSpeakerTitle() {
		return eventSpeakerTitle;
	}

	/**
	 * ���ڸ�Event�ı�����ͷ�θ�ֵ
	 * 
	 * @param eventSpeakerTitle
	 *            Event�ı�����ͷ��
	 */
	public void setEventSpeakerTitle(String eventSpeakerTitle) {
		this.eventSpeakerTitle = eventSpeakerTitle;
	}

	/**
	 * ���ڻ�ȡEvent�Ƿ񱻹�ע
	 * 
	 * @return boolean Event�Ƿ񱻹�ע
	 */
	public boolean isEventIsLike() {
		return eventIsLike;
	}

	/**
	 * ���ڸ�Event�Ƿ񱻹�ע��ֵ
	 * 
	 * @param eventIsLike
	 *            Event�Ƿ񱻹�ע
	 */
	public void setEventIsLike(boolean eventIsLike) {
		this.eventIsLike = eventIsLike;
	}

	/**
	 * ���ڻ�ȡEvent�ļ��
	 * 
	 * @return String Event�ļ��
	 */
	public String getEventDescription() {
		return eventDescription;
	}

	/**
	 * ���ڸ�Event�ļ�鸳ֵ
	 * 
	 * @param eventDescription
	 *            Event�ļ��
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	/**
	 * ������LogCat���ʱ,��ӡһ��Event��Ϣ
	 */
	@Override
	public String toString() {
		return "event:[" + eventId + " ; " + eventName + " ; " + eventDate
				+ " ; " + eventTime + " ; " + eventBuilding + " ; "
				+ eventLocation + " ; " + eventSpeaker + " ; ";
	}

	/**
	 * ����һ����Parcelable,������ͨ��Intent��ֵʱ,��Event����Bundle
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

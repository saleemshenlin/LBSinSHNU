/*
 * 通过Android使用了自定义的Parcelable，实现使用Bundle传递对象的方法。
 * read 和 write 的顺序要一致
 */
package com.shnu.lbsshnu;

import android.os.Parcel;
import android.os.Parcelable;

public class ActivityClass implements Parcelable {
	private int activityId;
	private String activityName;
	private String activityDate;
	private String activityTime;
	private String activityLocation;
	private int activityBuilding;
	private int activityType;
	private String activitySpeaker;
	private String activitySpeakerTitle;
	private boolean activityIsLike;
	private String activityDescription;

	public ActivityClass() {
		super();
	}

	public ActivityClass(int activityId, String activityName,
			String activityDate, String activityTime, String activityLocation,
			int activityBuilding, int activityType, String activitySpeaker,
			String activitySpeakerTitle, boolean activityIsLike,
			String activityDescription) {
		this.activityId = activityId;
		this.activityName = activityName;
		this.activityDate = activityDate;
		this.activityTime = activityTime;
		this.activityLocation = activityLocation;
		this.activityBuilding = activityBuilding;
		this.activityType = activityType;
		this.activitySpeaker = activitySpeakerTitle;
		this.activityIsLike = activityIsLike;
		this.activityDescription = activityDescription;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}

	public String getActivityTime() {
		return activityTime;
	}

	public void setActivityTime(String activityTime) {
		this.activityTime = activityTime;
	}

	public String getActivityLocation() {
		return activityLocation;
	}

	public void setActivityLocation(String activityLocation) {
		this.activityLocation = activityLocation;
	}

	public int getActivityBuilding() {
		return activityBuilding;
	}

	public void setActivityBuilding(int activityBuilding) {
		this.activityBuilding = activityBuilding;
	}

	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public String getActivitySpeaker() {
		return activitySpeaker;
	}

	public void setActivitySpeaker(String activitySpeaker) {
		this.activitySpeaker = activitySpeaker;
	}

	public String getActivitySpeakerTitle() {
		return activitySpeakerTitle;
	}

	public void setActivitySpeakerTitle(String activitySpeakerTitle) {
		this.activitySpeakerTitle = activitySpeakerTitle;
	}

	public boolean isActivityIsLike() {
		return activityIsLike;
	}

	public void setActivityIsLike(boolean activityIsLike) {
		this.activityIsLike = activityIsLike;
	}

	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

	@Override
	public String toString() {
		return "Activity:[" + activityId + " ; " + activityName + " ; "
				+ activityDate + " ; " + activityTime + " ; "
				+ activityBuilding + " ; " + activityLocation + " ; "
				+ activitySpeaker + " ; ";
	}

	public static final Parcelable.Creator<ActivityClass> CREATOR = new Creator<ActivityClass>() {

		@Override
		public ActivityClass createFromParcel(Parcel source) {
			ActivityClass activity = new ActivityClass();
			activity.activityId = source.readInt();
			activity.activityName = source.readString();
			activity.activityDate = source.readString();
			activity.activityTime = source.readString();
			activity.activityLocation = source.readString();
			activity.activityBuilding = source.readInt();
			activity.activityType = source.readInt();
			switch (source.readInt()) {
			case 1:
				activity.activityIsLike = true;
			default:
				activity.activityIsLike = false;
			}
			activity.activitySpeaker = source.readString();
			activity.activitySpeakerTitle = source.readString();
			activity.activityDescription = source.readString();
			return activity;
		}

		@Override
		public ActivityClass[] newArray(int size) {
			return new ActivityClass[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(activityId);
		parcel.writeString(activityName);
		parcel.writeString(activityDate);
		parcel.writeString(activityTime);
		parcel.writeString(activityLocation);
		parcel.writeInt(activityBuilding);
		parcel.writeInt(activityType);
		if (activityIsLike) {
			parcel.writeInt(1);
		} else {
			parcel.writeInt(0);
		}
		parcel.writeString(activitySpeaker);
		parcel.writeString(activitySpeakerTitle);
		parcel.writeString(activityDescription);
	}
}

package com.shnu.lbsshnu;

import android.os.Parcel;
import android.os.Parcelable;

class Result implements Parcelable {

	private int id;
	private int num;

	public int getId() {
		return this.id;
	}

	public void setId(int value) {
		this.id = value;
	}

	public int getNum() {
		return this.num;
	}

	public void setNum(int value) {
		this.num = value;
	}

	public Result() {
		super();
	}

	public Result(int id, int num) {
		this.id = id;
		this.num = num;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(id);
		parcel.writeInt(num);
	}

	public static final Parcelable.Creator<Result> CREATOR = new Creator<Result>() {

		@Override
		public Result createFromParcel(Parcel source) {
			Result result = new Result();
			result.id = source.readInt();
			result.num = source.readInt();
			return result;
		}

		@Override
		public Result[] newArray(int size) {
			return new Result[size];
		}
	};
}
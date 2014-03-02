
package com.uphyca.robota.engine;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Help implements Serializable, Parcelable {

    private String mEvent;
    private String mDescription;

    public Help() {
    }

    public Help(String event, String description) {
        mEvent = event;
        mDescription = description;
    }

    public String getEvent() {
        return mEvent;
    }

    public void setEvent(String event) {
        mEvent = event;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mEvent);
        dest.writeString(this.mDescription);
    }

    private Help(Parcel in) {
        this.mEvent = in.readString();
        this.mDescription = in.readString();
    }

    public static Creator<Help> CREATOR = new Creator<Help>() {
        public Help createFromParcel(Parcel source) {
            return new Help(source);
        }

        public Help[] newArray(int size) {
            return new Help[size];
        }
    };
}

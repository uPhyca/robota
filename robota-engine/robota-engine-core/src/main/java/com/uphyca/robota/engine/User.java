
package com.uphyca.robota.engine;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable, Parcelable {

    private long mId;
    private String mName;

    public User() {
    }

    public long getId() {
        return mId;
    }

    public User setId(long id) {
        mId = id;
        return this;
    }

    public String getName() {
        return mName;
    }

    public User setName(String name) {
        mName = name;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
    }

    private User(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
    }

    public static Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

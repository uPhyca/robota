
package com.uphyca.robota.engine;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TextMessage implements Serializable, Parcelable {

    private User mUser;
    private String mText;
    private MessageBundle mData;

    public TextMessage() {
    }

    public User getUser() {
        return mUser;
    }

    public TextMessage setUser(User user) {
        mUser = user;
        return this;
    }

    public String getText() {
        return mText;
    }

    public TextMessage setText(String text) {
        mText = text;
        return this;
    }

    public MessageBundle getData() {
        return mData;
    }

    public TextMessage setData(MessageBundle data) {
        mData = data;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mUser, 0);
        dest.writeString(this.mText);
        dest.writeParcelable(this.mData, 0);
    }

    private TextMessage(Parcel in) {
        this.mUser = in.readParcelable(((Object) mUser).getClass()
                                                       .getClassLoader());
        this.mText = in.readString();
        this.mData = in.readParcelable(((Object) mData).getClass()
                                                       .getClassLoader());
    }

    public static Creator<TextMessage> CREATOR = new Creator<TextMessage>() {
        public TextMessage createFromParcel(Parcel source) {
            return new TextMessage(source);
        }

        public TextMessage[] newArray(int size) {
            return new TextMessage[size];
        }
    };
}

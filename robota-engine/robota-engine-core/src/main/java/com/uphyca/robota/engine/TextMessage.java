/*
 * Copyright (C) 2014 uPhyca Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uphyca.robota.engine;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Represents an text message entity.
 * 
 * @author Sosuke Masui (masui@uphyca.com)
 */
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

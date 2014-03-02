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
 * Represents an user entity.
 * 
 * @author Sosuke Masui (masui@uphyca.com)
 */
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

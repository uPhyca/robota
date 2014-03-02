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
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an message entity.
 * 
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class MessageBundle implements Serializable, Parcelable {

    private long mId;
    private String mBody;
    private String mBodyPlain;
    private List<String> mImageUrls;
    private boolean mMultiline;
    private List<Long> mMentions;
    private String mCreatedAt;
    private long mRoomId;
    private String mRoomName;
    private String mOrganizationSlug;
    private String mSenderType;
    private long mSenderId;
    private String mSenderName;
    private String mSenderIconUrl;

    public MessageBundle() {
    }

    public long getId() {
        return mId;
    }

    public MessageBundle setId(long id) {
        this.mId = id;
        return this;
    }

    public String getBody() {
        return mBody;
    }

    public MessageBundle setBody(String body) {
        this.mBody = body;
        return this;
    }

    public String getBodyPlain() {
        return mBodyPlain;
    }

    public MessageBundle setBodyPlain(String bodyPlain) {
        this.mBodyPlain = bodyPlain;
        return this;
    }

    public List<String> getImageUrls() {
        return mImageUrls;
    }

    public MessageBundle setImageUrls(List<String> imageUrls) {
        this.mImageUrls = imageUrls;
        return this;
    }

    public boolean isMultiline() {
        return mMultiline;
    }

    public MessageBundle setMultiline(boolean multiline) {
        this.mMultiline = multiline;
        return this;
    }

    public List<Long> getMentions() {
        return mMentions;
    }

    public MessageBundle setMentions(List<Long> mentions) {
        this.mMentions = mentions;
        return this;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public MessageBundle setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
        return this;
    }

    public long getRoomId() {
        return mRoomId;
    }

    public MessageBundle setRoomId(long roomId) {
        this.mRoomId = roomId;
        return this;
    }

    public String getRoomName() {
        return mRoomName;
    }

    public MessageBundle setRoomName(String roomName) {
        mRoomName = roomName;
        return this;
    }

    public String getOrganizationSlug() {
        return mOrganizationSlug;
    }

    public MessageBundle setOrganizationSlug(String organizationSlug) {
        mOrganizationSlug = organizationSlug;
        return this;
    }

    public String getSenderType() {
        return mSenderType;
    }

    public MessageBundle setSenderType(String senderType) {
        this.mSenderType = senderType;
        return this;
    }

    public long getSenderId() {
        return mSenderId;
    }

    public MessageBundle setSenderId(long senderId) {
        this.mSenderId = senderId;
        return this;
    }

    public String getSenderName() {
        return mSenderName;
    }

    public MessageBundle setSenderName(String senderName) {
        this.mSenderName = senderName;
        return this;
    }

    public String getSenderIconUrl() {
        return mSenderIconUrl;
    }

    public MessageBundle setSenderIconUrl(String senderIconUrl) {
        this.mSenderIconUrl = senderIconUrl;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mBody);
        dest.writeString(this.mBodyPlain);
        dest.writeList(this.mImageUrls);
        dest.writeByte(mMultiline ? (byte) 1 : (byte) 0);
        dest.writeList(this.mMentions);
        dest.writeString(this.mCreatedAt);
        dest.writeLong(this.mRoomId);
        dest.writeString(this.mRoomName);
        dest.writeString(this.mOrganizationSlug);
        dest.writeString(this.mSenderType);
        dest.writeLong(this.mSenderId);
        dest.writeString(this.mSenderName);
        dest.writeString(this.mSenderIconUrl);
    }

    private MessageBundle(Parcel in) {
        this.mId = in.readLong();
        this.mBody = in.readString();
        this.mBodyPlain = in.readString();
        this.mImageUrls = new ArrayList<String>();
        in.readList(this.mImageUrls, String.class.getClassLoader());
        this.mMultiline = in.readByte() != 0;
        this.mMentions = new ArrayList<Long>();
        in.readList(this.mMentions, Long.class.getClassLoader());
        this.mCreatedAt = in.readString();
        this.mRoomId = in.readLong();
        this.mRoomName = in.readString();
        this.mOrganizationSlug = in.readString();
        this.mSenderType = in.readString();
        this.mSenderId = in.readLong();
        this.mSenderName = in.readString();
        this.mSenderIconUrl = in.readString();
    }

    public static Creator<MessageBundle> CREATOR = new Creator<MessageBundle>() {
        public MessageBundle createFromParcel(Parcel source) {
            return new MessageBundle(source);
        }

        public MessageBundle[] newArray(int size) {
            return new MessageBundle[size];
        }
    };
}

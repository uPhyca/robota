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

package com.uphyca.robota.data;

import com.uphyca.idobata.event.MessageCreatedEvent;
import com.uphyca.idobata.model.Message;
import com.uphyca.idobata.model.MessageBean;
import com.uphyca.idobata.model.Organization;
import com.uphyca.idobata.model.Room;

import java.util.List;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public abstract class IdobataUtils {

    private IdobataUtils() {
        throw new UnsupportedOperationException();
    }

    public static Organization findOrganizationById(long id, List<Organization> organizations) {
        for (Organization org : organizations) {
            if (org.getId() == id) {
                return org;
            }
        }
        return null;
    }

    public static Room findRoomById(long id, List<Room> rooms) {
        for (Room room : rooms) {
            if (room.getId() == id) {
                return room;
            }
        }
        return null;
    }

    public static Message eventToMessage(MessageCreatedEvent event) {
        Message message = new MessageBean();
        message.setBody(event.getBody());
        message.setRoomId(event.getRoomId());
        message.setBodyPlain(event.getBodyPlain());
        message.setSenderName(event.getSenderName());
        message.setSenderId(event.getSenderId());
        message.setMentions(event.getMentions());
        message.setCreatedAt(event.getCreatedAt());
        message.setId(event.getId());
        message.setImageUrls(event.getImageUrls());
        message.setMultiline(event.isMultiline());
        message.setSenderIconUrl(event.getSenderIconUrl());
        message.setSenderType(event.getSenderType());
        return message;
    }

    public static String[] toStringArray(List<String> list) {
        if (list == null) {
            return new String[0];
        }
        return list.toArray(new String[list.size()]);
    }

    public static long[] toLongArray(List<Long> list) {
        if (list == null) {
            return new long[0];
        }
        int size = list.size();
        long[] longs = new long[size];
        for (int i = 0; i < size; ++i) {
            longs[i] = list.get(i);
        }
        return longs;
    }
}

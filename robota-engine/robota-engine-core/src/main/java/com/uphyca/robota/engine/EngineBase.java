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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.uphyca.robota.engine.Robota.EXTRA_BODY;
import static com.uphyca.robota.engine.Robota.EXTRA_BODY_PLAIN;
import static com.uphyca.robota.engine.Robota.EXTRA_BOT_API_TOKEN;
import static com.uphyca.robota.engine.Robota.EXTRA_BOT_ICON_URL;
import static com.uphyca.robota.engine.Robota.EXTRA_BOT_ID;
import static com.uphyca.robota.engine.Robota.EXTRA_BOT_NAME;
import static com.uphyca.robota.engine.Robota.EXTRA_CREATED_AT;
import static com.uphyca.robota.engine.Robota.EXTRA_ID;
import static com.uphyca.robota.engine.Robota.EXTRA_IMAGE_URLS;
import static com.uphyca.robota.engine.Robota.EXTRA_MENTIONS;
import static com.uphyca.robota.engine.Robota.EXTRA_MULTILINE;
import static com.uphyca.robota.engine.Robota.EXTRA_ORGANIZATION_SLUG;
import static com.uphyca.robota.engine.Robota.EXTRA_ROOM_ID;
import static com.uphyca.robota.engine.Robota.EXTRA_ROOM_NAME;
import static com.uphyca.robota.engine.Robota.EXTRA_SENDER_ICON_URL;
import static com.uphyca.robota.engine.Robota.EXTRA_SENDER_ID;
import static com.uphyca.robota.engine.Robota.EXTRA_SENDER_NAME;
import static com.uphyca.robota.engine.Robota.EXTRA_SENDER_TYPE;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public abstract class EngineBase extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Robota.ACTION_MESSAGE_CREATED)) {
            try {
                handleMessageCreated(context, intent);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an message from Idobata.
     * 
     * @param context The Context in which the receiver is running.
     * @param bot The Bot in which the receiver is running.
     * @param textMessage The message being received.
     * @return
     */
    protected abstract String onMessageReceived(Context context, Bot bot, TextMessage textMessage);

    private void handleMessageCreated(Context context, Intent intent) {

        Bot bot = parseBot(intent);
        TextMessage textMessage = parseTextMessage(intent);

        if (getResultData() != null) {
            return;
        }

        String result = onMessageReceived(context, bot, textMessage);
        if (result != null) {
            setResultData(result);
        }
    }

    private Bot parseBot(Intent intent) {

        long id = intent.getLongExtra(EXTRA_BOT_ID, 0);
        String name = intent.getStringExtra(EXTRA_BOT_NAME);
        String apiToken = intent.getStringExtra(EXTRA_BOT_API_TOKEN);
        String iconUrl = intent.getStringExtra(EXTRA_BOT_ICON_URL);

        return new Bot().setId(id)
                        .setName(name)
                        .setApiToken(apiToken)
                        .setIconUrl(iconUrl);
    }

    private TextMessage parseTextMessage(Intent intent) {

        long id = intent.getLongExtra(EXTRA_ID, 0);
        String body = intent.getStringExtra(EXTRA_BODY);
        String bodyPlain = intent.getStringExtra(EXTRA_BODY_PLAIN);
        String[] imageUrls = intent.getStringArrayExtra(EXTRA_IMAGE_URLS);
        boolean multiline = intent.getBooleanExtra(EXTRA_MULTILINE, false);
        long[] mentions = intent.getLongArrayExtra(EXTRA_MENTIONS);
        String createdAt = intent.getStringExtra(EXTRA_CREATED_AT);
        long roomId = intent.getLongExtra(EXTRA_ROOM_ID, 0);
        String roomName = intent.getStringExtra(EXTRA_ROOM_NAME);
        String organizationSlug = intent.getStringExtra(EXTRA_ORGANIZATION_SLUG);
        String senderType = intent.getStringExtra(EXTRA_SENDER_TYPE);
        long senderId = intent.getLongExtra(EXTRA_SENDER_ID, 0);
        String senderName = intent.getStringExtra(EXTRA_SENDER_NAME);
        String senderIconUrl = intent.getStringExtra(EXTRA_SENDER_ICON_URL);

        User user = new User().setId(senderId)
                              .setName(senderName);

        MessageBundle bundle = new MessageBundle().setId(id)
                                                  .setBody(body)
                                                  .setBodyPlain(bodyPlain)
                                                  .setImageUrls(Arrays.asList(imageUrls))
                                                  .setMultiline(multiline)
                                                  .setMentions(asList(mentions))
                                                  .setCreatedAt(createdAt)
                                                  .setRoomId(roomId)
                                                  .setRoomName(roomName)
                                                  .setOrganizationSlug(organizationSlug)
                                                  .setSenderType(senderType)
                                                  .setSenderId(senderId)
                                                  .setSenderName(senderName)
                                                  .setSenderIconUrl(senderIconUrl);
        return new TextMessage().setUser(user)
                                .setText(bodyPlain)
                                .setData(bundle);
    }

    private static List<Long> asList(long[] array) {
        ArrayList<Long> list = new ArrayList<Long>();
        for (long each : array) {
            list.add(each);
        }
        return list;
    }
}

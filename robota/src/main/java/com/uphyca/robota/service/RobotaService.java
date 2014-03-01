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

package com.uphyca.robota.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;

import com.uphyca.idobata.ErrorListener;
import com.uphyca.idobata.Idobata;
import com.uphyca.idobata.IdobataError;
import com.uphyca.idobata.IdobataStream;
import com.uphyca.idobata.event.ConnectionEvent;
import com.uphyca.idobata.event.MessageCreatedEvent;
import com.uphyca.idobata.event.RoomTouchedEvent;
import com.uphyca.idobata.model.Bot;
import com.uphyca.idobata.model.Message;
import com.uphyca.robota.InjectionUtils;
import com.uphyca.robota.data.api.BackoffPolicy;
import com.uphyca.robota.data.api.Environment;
import com.uphyca.robota.data.api.MessageFilter;
import com.uphyca.robota.data.api.PollingInterval;
import com.uphyca.robota.data.api.StreamConnection;
import com.uphyca.robota.data.prefs.LongPreference;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static com.uphyca.robota.Robota.ACTION_MESSAGE_CREATED;
import static com.uphyca.robota.Robota.EXTRA_BODY;
import static com.uphyca.robota.Robota.EXTRA_BODY_PLAIN;
import static com.uphyca.robota.Robota.EXTRA_BOT_ID;
import static com.uphyca.robota.Robota.EXTRA_BOT_NAME;
import static com.uphyca.robota.Robota.EXTRA_CREATED_AT;
import static com.uphyca.robota.Robota.EXTRA_ID;
import static com.uphyca.robota.Robota.EXTRA_IMAGE_URLS;
import static com.uphyca.robota.Robota.EXTRA_MENTIONS;
import static com.uphyca.robota.Robota.EXTRA_MULTILINE;
import static com.uphyca.robota.Robota.EXTRA_ORGANIZATION_SLUG;
import static com.uphyca.robota.Robota.EXTRA_ROOM_ID;
import static com.uphyca.robota.Robota.EXTRA_ROOM_NAME;
import static com.uphyca.robota.Robota.EXTRA_SENDER_ICON_URL;
import static com.uphyca.robota.Robota.EXTRA_SENDER_ID;
import static com.uphyca.robota.Robota.EXTRA_SENDER_NAME;
import static com.uphyca.robota.Robota.EXTRA_SENDER_TYPE;
import static com.uphyca.robota.data.IdobataUtils.eventToMessage;
import static com.uphyca.robota.data.IdobataUtils.toLongArray;
import static com.uphyca.robota.data.IdobataUtils.toStringArray;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class RobotaService extends Service implements IdobataStream.ConnectionListener, ErrorListener {

    private static final String THREAD_NAME = "RobotaService";
    private static final int OPEN = 0;
    private static final long DELAY_MILLIS_TO_RESTART = TimeUnit.SECONDS.toMillis(5);

    @Inject
    Idobata mIdobata;

    @Inject
    AlarmManager mAlarmManager;

    @Inject
    Set<MessageFilter> mMessageFilters;

    @Inject
    @PollingInterval
    LongPreference mPollingIntervalPref;

    @Inject
    Environment mEnvironment;

    @Inject
    @StreamConnection
    BackoffPolicy mBackoffPolicy;

    private Looper mServiceLooper;

    private ServiceHandler mServiceHandler;

    private Bot mBot;

    private IdobataStream mStream;

    @Override
    public void onCreate() {
        super.onCreate();
        ensureHandler();
        InjectionUtils.getObjectGraph(this)
                      .inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executeAt(mEnvironment.elapsedRealtime() + mPollingIntervalPref.get());
        mServiceHandler.sendEmptyMessage(OPEN);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        cancelPolling();
        closeQuietly();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void closed(ConnectionEvent event) {
        retryToConnect();
    }

    @Override
    public void opened(ConnectionEvent event) {
        mBackoffPolicy.reset();
    }

    @Override
    public void onError(IdobataError error) {
        retryToConnect();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        long cur = mEnvironment.elapsedRealtime();
        executeAt(cur + DELAY_MILLIS_TO_RESTART);
    }

    private void onMessageCreatedEvent(MessageCreatedEvent event) {
        Message message = eventToMessage(event);
        filterAndNotifyMessage(message, event.getOrganizationSlug(), event.getRoomName());
    }

    private void onRoomTouchedEvent(RoomTouchedEvent event) {
        //no-op.
    }

    private Uri toRoomUri(String organizationSlug, String roomName) {
        return Uri.parse(String.format("https://idobata.io/#/organization/%s/room/%s", organizationSlug, roomName));
    }

    private void filterAndNotifyMessage(Message message, String organizationSlug, String roomName) {
        for (MessageFilter each : mMessageFilters) {
            if (each.isSubscribed(mBot, message)) {
                notifyMessage(message, organizationSlug, roomName);
                return;
            }
        }
    }

    private void cancelPolling() {
        PendingIntent pi = buildPendingStartServiceIntent();
        mAlarmManager.cancel(pi);
    }

    private void retryToConnect() {
        if (mBackoffPolicy.isFailed()) {
            stopSelf();
            return;
        }

        long nextBackOffMillis = mBackoffPolicy.getNextBackOffMillis();
        try {
            executeAt(nextBackOffMillis);
        } finally {
            mBackoffPolicy.backoff();
        }
    }

    private void closeQuietly() {
        if (mStream == null) {
            return;
        }
        try {
            mStream.setConnectionListener(null)
                   .setErrorListener(null)
                   .close();
            mStream = null;
        } catch (IOException ignore) {
        }
    }

    private void executeAt(long triggerAtMillis) {
        PendingIntent pi = buildPendingStartServiceIntent();
        mAlarmManager.cancel(pi);
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, triggerAtMillis, pi);
    }

    private void ensureHandler() {
        HandlerThread t = new HandlerThread(THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        t.start();
        mServiceLooper = t.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    private void open() throws IdobataError {
        if (mBot == null) {
            mBot = mIdobata.getSeed()
                           .getRecords()
                           .getBot();
        }
        if (mStream == null) {
            mStream = mIdobata.openStream()
                              .setErrorListener(this)
                              .setConnectionListener(this)
                              .subscribeMessageCreated(new MessageCreatedEventListener())
                              .subscribeRoomTouched(new RoomTouchedEventListener());
        }
        mStream.open();
    }

    private PendingIntent buildPendingStartServiceIntent() {
        final Intent intent = new Intent(RobotaService.this, RobotaService.class);
        return PendingIntent.getService(RobotaService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void notifyMessage(Message message, String organizationSlug, String roomName) {
        Intent intent = pack(message, organizationSlug, roomName);
        intent.putExtra(EXTRA_BOT_ID, mBot.getId());
        intent.putExtra(EXTRA_BOT_NAME, mBot.getName());

        Uri roomUri = toRoomUri(organizationSlug, roomName);
        sendOrderedBroadcast(intent, null, new MessageCreatedResultReceiver(roomUri), mServiceHandler, 0, null, null);
    }

    private Intent pack(Message message, String organizationSlug, String roomName) {
        return new Intent().setAction(ACTION_MESSAGE_CREATED)
                           .putExtra(EXTRA_ID, message.getId())
                           .putExtra(EXTRA_BODY, message.getBody())
                           .putExtra(EXTRA_BODY_PLAIN, message.getBodyPlain())
                           .putExtra(EXTRA_IMAGE_URLS, toStringArray(message.getImageUrls()))
                           .putExtra(EXTRA_MULTILINE, message.isMultiline())
                           .putExtra(EXTRA_MENTIONS, toLongArray(message.getMentions()))
                           .putExtra(EXTRA_CREATED_AT, message.getCreatedAt())
                           .putExtra(EXTRA_ROOM_ID, message.getRoomId())
                           .putExtra(EXTRA_ROOM_NAME, roomName)
                           .putExtra(EXTRA_ORGANIZATION_SLUG, organizationSlug)
                           .putExtra(EXTRA_SENDER_TYPE, message.getSenderType())
                           .putExtra(EXTRA_SENDER_ID, message.getSenderId())
                           .putExtra(EXTRA_SENDER_NAME, message.getSenderName())
                           .putExtra(EXTRA_SENDER_ICON_URL, message.getSenderIconUrl());
    }

    private final class MessageCreatedEventListener implements IdobataStream.Listener<MessageCreatedEvent> {

        @Override
        public void onEvent(MessageCreatedEvent event) {
            onMessageCreatedEvent(event);
        }
    }

    private final class RoomTouchedEventListener implements IdobataStream.Listener<RoomTouchedEvent> {
        @Override
        public void onEvent(RoomTouchedEvent event) {
            onRoomTouchedEvent(event);
        }
    }

    private final class MessageCreatedResultReceiver extends BroadcastReceiver {

        private final Uri roomUri;

        private MessageCreatedResultReceiver(Uri roomUri) {
            this.roomUri = roomUri;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String source = getResultData();
            if (source == null) {
                return;
            }
            PostTextService.postText(context, roomUri, source);
        }
    }

    private final class ServiceHandler extends Handler {

        private ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            try {
                open();
            } catch (IdobataError idobataError) {
                onError(idobataError);
            }
        }
    }
}

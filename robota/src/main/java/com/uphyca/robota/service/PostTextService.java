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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.uphyca.idobata.Idobata;
import com.uphyca.idobata.IdobataError;
import com.uphyca.robota.InjectionUtils;

import javax.inject.Inject;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class PostTextService extends IntentService {

    private static final String EXTRA_ROOM_URI = "room_uri";
    private static final String EXTRA_SOURCE = "source";

    public static void postText(Context context, Uri roomUri, String source) {
        Intent intent = new Intent(context, PostTextService.class).putExtra(EXTRA_SOURCE, source)
                                                                  .putExtra(EXTRA_ROOM_URI, roomUri);
        context.startService(intent);
    }

    @Inject
    Idobata mIdobata;

    public PostTextService() {
        super("PostTextService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        InjectionUtils.getObjectGraph(this)
                      .inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri roomUri = intent.getParcelableExtra(EXTRA_ROOM_URI);
        String source = intent.getStringExtra(EXTRA_SOURCE);
        try {
            String[] tuple = roomUri.getFragment()
                                    .split("/");
            String organizationSlug = tuple[2];
            String roomName = tuple[4];
            long roomId = mIdobata.getRooms(organizationSlug, roomName)
                                  .get(0)
                                  .getId();
            mIdobata.postMessage(roomId, source);
        } catch (IdobataError idobataError) {
            idobataError.printStackTrace();
        }
    }
}

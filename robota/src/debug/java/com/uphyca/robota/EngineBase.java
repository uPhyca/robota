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

package com.uphyca.robota;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class EngineBase extends BroadcastReceiver {

    private static final String MENTION_TEMPLATE = "@%s ";

    @Override
    public final void onReceive(Context context, Intent intent) {
        String result = getResultData();

        if (result != null) {
            return;
        }

        String botName = intent.getStringExtra(Robota.EXTRA_BOT_NAME);
        String bodyPlain = intent.getStringExtra(Robota.EXTRA_BODY_PLAIN);

        if (!isMention(botName, bodyPlain)) {
            return;
        }

        String mentionBody = getMentionBody(botName, bodyPlain);

        onMentionReceived(context, mentionBody);
    }

    protected abstract void onMentionReceived(Context context, String mentionBody);

    private boolean isMention(String botName, String bodyPlain) {
        return bodyPlain.indexOf(String.format(MENTION_TEMPLATE, botName)) >= 0;
    }

    private String getMentionBody(String botName, String bodyPlain) {
        String head = String.format(MENTION_TEMPLATE, botName);
        int i = bodyPlain.indexOf(head);
        String content = bodyPlain.substring(i + head.length());
        return content;
    }
}

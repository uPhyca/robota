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

import android.content.Context;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeEngine extends EngineBase {

    private static final String EVENT_PATTERN_TEMPLATE = "^[@]?%s[:,]?\\s*(?:time$)";

    @Override
    protected String onMessageReceived(Context context, Bot bot, TextMessage textMessage) {
        Pattern pt = Pattern.compile(String.format(EVENT_PATTERN_TEMPLATE, bot.getName()), Pattern.CASE_INSENSITIVE);
        Matcher mt = pt.matcher(textMessage.getText());
        if (!mt.find()) {
            return null;
        }
        return new Date().toString();
    }

    @Override
    protected Help describe(Context context) {
        return new Help("time", "Reply with current time");
    }
}

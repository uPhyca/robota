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

package com.uphyca.robota.shell;

import android.content.Context;

import com.uphyca.robota.engine.Bot;
import com.uphyca.robota.engine.EngineBase;
import com.uphyca.robota.engine.TextMessage;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run remote shell command
 * 
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class ShellEngine extends EngineBase {

    private static final String EVENT_PATTERN_TEMPLATE = "^[@]?%s[:,]?\\s*(?:shell\\s*((.*)?)$)";

    @Override
    protected String onMessageReceived(Context context, Bot bot, TextMessage textMessage) {
        Pattern pt = Pattern.compile(String.format(EVENT_PATTERN_TEMPLATE, bot.getName()), Pattern.CASE_INSENSITIVE);
        Matcher mt = pt.matcher(textMessage.getText());
        if (!mt.find()) {
            return null;
        }
        Process process = null;
        StringBuilder builder = new StringBuilder();
        try {
            process = Runtime.getRuntime()
                             .exec(mt.group(1));
            process.waitFor();
            drain(builder, process.getInputStream());
            drain(builder, process.getErrorStream());
        } catch (Exception ignore) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(ignore.getMessage());
        } finally {
            if (process != null) {
                closeQuietly(process.getOutputStream());
                closeQuietly(process.getInputStream());
                closeQuietly(process.getErrorStream());
            }
        }
        return builder.toString();
    }

    private static void drain(StringBuilder builder, InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        for (String line; (line = reader.readLine()) != null;) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(line);
        }
    }

    private static void closeQuietly(Closeable resource) {
        if (resource == null) {
            return;
        }
        try {
            resource.close();
        } catch (IOException ignore) {
        }
    }
}

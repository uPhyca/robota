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

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class Robota {

    private static final String INTENT = "com.uphyca.robota";
    private static final String ACTION = INTENT + ".action";
    private static final String EXTRA = INTENT + ".extra";
    private static final String PERMISSION = INTENT + ".permission";

    // Message created event

    public static final String ACTION_MESSAGE_CREATED = ACTION + ".MESSAGE_CREATED";

    public static final String PERMISSION_RECEIVE_MESSAGE_CREATED = PERMISSION + ".RECEIVE_MESSAGE_CREATED";

    public static final String EXTRA_ID = EXTRA + ".ID";
    public static final String EXTRA_BODY = EXTRA + ".BODY";
    public static final String EXTRA_BODY_PLAIN = EXTRA + ".BODY_PLAIN";
    public static final String EXTRA_IMAGE_URLS = EXTRA + ".IMAGE_URLS";
    public static final String EXTRA_MULTILINE = EXTRA + ".MULTILINE";
    public static final String EXTRA_MENTIONS = EXTRA + ".MENTIONS";
    public static final String EXTRA_CREATED_AT = EXTRA + ".CREATED_AT";
    public static final String EXTRA_ROOM_ID = EXTRA + ".ROOM_ID";
    public static final String EXTRA_ROOM_NAME = EXTRA + ".ROOM_NAME";
    public static final String EXTRA_ORGANIZATION_SLUG = EXTRA + ".ORGANIZATION_SLUG";
    public static final String EXTRA_SENDER_TYPE = EXTRA + ".SENDER_TYPE";
    public static final String EXTRA_SENDER_ID = EXTRA + ".SENDER_ID";
    public static final String EXTRA_SENDER_NAME = EXTRA + ".SENDER_NAME";
    public static final String EXTRA_SENDER_ICON_URL = EXTRA + ".SENDER_ICON_URL";

    public static final String EXTRA_BOT_ID = EXTRA + ".BOT_ID";
    public static final String EXTRA_BOT_NAME = EXTRA + ".BOT_NAME";
    public static final String EXTRA_BOT_API_TOKEN = EXTRA + ".BOT_API_TOKEN";
    public static final String EXTRA_BOT_ICON_URL = EXTRA + ".BOT_ICON_URL";
}

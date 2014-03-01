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

import com.uphyca.idobata.model.Guy;
import com.uphyca.idobata.model.Message;
import com.uphyca.robota.data.api.MessageFilter;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class MyMessageFilter implements MessageFilter {

    @Override
    public boolean isSubscribed(Guy guy, Message message) {
        return guy.getId() != message.getSenderId();
    }
}

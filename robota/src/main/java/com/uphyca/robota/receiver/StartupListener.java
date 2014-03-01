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

package com.uphyca.robota.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.uphyca.robota.InjectionUtils;
import com.uphyca.robota.data.api.Enabled;
import com.uphyca.robota.data.prefs.BooleanPreference;
import com.uphyca.robota.service.RobotaService;

import javax.inject.Inject;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class StartupListener extends BroadcastReceiver {

    @Inject
    @Enabled
    BooleanPreference mEnabledPreference;

    @Override
    public void onReceive(Context context, Intent intent) {
        InjectionUtils.getObjectGraph(context)
                      .inject(this);

        if (mEnabledPreference.get()) {
            context.startService(new Intent(context, RobotaService.class));
        }
    }
}

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.uphyca.robota.InjectionUtils;
import com.uphyca.robota.data.api.Enabled;
import com.uphyca.robota.data.prefs.BooleanPreference;
import com.uphyca.robota.service.RobotaService;

import javax.inject.Inject;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class ConnectivityListener extends BroadcastReceiver {

    @Inject
    @Enabled
    BooleanPreference mEnabledPreference;

    @Override
    public void onReceive(Context context, Intent intent) {
        InjectionUtils.getObjectGraph(context)
                      .inject(this);

        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras()
                                                 .get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                if (mEnabledPreference.get()) {
                    context.startService(new Intent(context, RobotaService.class));
                }
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                context.stopService(new Intent(context, RobotaService.class));
            }
        }
    }
}

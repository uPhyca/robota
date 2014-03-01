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

package com.uphyca.robota.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ToggleButton;

import com.uphyca.robota.InjectionUtils;
import com.uphyca.robota.R;
import com.uphyca.robota.data.api.Enabled;
import com.uphyca.robota.data.api.Main;
import com.uphyca.robota.data.api.Networking;
import com.uphyca.robota.data.prefs.BooleanPreference;
import com.uphyca.robota.service.RobotaService;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class MainActivity extends Activity {

    @Inject
    @Enabled
    BooleanPreference mEnablePreference;

    @Inject
    Vibrator mVibrator;

    @InjectView(R.id.enabled)
    ToggleButton mEnabled;

    @Inject
    @Networking
    Executor mExecutor;

    @Inject
    @Main
    Executor mDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectionUtils.getObjectGraph(this)
                      .inject(this);
        setContentView(R.layout.activity_main);
        encureViews();

        if (savedInstanceState == null && mEnablePreference.get()) {
            Intent intent = new Intent(this, RobotaService.class);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.enabled)
    void onEnabledClick() {
        mVibrator.vibrate(10);
    }

    @OnCheckedChanged(R.id.enabled)
    void onEnabledChecked(boolean checked) {
        mEnablePreference.set(checked);

        Intent intent = new Intent(this, RobotaService.class);
        if (checked) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    private void encureViews() {
        ButterKnife.inject(this);
        mEnabled.setChecked(mEnablePreference.get());
    }

    private void startSettings() {
        startActivity(new Intent(this, SettingActivity.class));
    }
}

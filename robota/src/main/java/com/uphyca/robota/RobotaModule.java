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

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;

import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;
import com.uphyca.idobata.Idobata;
import com.uphyca.idobata.IdobataBuilder;
import com.uphyca.idobata.RequestInterceptor;
import com.uphyca.idobata.TokenAuthenticator;
import com.uphyca.idobata.http.Client;
import com.uphyca.robota.data.AndroidEnvironment;
import com.uphyca.robota.data.AndroidExecutor;
import com.uphyca.robota.data.ExponentialBackoff;
import com.uphyca.robota.data.MyMessageFilter;
import com.uphyca.robota.data.OkClient;
import com.uphyca.robota.data.api.ApiToken;
import com.uphyca.robota.data.api.BackoffPolicy;
import com.uphyca.robota.data.api.Enabled;
import com.uphyca.robota.data.api.Environment;
import com.uphyca.robota.data.api.Main;
import com.uphyca.robota.data.api.MessageFilter;
import com.uphyca.robota.data.api.Networking;
import com.uphyca.robota.data.api.PollingInterval;
import com.uphyca.robota.data.api.StreamConnection;
import com.uphyca.robota.data.prefs.BooleanPreference;
import com.uphyca.robota.data.prefs.LongPreference;
import com.uphyca.robota.data.prefs.StringPreference;
import com.uphyca.robota.receiver.ConnectivityListener;
import com.uphyca.robota.receiver.StartupListener;
import com.uphyca.robota.service.PostTextService;
import com.uphyca.robota.service.RobotaService;
import com.uphyca.robota.ui.BotActivity;
import com.uphyca.robota.ui.MainActivity;
import com.uphyca.robota.ui.OssLicensesActivity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
@Module(injects = {
        RobotaService.class, //
        PostTextService.class, //
        MainActivity.class, //
        BotActivity.BotFragment.class, //
        ConnectivityListener.class, //
        StartupListener.class, //
        OssLicensesActivity.LicenseDialogFragment.class, //
})
public class RobotaModule {

    private final Application mApplication;
    private static final long DEFAULT_POLLING_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(5);
    private static final long BACKOFF_SLEEP_MILLIS = TimeUnit.SECONDS.toMillis(5);
    private static final int BACKOFF_TRIES = Integer.MAX_VALUE;

    public RobotaModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        File cacheDir = new File(mApplication.getCacheDir(), "okhttp");
        final HttpResponseCache cache;
        try {
            cache = new HttpResponseCache(cacheDir, 10 * 1024 * 1024);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        okHttpClient.setResponseCache(cache);
        return okHttpClient;
    }

    @Provides
    @Singleton
    Client provideClient(OkHttpClient okHttpClient) {
        return new OkClient(okHttpClient);
    }

    @Provides
    @Singleton
    Idobata provideIdobata(RequestInterceptor requestInterceptor, Client client) {
        return new IdobataBuilder().setRequestInterceptor(requestInterceptor)
                                   .setClient(client)
                                   .build();
    }

    @Provides
    @Singleton
    RequestInterceptor provideRequestInterceptor(TokenAuthenticator.TokenProvider tokenProvider) {
        return new TokenAuthenticator(tokenProvider);
    }

    @Provides
    @Singleton
    AlarmManager provideAlarmManager() {
        return (AlarmManager) mApplication.getSystemService(Context.ALARM_SERVICE);
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return mApplication.getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    @PollingInterval
    LongPreference providePollingIntervalPreference(SharedPreferences pref) {
        return new LongPreference(pref, "polling_interval", DEFAULT_POLLING_INTERVAL_MILLIS);
    }

    @Provides(type = Provides.Type.SET)
    @Singleton
    MessageFilter provideMyMessageFilter() {
        return new MyMessageFilter();
    }

    @Provides
    @Singleton
    @Networking
    Executor provideHttpExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Provides
    @Singleton
    @Main
    Executor provideUiExecutor() {
        return new AndroidExecutor();
    }

    @Provides
    @Singleton
    Environment provideEnvironment() {
        return new AndroidEnvironment();
    }

    @Provides
    @Singleton
    @StreamConnection
    BackoffPolicy provideBackoffPolicy(Environment environment) {
        return new ExponentialBackoff(environment, BACKOFF_SLEEP_MILLIS, BACKOFF_TRIES);
    }

    @Provides
    @Singleton
    @Enabled
    BooleanPreference provideEnablePreference(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, "enabled");
    }

    @Provides
    @Singleton
    Vibrator provideVibrator() {
        return (Vibrator) mApplication.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Provides
    @Singleton
    @ApiToken
    StringPreference provideApiTokenPreference(SharedPreferences pref) {
        return new StringPreference(pref, "api_token");
    }

    @Provides
    @Singleton
    TokenAuthenticator.TokenProvider provideTokenProvider(final @ApiToken StringPreference apiTokenPreference) {
        return new TokenAuthenticator.TokenProvider() {
            @Override
            public String get() {
                return apiTokenPreference.get();
            }
        };
    }
}

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

package com.uphyca.robota.data.prefs;

import android.content.SharedPreferences;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class StringPreference {

    private final SharedPreferences mSharedPreferences;
    private final String mKey;
    private final String mDefaultValue;

    public StringPreference(SharedPreferences sharedPreferences, String key) {
        this(sharedPreferences, key, null);
    }

    public StringPreference(SharedPreferences sharedPreferences, String key, String defaultValue) {
        mSharedPreferences = sharedPreferences;
        mKey = key;
        mDefaultValue = defaultValue;
    }

    public String get() {
        return mSharedPreferences.getString(mKey, mDefaultValue);
    }

    public boolean isSet() {
        return mSharedPreferences.contains(mKey);
    }

    public void set(String value) {
        mSharedPreferences.edit()
                          .putString(mKey, value)
                          .apply();
    }

    public void delete() {
        mSharedPreferences.edit()
                          .remove(mKey)
                          .apply();
    }
}

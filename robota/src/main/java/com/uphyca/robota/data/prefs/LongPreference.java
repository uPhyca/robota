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
public class LongPreference {

    private final SharedPreferences mSharedPreferences;
    private final String mKey;
    private final long mDefaultValue;

    public LongPreference(SharedPreferences sharedPreferences, String key) {
        this(sharedPreferences, key, 0L);
    }

    public LongPreference(SharedPreferences sharedPreferences, String key, long defaultValue) {
        mSharedPreferences = sharedPreferences;
        mKey = key;
        mDefaultValue = defaultValue;
    }

    public long get() {
        return mSharedPreferences.getLong(mKey, mDefaultValue);
    }

    public boolean isSet() {
        return mSharedPreferences.contains(mKey);
    }

    public void set(long value) {
        mSharedPreferences.edit()
                          .putLong(mKey, value)
                          .apply();
    }

    public void delete() {
        mSharedPreferences.edit()
                          .remove(mKey)
                          .apply();
    }
}

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

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class AndroidExecutor implements Executor {

    private final Handler mHandler;

    public AndroidExecutor() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }
}

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

import com.squareup.okhttp.OkHttpClient;
import com.uphyca.idobata.http.Request;
import com.uphyca.idobata.http.UrlConnectionClient;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class OkClient extends UrlConnectionClient {

    private final OkHttpClient mOkClient;

    public OkClient(OkHttpClient okClient) {
        mOkClient = okClient;
    }

    public OkClient(OkHttpClient okClient, CookieHandler cookieHandler) {
        super(cookieHandler);
        mOkClient = okClient;
    }

    @Override
    protected HttpURLConnection openConnection(Request request) throws IOException {
        return mOkClient.open(URI.create(request.getUrl())
                                 .toURL());
    }
}

/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tiogasolutions.push.client;

import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.lib.jaxrs.client.BasicAuthorization;
import org.tiogasolutions.lib.jaxrs.client.SimpleRestClient;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.pub.common.PingPush;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushResponse;
import org.tiogasolutions.push.pub.domain.PubConfig;
import org.tiogasolutions.push.pub.internal.RequestErrors;

public class LivePushServerClient implements PushServerClient {

    private final SimpleRestClient client;

    public LivePushServerClient(String url) {
        PushObjectMapper objectMapper = new PushObjectMapper();
        TiogaJacksonTranslator translator = new TiogaJacksonTranslator(objectMapper);
        client = new SimpleRestClient(translator, url);
    }

    public LivePushServerClient(String url, String userName, String password) {
        PushObjectMapper objectMapper = new PushObjectMapper();
        TiogaJacksonTranslator translator = new TiogaJacksonTranslator(objectMapper);
        client = new SimpleRestClient(translator, url, new BasicAuthorization(userName, password));
    }

    public LivePushServerClient(SimpleRestClient client) {
        this.client = client;
    }

    public SimpleRestClient getClient() {
        return client;
    }

    @Override
    public long ping() {
        long start = System.currentTimeMillis();
        send(PingPush.newPush());
        return System.currentTimeMillis() - start;
    }

    @Override
    public PushResponse send(Push push) {
        push.validate(new RequestErrors()).assertNoErrors();
        return getClient().post(PushResponse.class, "/pushes", push);
    }

    @Override
    public PubConfig getSettings() {
        return getClient().get(PubConfig.class, "/settings");
    }
}

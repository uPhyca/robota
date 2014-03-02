[![Android app on Google Play](https://developer.android.com/images/brand/en_app_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.uphyca.robota)


robota makes your Android phone as Idobata(https://idobata.io) bot


robota
----

robota is a message router application that deliver the Intent when received a message from Idobata.

The Intent are contained following extras that contains the Idobata message.

 - id
 - body
 - body_plain
 - image_urls
 - multiline
 - mentions
 - createdAt
 - room_id
 - room_name
 - organization_slug
 - sender_type
 - sender_id
 - sender_name
 - sender_icon_url

The Intent also contains following extras that describes the bot.

 - id
 - name
 - api_token
 - icon_url

The Intent is broadcasted by robota application.
You can receive the Intent on a BroadcastReceiver with "com.uphyca.robota.action.MESSAGE_CREATED" action.

 - Add permisson "com.uphyca.robota.permission.RECEIVE_MESSAGE_CREATED" to receive the message.
 - Add label to receiver for help message.
 - Add description to receiver for help description.
 - Add intent filter "com.uphyca.robota.action.MESSAGE_CREATED" to receive the message.

AndroidManifest.xml
```XML
<uses-permission android:name="com.uphyca.robota.permission.RECEIVE_MESSAGE_CREATED"/>

<receiver android:name=".ExampleEngine"
          android:label="@string/label_example"
          android:description="@string/description_example">
    <intent-filter>
        <action android:name="com.uphyca.robota.action.MESSAGE_CREATED"/>
    </intent-filter>
</receiver>
```

strings.xml
```XML
<string name="label_example"><![CDATA[hello]]></string>
<string name="description_example"><![CDATA[say HELLO]]></string>
```


The receiver will call setResuleData(String) to responds the message.

```Java
public class ExampleEngine extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String bodyPlain = intent.getStringExtra("com.uphyca.robota.extra.BODY_PLAIN");
        if(bodyPlain.equalsIgnoreCase("hello")) {
            setResultData("HELLO");
        }
    }
}
```


robota-engine
----

robota-engine is a library of bot-engine.
the library provides onMessageReceived() method which delived parsed Intent to Bot and TextMessage objects.
The following example is a echo program, extends EngineBase which is the base class to build your bot engine.

```Java
public class EchoEngine extends EngineBase {

    private static final String EVENT_PATTERN_TEMPLATE = "^[@]?%s[:,]?\\s*(?:echo\\s*((.*)?)$)";

    @Override
    protected String onMessageReceived(Context context, Bot bot, TextMessage textMessage) {
        Pattern pt = Pattern.compile(String.format(EVENT_PATTERN_TEMPLATE, bot.getName()), Pattern.CASE_INSENSITIVE);
        Matcher mt = pt.matcher(textMessage.getText());
        if (!mt.find()) {
            return null;
        }
        return mt.group(1);
    }
}
```


Download
-----

robota-engine releases are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22robota-engine-core%22).

Gradle
```groovy
compile "com.uphyca.robota:robota-engine-core:${robotaVersion}"
```

Maven
```xml
<dependencies>
  <dependency>
    <groupId>com.uphyca.robota</groupId>
    <artifactId>robota-engine-core</artifactId>
    <version>${robota.version}</version>
  </dependency>
</dependencies>
```


License
-------

    Copyright 2014 uPhyca, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

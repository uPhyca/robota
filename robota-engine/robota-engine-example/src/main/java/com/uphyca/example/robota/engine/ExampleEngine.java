package com.uphyca.example.robota.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ExampleEngine extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setResultData("Hello, Idobata");
    }
}

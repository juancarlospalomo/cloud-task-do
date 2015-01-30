package com.productivity.cloudtaskdo.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by JuanCarlos on 30/01/2015.
 * <p/>
 * Triggered when the device is booted.
 * Registered in AndroidManifest.xml
 */
public class BootReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

        }
    }

}

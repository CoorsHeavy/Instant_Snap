package com.hudson.co;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BootReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctxt, Intent i) {
		if (i.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			ctxt.startService(new Intent(ctxt,
					ChatHeadService.class));
		}
	}
}
package com.hudson.co;

import com.hudson.co.BootReciever;

import android.support.v7.app.ActionBarActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("hudson", "activity started");
		Button Activate = (Button) findViewById(R.id.activate);
		Activate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Do something in response to button click
				startService(new Intent(getApplicationContext(),
						ChatHeadService.class));
				ComponentName receiver = new ComponentName(
						getApplicationContext(), BootReciever.class);
				PackageManager pm = getApplicationContext().getPackageManager();
				pm.setComponentEnabledSetting(receiver,
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						PackageManager.DONT_KILL_APP);
			}
		});
		Button Feed = (Button) findViewById(R.id.Feedback);
		Feed.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i("Send email", "");

				String[] TO = { "96hudson@gmail.com" };
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setData(Uri.parse("mailto:"));
				emailIntent.setType("message/rfc822");

				emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT,
						"Question or Complaint About MMLW");
				emailIntent.putExtra(Intent.EXTRA_TEXT,
						"Email message goes here");

				try {
					startActivity(Intent.createChooser(emailIntent,
							"Send mail..."));
					finish();
					Log.i("Finished sending email...", "");
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(MainActivity.this,
							"There is no email client installed.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		Button Deactivate = (Button) findViewById(R.id.Deactivate);
		Deactivate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Do something in response to button click
				Log.d("hudson", "deactivate");
				stopService(new Intent(getApplicationContext(),
						ChatHeadService.class));
				ComponentName receiver = new ComponentName(
						getApplicationContext(), BootReciever.class);
				PackageManager pm = getApplicationContext().getPackageManager();
				pm.setComponentEnabledSetting(receiver,
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
						PackageManager.DONT_KILL_APP);
			}
		});
		Button Help = (Button) findViewById(R.id.Help);
		Help.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Do something in response to button click
				startActivity(new Intent(getApplicationContext(), Help.class));
			}
		});
		Button Settings = (Button) findViewById(R.id.settings);
		Settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Do something in response to button click
				startActivity(new Intent(getApplicationContext(),
						SettingsActivity.class));
			}
		});

	}

}

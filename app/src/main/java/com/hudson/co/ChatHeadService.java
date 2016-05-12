package com.hudson.co;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class ChatHeadService extends Service {

	private WindowManager windowManager;
	private ImageView chatHead;
	private GestureDetector gestureDetector;

	@Override
	public IBinder onBind(Intent intent) {
		// Not used
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		chatHead = new ImageView(this);
		chatHead.setImageResource(R.drawable.icon);
		Point size = new Point();
		windowManager.getDefaultDisplay().getSize(size);
		int width = size.x;
		int height = size.y;
		int d = width / 15;
		if (height > width)
			d = height / 15;
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				d, d, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		gestureDetector = new GestureDetector(this, new SingleTapConfirm());
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		windowManager.addView(chatHead, params);

		chatHead.setOnTouchListener(new View.OnTouchListener() {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					// single tap
					return true;
				} else {
					Point size = new Point();
					windowManager.getDefaultDisplay().getSize(size);
					int width = size.x;
					int height = size.y;
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						initialX = params.x;
						initialY = params.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						return true;
					case MotionEvent.ACTION_UP:

						return true;
					case MotionEvent.ACTION_MOVE:
						params.x = initialX
								+ (int) (event.getRawX() - initialTouchX);
						params.y = initialY
								+ (int) (event.getRawY() - initialTouchY);
						if (event.getRawX() < width / 2)
							params.x = 0;
						if (event.getRawX() > width / 2)
							params.x = width - v.getWidth();
						windowManager.updateViewLayout(chatHead, params);
						return true;
					}
					return false;
				}
			}
		});
		Log.d("hudson", "service started");
	}

	private class SingleTapConfirm extends SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.d("hudson", "clicked");
			Toast.makeText(getApplicationContext(), "Stay On Target",
					Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getApplicationContext(), TakePicture.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			return super.onDoubleTap(e);
			
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onDoubleTapEvent(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onLongPress(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {
			return false;
			
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("hudson", "destroyed");
		if (chatHead != null)
			windowManager.removeView(chatHead);
	}

	String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";
	public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent myIntent) {

			if (myIntent.getAction().equals(BCAST_CONFIGCHANGED)) {

				Log.d("Hudson", "received->" + BCAST_CONFIGCHANGED);
				try {
					Point size = new Point();
					windowManager.getDefaultDisplay().getSize(size);
					int width = size.x;
					int height = size.y;
					int d = width / 15;
					if (height > width)
						d = height / 15;
					final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
							d, d, WindowManager.LayoutParams.TYPE_PHONE,
							WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
							PixelFormat.TRANSLUCENT);
					params.gravity = Gravity.TOP | Gravity.LEFT;
					windowManager.updateViewLayout(chatHead, params);
				} catch (Exception e) {

				}
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					// it's Landscape
					Log.d("Hudson", "LANDSCAPE");
				} else {
					Log.d("Hudson", "PORTRAIT");
				}
			}
		}
	};
}
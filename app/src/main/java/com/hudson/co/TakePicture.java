package com.hudson.co;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TakePicture extends Activity implements SurfaceHolder.Callback {
	// a variable to store a reference to the Image View at the main.xml file
	private ImageView iv_image;
	// a variable to store a reference to the Surface View at the main.xml file
	private SurfaceView sv;

	// a bitmap to display the captured image
	private Bitmap bmp;

	// Camera variables
	// a surface holder
	private SurfaceHolder sHolder;
	// a variable to control the camera
	private Camera mCamera;
	// the camera parameters
	private Parameters parameters;
	PictureCallback mCall;
	private SharedPreferences sharedPref;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		final boolean start = sharedPref.getBoolean("openCamera", true);
		// get the Image View at the main.xml file

		// get the Surface View at the main.xml file
		sv = (SurfaceView) findViewById(R.id.surfaceView);

		// sets what code should be executed after the picture is taken
		mCall = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// decode the data obtained by the camera into a Bitmap
				Log.d("hudson", "piced");
				bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				savePhoto(bmp);
				Toast.makeText(getApplicationContext(), "Picture Taken",
						Toast.LENGTH_SHORT).show();
				Toast ImageToast = new Toast(getBaseContext());
		        LinearLayout toastLayout = new LinearLayout(getBaseContext());
		        toastLayout.setOrientation(LinearLayout.VERTICAL);
		        ImageView image = new ImageView(getBaseContext());
//		        TextView text = new TextView(getBaseContext());
		        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
				Point size = new Point();
				windowManager.getDefaultDisplay().getSize(size);
				int width = size.x;
				int height = size.y;
				image.setAdjustViewBounds(true);
		        image.setMaxHeight(6*height/10);
		        image.setMaxWidth(6*width/10);
		        image.setImageBitmap(bmp);
//		        text.setText("Picture Taken");
		        toastLayout.addView(image);
//		        toastLayout.addView(text);
		        ImageToast.setView(toastLayout);
		        ImageToast.setDuration(Toast.LENGTH_LONG);
		        ImageToast.show();
				if (start) {
					Intent i = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					try {
						PackageManager pm = getApplicationContext()
								.getPackageManager();

						final ResolveInfo mInfo = pm.resolveActivity(i, 0);

						Intent intent = new Intent();
						intent.setComponent(new ComponentName(
								mInfo.activityInfo.packageName,
								mInfo.activityInfo.name));
						intent.setAction(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_LAUNCHER);
						startActivity(intent);
					} catch (Exception e) {
						Log.i("hudson", "Unable to launch camera: " + e);
						Toast.makeText(getApplicationContext(),
								"Unable to launch camera", Toast.LENGTH_SHORT)
								.show();
					}
				}
				finish();
			}
		};

		// Get a surface
		sHolder = sv.getHolder();

		// add the callback interface methods defined below as the Surface View
		// callbacks
		sHolder.addCallback(this);

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// get camera parameters

	}

	/** A safe way to get an instance of the Camera object. */
	public Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			Toast.makeText(getApplicationContext(), "Unable to retrieve ", Toast.LENGTH_SHORT).show();
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw the preview.
		mCamera = getCameraInstance();
		try {
			mCamera.setPreviewDisplay(holder);
			parameters = mCamera.getParameters();
			parameters.setFlashMode(sharedPref.getString("flashMode", "auto"));
			Log.d("hudson", String.valueOf(parameters.getPreviewSize().width));
			Log.d("hudson", String.valueOf(parameters.getPreviewSize().height));
			parameters.setPictureSize(parameters.getPreviewSize().width,
					parameters.getPreviewSize().height);
			if (sharedPref.getBoolean("autofocus", true))
				parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			// set camera parameters
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview();
			mCamera.setParameters(parameters);
			mCamera.enableShutterSound(false);

			mCamera.takePicture(null, null, mCall);
			Log.d("hudson", "created");

		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// stop the preview
		mCamera.stopPreview();
		// release the camera
		mCamera.release();
		// unbind the camera from this object
		mCamera = null;
	}

	String savePhoto(Bitmap bmp) {

		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

		FileOutputStream out = null;
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH))
				+ fromInt(c.get(Calendar.DAY_OF_MONTH))
				+ fromInt(c.get(Calendar.YEAR))
				+ fromInt(c.get(Calendar.HOUR_OF_DAY))
				+ fromInt(c.get(Calendar.MINUTE))
				+ fromInt(c.get(Calendar.SECOND));
		File imageFileName = new File(path, date.toString() + ".jpg"); // imageFileFolder

		try {
			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			scanPhoto(imageFileName.toString());
			out = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageFileName.toString();
	}

	private String fromInt(int val) {
		return String.valueOf(val);
	}

	/*
	 * invoke the system's media scanner to add your photo to the Media
	 * Provider's database, making it available in the Android Gallery
	 * application and to other apps.
	 */
	private void scanPhoto(String imageFileName) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(imageFileName);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		// this.cordova.getContext().sendBroadcast(mediaScanIntent); //this
		// is deprecated
		getApplicationContext().sendBroadcast(mediaScanIntent);
	}
}
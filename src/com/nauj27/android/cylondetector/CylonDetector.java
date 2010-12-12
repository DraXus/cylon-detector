package com.nauj27.android.cylondetector;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class CylonDetector extends Activity {
	
	private Camera camera = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // From http://www.designerandroid.com/?p=73
    	// This is the only way camera preview work on all android devices
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		// No title, no name: Full screen
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		// Set the main layout for the application
		setContentView(R.layout.main);
        
        // Get the surface view where to put the camera images
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.SurfaceViewCamera);
		
		// Get the surface holder and add properties and callback
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setKeepScreenOn(true);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(surfaceCallback);
		
    }
    
	/**
	 * Create the surface call back for the surface holder of the camera.
	 */
	private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		
		@Override
		public void surfaceCreated(SurfaceHolder surfaceHolder) {	
			
			// Obtain an instance of Camera.
			// See http://developer.android.com/reference/android/hardware/Camera.html
			// for more information of the whole  process.
			camera = Camera.open();
			
		}

		@Override
		public void surfaceChanged(
				SurfaceHolder surfaceHolder, 
				int format, 
				int width, 
				int height) {
			
			if (camera != null) {
				Camera.Parameters cameraParameters = camera.getParameters();
				
				// Get the supported preview sizes
				List<Camera.Size> supportedPreviewSizes = cameraParameters.getSupportedPreviewSizes();
				
				// Search for the minimum camera preview size, just check width
				Camera.Size cameraSize = null;
				if (supportedPreviewSizes == null) {
					// Set the camera size of the emulator
					cameraSize = camera.new Size(213, 350);
				} else {
					int previewSizeIndex = 0;
					for (int index = 0; index < supportedPreviewSizes.size(); index++) {
						if (supportedPreviewSizes.get(index).width < supportedPreviewSizes.get(previewSizeIndex).width) {
							previewSizeIndex = index;
						}
					}
					cameraSize = supportedPreviewSizes.get(previewSizeIndex);
				}
				
				cameraParameters.setPreviewSize(cameraSize.width, cameraSize.height);
				cameraParameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
				camera.setParameters(cameraParameters);
				
				try {
					camera.setPreviewDisplay(surfaceHolder);
				} catch (IOException e) {
					// TODO: SHOW TO THE USER THAT CAN'T SHOW CAMERA IMAGE
					// AND RETURN
				}
				
				camera.setPreviewCallback(previewCallback);
				camera.startPreview();
			} else {
				// TODO: SHOW TO THE USER THAT CAN'T OPEN THE CAMERA
			}
		}
		
		@Override
		public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}

	};
	
	
	private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			Face[] faces = new Face[1];
			
			// The following line is not working :(
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			// FIXME: We have to create a Bitmap from data from the camera preview
			
			FaceDetector faceDetector = new FaceDetector(320, 240, 1);
			faceDetector.findFaces(bitmap, faces);
			
		}
		
	};
}

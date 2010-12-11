package com.nauj27.android.cylondetector;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class CylonDetector extends Activity {
	
	private static final int PREVIEW_SIZE_WIDTH = 160;
	private static final int PREVIEW_SIZE_HEIGHT = 120;
	
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
				int previewSizeIndex = 0;
				for (int index = 0; index < supportedPreviewSizes.size(); index++) {
					if (supportedPreviewSizes.get(index).width < supportedPreviewSizes.get(previewSizeIndex).width) {
						previewSizeIndex = index;
					}
				}
				Camera.Size cameraSize = supportedPreviewSizes.get(previewSizeIndex);
				cameraParameters.setPreviewSize(cameraSize.width, cameraSize.height);
				
				// Set auto white balance
				cameraParameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
				
				camera.setParameters(cameraParameters);
				
				try {
					camera.setPreviewDisplay(surfaceHolder);
				} catch (IOException e) {
					// TODO: SHOW TO THE USER THAT CAN'T SHOW CAMERA IMAGE
					// AND RETURN
				}
				
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
}
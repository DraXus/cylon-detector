package com.nauj27.android.cylondetector;

import java.io.IOException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CylonDetector extends Activity {
	
	private static final int PREVIEW_SIZE_WIDTH = 160;
	private static final int PREVIEW_SIZE_HEIGHT = 120;
	
	private Camera camera = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
	 * Create the surface callback for the surface holder of the camera.
	 */
	private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		
		@Override
		public void surfaceCreated(SurfaceHolder surfaceHolder) {		
			camera = Camera.open();
			
			try {
				camera.setPreviewDisplay(surfaceHolder);
			} catch (IOException ioException) {
				if (camera != null) {
					camera.release();
					camera = null;
				}
			}
		}

		@Override
		public void surfaceChanged(
				SurfaceHolder surfaceHolder, 
				int format, 
				int width, 
				int height) {
			
			// http://developer.android.com/reference/android/hardware/Camera.html
			Camera.Parameters cameraParameters = camera.getParameters();
			
			// TODO: GET THE VALID PREVIEW SIZE WIDTH AND HEIGHT FOR THE CAMERA
			
			cameraParameters.setPreviewSize(PREVIEW_SIZE_WIDTH, PREVIEW_SIZE_HEIGHT);
			cameraParameters.setPictureFormat(PixelFormat.JPEG);
			
			camera.setParameters(cameraParameters);
			camera.startPreview();
		}
		
		@Override
		public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}

	};
}
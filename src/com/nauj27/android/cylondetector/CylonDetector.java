package com.nauj27.android.cylondetector;

import java.io.IOException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CylonDetector extends Activity {
	
	private static final int PREVIEW_SIZE_WIDTH = 352;
	private static final int PREVIEW_SIZE_HEIGHT = 288;
	
	private Camera camera = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.SurfaceViewCamera);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceCallback);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
	/**
	 * Create the surface callback for 
	 */
	private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}

		@Override
		public void surfaceChanged(
				SurfaceHolder surfaceHolder, int format, int width, int height) {
			
			Camera.Parameters cameraParameters = camera.getParameters();
			
			cameraParameters.setPreviewSize(PREVIEW_SIZE_WIDTH, PREVIEW_SIZE_HEIGHT);
			cameraParameters.setPictureFormat(PixelFormat.JPEG);
			
			camera.setParameters(cameraParameters);
			camera.startPreview();
		}

		@Override
		public void surfaceCreated(SurfaceHolder surfaceHolder) {		
			camera = Camera.open();
			
			try {
				camera.setPreviewDisplay(surfaceHolder);
			} catch (IOException ioException) {
				//Log.e(TAG, "Error setting preview display");
				camera.release();
				camera = null;
			}
		}
	};
}
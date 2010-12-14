package com.nauj27.android.cylondetector;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class CylonDetector extends Activity {
	
	private Camera camera = null;
	private ImageView imageViewDebug = null;
	
	private int previewCameraWidth = 0;
	private int previewCameraHeight = 0;
	
	private static final int PREVIEW_CAMERA_EMULATOR_WIDTH = 350;
	private static final int PREVIEW_CAMERA_EMULATOR_HEIGHT = 213;
	
	private static final int FACES_NUMBER_TO_SEARCH = 1;
	
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
		
		// debug purposes
		imageViewDebug = (ImageView)findViewById(R.id.ImageViewDebug);
		
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
				List<Camera.Size> supportedPreviewSizes = cameraParameters.getSupportedPreviewSizes();
				
				
				Camera.Size cameraSize = null;
				
				if (supportedPreviewSizes == null) {
				
					// There is a but in the emulator for 2.1 SDK
					// According to the doc, getSupportedPreviewSizes does 
					// ALWAYS returns a List with at least one element but
					// on the emulator 2.1 SDK version it returns null.
					cameraSize = camera.new Size(PREVIEW_CAMERA_EMULATOR_HEIGHT, PREVIEW_CAMERA_EMULATOR_WIDTH);
				
				} else {
					
					// Search for the minimum camera preview size, just check width
					int previewSizeIndex = 0;
					for (int index = 0; index < supportedPreviewSizes.size(); index++) {
						if (supportedPreviewSizes.get(index).width < supportedPreviewSizes.get(previewSizeIndex).width) {
							previewSizeIndex = index;
						}
					}
					cameraSize = supportedPreviewSizes.get(previewSizeIndex);
					
				}
				
				// Save values for later use
				previewCameraWidth = cameraSize.width;
				previewCameraHeight = cameraSize.height;
				
				cameraParameters.setPreviewSize(previewCameraWidth, previewCameraHeight);
				cameraParameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
				camera.setParameters(cameraParameters);
				
				try {
					camera.setPreviewDisplay(surfaceHolder);
				} catch (IOException e) {
					// TODO: SHOW TO THE USER THAT CAN'T SHOW CAMERA IMAGE
					// AND RETURN
				}
				
				camera.startPreview();
				camera.setPreviewCallback(previewCallback);
			} else {
				// TODO: SHOW TO THE USER THAT CAN'T OPEN THE CAMERA
			}
		}
		
		@Override
		public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
			camera.setPreviewCallback(null); // Stop receiving call backs.
			camera.stopPreview();
			camera.release();
			camera = null;
		}

	};
	
	
	private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			Face[] faces = new Face[FACES_NUMBER_TO_SEARCH];
			int facesNumberFound = 0;
			
			Bitmap bitmap = Utils.getBitmapFromNV21(
					data, 
					previewCameraWidth, 
					previewCameraHeight,
					Bitmap.Config.RGB_565);
			
			imageViewDebug.setImageBitmap(bitmap);
			
			FaceDetector faceDetector = new FaceDetector(
					previewCameraWidth, 
					previewCameraHeight, 
					FACES_NUMBER_TO_SEARCH);
			
			facesNumberFound = faceDetector.findFaces(bitmap, faces);
			
			if (facesNumberFound > 0) {
				// TODO: GET PHOTO AND "ANALIZE" TO SEARCH FOR CYLON BEING
				camera.autoFocus(null);
				Log.d("FaceDetector", "Face found");
			}
			
		}
		
	};
}

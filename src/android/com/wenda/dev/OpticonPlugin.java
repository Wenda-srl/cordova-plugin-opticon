package com.wenda.dev;

import com.extbcr.scannersdk.BarcodeManager;
import com.extbcr.scannersdk.EventListener;
import com.extbcr.scannersdk.CodeID;
import com.extbcr.scannersdk.BarcodeData;
// import com.extbcr.scannersdk.BarcodeDataEx;
import com.extbcr.scannersdk.AllSettings;
import com.extbcr.scannersdk.PropertyID;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.media.AudioManager;
import android.os.Handler;
import android.webkit.WebView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import android.content.res.Configuration;
import android.content.Context;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpticonPlugin extends CordovaPlugin {
	private static final String TAG = "ScannerSDK - MainActivity:";

	private BarcodeManager mBarcodeManager;
	private EventListener mEventListener;

	private CallbackContext callbackContext;
	private CallbackContext myCallBack = null;

	private boolean ignoreStop = false;
	int i=0;
	private boolean triggermode = false;
	private boolean serverconnect = false;
	private boolean initialized = false;
	Handler mHandler;
	Runnable mRunnable;

	private void initScanner(CallbackContext callbackContext) {
		try {
			Context context = this.cordova.getActivity().getApplicationContext();
			Log.i(TAG, "INIT SCAN");
			mBarcodeManager = new BarcodeManager(context);
			mBarcodeManager.init();

			mEventListener = new EventListener() {

				@Override
				public void onReadData(BarcodeData result) {
					Log.e(TAG, "ON READ DATA: " + result.getText() + ", code id is " + result.getCodeID());

					// plugin result object constuction
					/*
					JSONObject event = new JSONObject();
					event.put("event", "onReadData");
					event.put("data", result.getText());
					event.put("id", result.getCodeID());
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, event);
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
					*/
					
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onReadData\", \"data\": \"" + result.getText() + "\", \"id\": \"" + result.getCodeID() + "\"}");
					pluginResult.setKeepCallback(true);
					if (myCallBack != null) {
						Log.i(TAG, ">>> myCallBack <<<");
						myCallBack.sendPluginResult(pluginResult);
						myCallBack = null;
					}
					else {
						callbackContext.sendPluginResult(pluginResult);
					}
					ignoreStop = true;
				}

				@Override
				public void onTimeout() {
					Log.i(TAG, "ON TIMEOUT");
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onTimeout\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
					ignoreStop = true;
				}

				@Override
				public void onConnect(){
					Log.i(TAG, "ON CONNECT");
					serverconnect = true;
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onConnect\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
				}

				@Override
				public void onDisconnect(){
					Log.i(TAG, "ON DISCONNECT");
					serverconnect = false;
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onDisconnect\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
				}

				@Override
				public void onStart(){
					Log.i(TAG, "ON START");
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onStart\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
				}

				@Override
				public void onStop(){
					Log.i(TAG, "ON STOP");
					/*
					if (myCallBack != null) {
						// send timeout error for startDecode
						Log.i(TAG, ">>> myCallBack <<<");
						PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "TIMEOUT");
						pluginResult.setKeepCallback(true);
						myCallBack.sendPluginResult(pluginResult);
						myCallBack = null;
					}
					else {
					*/
						PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onStop\"}");
						pluginResult.setKeepCallback(true);
						callbackContext.sendPluginResult(pluginResult);
					//}
				}

				@Override
				public void onImgBuffer(byte[] imgdata, int type){
					Log.i(TAG, "ON IMG BUFFER type=" + type + " Image Size=" + imgdata.length);
					// Bitmap bmp = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length);

					try {
						// Saving image to app directory (subfolder "uploaded") with passed imageName
						/*
						String extension = (type == 1) ? ".jpg" : ".bmp";
						File path = new File(cordova.getActivity().getApplicationContext().getFilesDir(), "uploaded");
						File img = new File(path, imageName + extension);
						path.mkdirs();
						OutputStream os = new FileOutputStream(img);
						os.write(imgdata);
						os.close();
						Log.i(TAG, "onImgBuffer image " + imageName + extension + " saved in " + path.toString());
						*/

						// PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onImgBuffer\", \"data\": \"" + Base64.encodeToString(imgdata, Base64.DEFAULT) + "\"}");
						PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, imgdata);
						pluginResult.setKeepCallback(true);
						if (myCallBack != null) {
							Log.i(TAG, ">>> myCallBack <<<");
							myCallBack.sendPluginResult(pluginResult);
							myCallBack = null;
						}
						else {
							callbackContext.sendPluginResult(pluginResult);
						}
					} catch (Exception e) {
						Log.e(TAG, "ON IMG BUFFER ERROR", e);
						PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "{\"event\": \"onImgBuffer\", \"data\": " + e + "}");
						pluginResult.setKeepCallback(true);
						if (myCallBack != null) {
							myCallBack.sendPluginResult(pluginResult);
							myCallBack = null;
						}
						else {
							callbackContext.sendPluginResult(pluginResult);
						}
					}

					// javascript code injection
					/*
					String event_data = String.format("javascript:cordova.fireDocumentEvent('imgbuffer', { 'picture': '%s' });", Base64.encodeToString(imgdata, Base64.DEFAULT));
					cordova.getActivity().runOnUiThread(new Runnable() {
						    @Override
						    public void run() {
							webView.loadUrl(event_data);
						    }
						});
					*/
				}
			};

			mBarcodeManager.addListener(mEventListener);
			initialized = true;
			callbackContext.success("Scanner initialized!");
		}
		catch (Exception ex) {
        		callbackContext.error("Something went wrong with initScanner: " + ex);
	        }
	}

	private void deinitScanner(CallbackContext callbackContext) {
		try {
			mBarcodeManager.removeListener();
			mBarcodeManager.deinit();
			initialized = false;
			serverconnect = false;
			callbackContext.success("Scanner deinitialized.");
		}
		catch (Exception ex) {
			callbackContext.error("Something went wrong with deinitScanner: " + ex);
        	}
	}
	
	private void takeSnapshot(CallbackContext callbackContext) {
		if (!initialized) {
			callbackContext.error("Scanner not initialized; call initScanner first");
		}
		else {
			try {
				if (serverconnect) {
					// void takeSnapshot(int subSampling, int bitPerPixel, int imageType, int jpegQuality);
					// Parameters
					// int subSampling: 1, 2, 4, 8
					// int bitPerPixel: 1, 4, 8, 10
					// int imageType: Jpeg(1), Bmp(3) int jpegQuality: 5~100 (%)
					mBarcodeManager.takeSnapshot(1, 8, 1, 100);

					// callbackContext.success("Snapshot taken");
					// save context for onImgBuffer result
					myCallBack = callbackContext;
				}
				else {
					callbackContext.error("Scanner disconnected");
				}
			} catch (Exception e) {
				callbackContext.error("Something went wrong with takeSnapshot: " + e);
			}
		}
	}
	
	private void startDecode(CallbackContext callbackContext) {
		if (!initialized) {
			callbackContext.error("Scanner not initialized; call initScanner first");
		}
		else {
			try {
				if (serverconnect) {
					mBarcodeManager.startDecode();
					// callbackContext.success("Decode started");
					// save context for onReadData/onStop result
					myCallBack = callbackContext;
				}
				else {
					callbackContext.error("Scanner disconnected");
				}
			} catch (Exception e) {
            			callbackContext.error("Something went wrong with startDecode: " + e);
			}
		}
	}
	
	private void stopDecode(CallbackContext callbackContext) {
		if (!initialized) {
			callbackContext.error("Scanner not initialized; call initScanner first");
		}
		else {
			try {
				if (serverconnect) {					
					mBarcodeManager.stopDecode();
					callbackContext.success("Decode stopped");
				}
				else {
					callbackContext.error("Scanner disconnected");
				}
			} catch (Exception e) {
            			callbackContext.error("Something went wrong with stopDecode: " + e);
			}
		}
	}
	
	private void startTrigger(CallbackContext callbackContext) {
		if (!initialized) {
			callbackContext.error("Scanner not initialized; call initScanner first");
		}
		else {
			try {
				if (serverconnect) {
					mBarcodeManager.startTrigger();
					callbackContext.success("Trigger started");
				}
				else {
					callbackContext.error("Scanner disconnected");
				}
			} catch (Exception e) {
            			callbackContext.error("Something went wrong with startTrigger: " + e);
			}
		}
	}
	
	private void stopTrigger(CallbackContext callbackContext) {
		if (!initialized) {
			callbackContext.error("Scanner not initialized; call initScanner first");
		}
		else {
			try {
				if (serverconnect) {					
					mBarcodeManager.stopTrigger();
					callbackContext.success("Trigger stopped");
				}
				else {
					callbackContext.error("Scanner disconnected");
				}
			} catch (Exception e) {
		            	callbackContext.error("Something went wrong with stopTrigger: " + e);
			}
		}
	}

	private void isConnected(CallbackContext callbackContext) {
		try {
			if (serverconnect) {
				callbackContext.success(1);
            		}
			else {
				callbackContext.success(0);
			}
		}
		catch (Exception e) {
			callbackContext.error("Something went wrong with isConnected: " + e);
		}
	}

	private void echo(String message, CallbackContext callbackContext) {
		/*
		this.cordova.getActivity().runOnUiThread(new Runnable() {
			    @Override
			    public void run() {
				webView.loadUrl("javascript:console.log('----------------------- ECHO FIRED -----------------------');");
			    }
			});
		*/
		if (message != null && message.length() > 0) {
			try {
				callbackContext.success("Hello, " + message);
			    }
			    catch (Exception e) {
				callbackContext.error("Something went wrong: " + e);
			    }
		} 
		else {
			callbackContext.error("Expected one non-empty string argument.");
		}
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		if (action.equals("echo")) {
			Log.e(TAG, "echo called");
			String message = args.getString(0);
			this.echo(message, callbackContext);
			return true;
		}
		if (action.equals("initOpticon")) {
			Log.e(TAG, "Initialised opticon handscanner API");
			this.initScanner(callbackContext);
			return true;
		}
		if (action.equals("deinitOpticon")) {
			Log.e(TAG, "Deinitialised opticon handscanner API");
			this.deinitScanner(callbackContext);
			return true;
		}
		if (action.equals("takeSnapshot")) {
			Log.e(TAG, "takeSnapshot called");
			this.takeSnapshot(callbackContext);
			return true;
		}
		if (action.equals("startDecode")) {
			Log.e(TAG, "startDecode called");
			this.startDecode(callbackContext);
			return true;
		}
		if (action.equals("stopDecode")) {
			Log.e(TAG, "stopDecode called");
			this.stopDecode(callbackContext);
			return true;
		}
		if (action.equals("startTrigger")) {
			Log.e(TAG, "startTrigger called");
			this.startTrigger(callbackContext);
			return true;
		}
		if (action.equals("stopTrigger")) {
			Log.e(TAG, "stopTrigger called");
			this.stopTrigger(callbackContext);
			return true;
		}
		if (action.equals("isConnected")) {
			Log.e(TAG, "isConnected called");
			this.isConnected(callbackContext);
			return true;
		}
		return false;
	}
}

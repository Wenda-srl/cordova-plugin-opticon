package com.wenda.dev;

import com.extbcr.scannersdk.BarcodeManager;
import com.extbcr.scannersdk.EventListener;
import com.extbcr.scannersdk.CodeID;
import com.extbcr.scannersdk.BarcodeData;
import com.extbcr.scannersdk.BarcodeDataEx;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpticonPlugin extends CordovaPlugin {
	private static final String TAG = "ScannerSDK-MainActivity";

	private BarcodeManager mBarcodeManager;
	private EventListener mEventListener;

	private CallbackContext callbackContext;

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
			Log.e(TAG, "initScan: XXX");
			mBarcodeManager = new BarcodeManager(context);
			mBarcodeManager.init();

			mEventListener = new EventListener() {

				@Override
				public void onReadData(BarcodeData result) {
					Log.e(TAG, "onReadData " + result.getText() + ", codeid is" + result.getCodeID());
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onReadData\", \"data\": \"" + result.getText() + "\", \"id\": \"" + result.getCodeID() + "\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
					ignoreStop = true;
				}

				@Override
				public void onTimeout() {
					Log.e(TAG, "onTimeout");
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onTimeout\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
					ignoreStop = true;
				}

				@Override
				public void onConnect(){
					Log.e(TAG, "onConnect");
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onConnect\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
					serverconnect = true;

				}

				@Override
				public void onDisconnect(){
					Log.e(TAG, "onDisconnect");
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onDisconnect\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
					serverconnect = false;
				}

				@Override
				public void onStart(){
					Log.e(TAG, "onStart");
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onStart\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
				}

				@Override
				public void onStop(){
					Log.e(TAG, "onStop");
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onStop\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
				}

				@Override
				public void onImgBuffer(byte[] imgdata, int type){
					Log.e(TAG, "onImgBuffer type=" + type + " imagesize=" + imgdata.length);

					// Bitmap bmp = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length);
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "{\"event\": \"onImgBuffer\", \"data\": \"" + Base64.encodeToString(imgdata, Base64.DEFAULT) + "\"}");
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
				}
			};

			mBarcodeManager.addListener(mEventListener);
			initialized = true;
			callbackContext.success("Scanner initialized!");
		}
		catch (Exception ex) {
            callbackContext.error("Something went wrong: " + ex);
        }
	}

	private void takeSnapshot(CallbackContext callbackContext) {
		if (!initialized) {
			callbackContext.error("Scanner not initialized; call initScanner first");
		}
		else {
			try {
				if (serverconnect) {
					isPreviewMode = false;
					isPreviewStart = false;
					
					mBarcodeManager.takeSnapshot(1, 8, 1, 100);
					// void takeSnapshot(int subSampling, int bitPerPixel, int imageType, int jpegQuality);
					// Parameters
					// int subSampling: 1, 2, 4, 8
					// int bitPerPixel: 1, 4, 8, 10
					// int imageType: Jpeg(1), Bmp(3) int jpegQuality: 5~100 (%)
					callbackContext.success("Snapshot taken");
				}
			} catch (Exception e) {
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
					isPreviewMode = false;
					isPreviewStart = false;
					
					mBarcodeManager.startDecode();
					callbackContext.success("Decode started");
				}
			} catch (Exception e) {
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
			} catch (Exception e) {
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
					isPreviewMode = false;
					isPreviewStart = false;	
					mBarcodeManager.startTrigger();
					callbackContext.success("Trigger started");
				}
			} catch (Exception e) {
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
			} catch (Exception e) {
			}
		}
	}

	private void echo(String message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) {
			try {
				callbackContext.success("Ciao, " + message);
            }
            catch (Exception ex) {
                callbackContext.error("Something went wrong: " + ex);
            }
		} 
		else {
			callbackContext.error("Expected one non-empty string argument.");
		}
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		// this.callbackContext = callbackContext;
		if (action.equals("echo")) {
			String message = args.getString(0);
			this.echo(message, callbackContext);
			return true;
		}
		if (action.equals("initOpticon")) {
			this.initScanner(callbackContext);
			Log.e(TAG, "Initialised opticon handscanner API");
			return true;
		}
		if (action.equals("takeSnapshot")) {
			this.takeSnapshot(callbackContext);
			Log.e(TAG, "takeSnapshot called");
			return true;
		}
		if (action.equals("startDecode")) {
			this.startDecode(callbackContext);
			Log.e(TAG, "startDecode called");
			return true;
		}
		if (action.equals("stopDecode")) {
			this.stopDecode(callbackContext);
			Log.e(TAG, "stopDecode called");
			return true;
		}
		if (action.equals("startTrigger")) {
			this.startTrigger(callbackContext);
			Log.e(TAG, "startTrigger called");
			return true;
		}
		if (action.equals("stopTrigger")) {
			this.stopTrigger(callbackContext);
			Log.e(TAG, "stopTrigger called");
			return true;
		}
		return false;
	}
}
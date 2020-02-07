package com.magicleap.magicscript.plane;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.magicleap.magicscript.scene.UiNodesManager;

public class ARPlaneDetector extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final ARPlaneDetectorEventsManager eventsManager;
    private final ARPlaneDetectorBridge bridge;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public ARPlaneDetector(ReactApplicationContext reactContext, ARPlaneDetectorEventsManager arEventsManager, ARPlaneDetectorBridge bridge) {
        super(reactContext);
        this.eventsManager = arEventsManager;
        this.bridge = bridge;
    }

    @ReactMethod
    public void startDetecting(final ReadableMap configuration) {
        bridge.startDetecting(configuration);
    }

    @ReactMethod
    public void stopDetecting() {
        bridge.stopDetecting();
    }

    @ReactMethod
    public void addOnPlaneUpdatedEventHandler() {
        mainHandler.post(() -> this.bridge.setOnPlanesUpdatedListener(eventsManager::onPlaneUpdatedEventReceived));
    }

    @ReactMethod
    public void addOnPlaneDetectedEventHandler() {
        mainHandler.post(() -> this.bridge.setOnPlanesAddedListener(eventsManager::onPlaneDetectedEventReceived));
    }

    @ReactMethod
    public void addOnPlaneRemovedEventHandler() {
        mainHandler.post(() -> this.bridge.setOnPlanesRemovedListener(eventsManager::onPlaneRemovedEventReceived));
    }

    @ReactMethod
    public void addOnPlaneTappedEventHandler() {
        mainHandler.post(() -> this.bridge.setOnPlaneTappedListener(eventsManager::onPlaneTappedListener));
    }

    @ReactMethod
    public void getAllPlanes(final ReadableMap configuration, final Callback callback) {
        mainHandler.post(() -> this.bridge.getAllPlanes(configuration, callback));
    }

    @ReactMethod
    public void requestPlaneCast(final ReadableMap configuration, final Callback callback) {
        WritableMap error = new WritableNativeMap();
        error.putString("error", "Not implemented yet");
        mainHandler.post(() -> callback.invoke(error, null));
    }

    @Override
    public String getName() {
        return "ARPlaneDetector";
    }

    @Override
    public void onHostResume() {
        //no-op
    }

    @Override
    public void onHostPause() {
        //no-op
    }

    @Override
    public void onHostDestroy() {
        bridge.destroy();
    }
}

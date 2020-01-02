/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.google.ar.sceneform.Scene;
import com.magicleap.magicscript.scene.CustomArFragment;
import com.magicleap.magicscript.scene.UiNodesManager;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

/**
 * View manager that is responsible for creating the AR Fragment
 */
public class ArViewManager extends ViewGroupManager<FrameLayout> {

    private static final String REACT_CLASS = "RCTARView";
    private static final String LOG_TAG = "AR_LOG";
    private static WeakReference<AppCompatActivity> activityRef = new WeakReference<>(null);
    public static Boolean showLayoutBounds = false;

    public static WeakReference<AppCompatActivity> getActivityRef() {
        return activityRef;
    }

    @NotNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NotNull
    @Override
    protected FrameLayout createViewInstance(@NotNull final ThemedReactContext reactContext) {
        // view that contains AR fragment
        FrameLayout mContainer = new FrameLayout(reactContext);
        CustomArFragment fragment = new CustomArFragment();
        AppCompatActivity currentActivity = (AppCompatActivity) reactContext.getCurrentActivity();
        activityRef = new WeakReference<>(currentActivity);
        if (currentActivity != null) {
            currentActivity.getSupportFragmentManager().beginTransaction().add(fragment, "arFragment").commitNow();
            addView(mContainer, fragment.getView(), 0); // same as mCointainer.addView
            Scene scene = fragment.getArSceneView().getScene();
            UiNodesManager.Companion.getINSTANCE().registerScene(scene);
        } else {
            Log.e(LOG_TAG, "createViewInstance: activity is null");
        }
        return mContainer;
    }

    @ReactProp(name = "planeDetection")
    public void setPlaneDetection(View view, @Nullable Boolean planeDetection) {
        UiNodesManager.Companion.getINSTANCE().setPlaneDetection(true);
    }

    @ReactProp(name = "showLayoutBounds")
    public void setShowLayoutBounds(View view, @Nullable Boolean showLayoutBounds) {
        ArViewManager.showLayoutBounds = showLayoutBounds;
    }

    @Override
    public boolean needsCustomLayoutForChildren() {
        return true;
    }

}

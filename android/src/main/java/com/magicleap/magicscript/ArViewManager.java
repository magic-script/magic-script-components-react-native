/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.magicleap.magicscript.ar.ArResourcesManager;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

/**
 * View manager that is responsible for creating the AR Fragment
 */
public class ArViewManager extends ViewGroupManager<ArFragmentContainer> {

    public static Boolean showLayoutBounds = false;

    public static WeakReference<Activity> getActivityRef() {
        return activityRef;
    }

    private static final String REACT_CLASS = "RCTARView";
    private static final String LOG_TAG = "AR_LOG";
    private static WeakReference<Activity> activityRef = new WeakReference<>(null);
    private static String FRAGMENT_TAG = "arFragment";

    @NotNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NotNull
    @Override
    protected ArFragmentContainer createViewInstance(@NotNull final ThemedReactContext reactContext) {
        // view that contains AR fragment
        ArFragmentContainer container = new ArFragmentContainer(reactContext);
        AppCompatActivity currentActivity = (AppCompatActivity) reactContext.getCurrentActivity();
        activityRef = new WeakReference<>(currentActivity);
        if (currentActivity != null) {
            FragmentManager fragmentManager = currentActivity.getSupportFragmentManager();
            container.setupFragment(FRAGMENT_TAG, fragmentManager);
        } else {
            Log.e(LOG_TAG, "createViewInstance: activity is null");
        }
        return container;
    }

    /**
     * Called when the view is no longer rendered in React's render() function
     * <p>
     * This is for some reason called with some delay, so e.g. when the user navigates
     * back from ARView screen to another screen that uses camera, the camera may not be
     * released on time (adding some delay in React before displaying the first screen
     * may be necessary)
     */
    @Override
    public void onDropViewInstance(@Nonnull final ArFragmentContainer view) {
        super.onDropViewInstance(view);
        view.dropFragment(FRAGMENT_TAG);
    }

    @ReactProp(name = "planeDetection")
    public void setPlaneDetection(View view, boolean planeDetection) {
        ArResourcesManager arResourcesManager = ArResourcesManager.Companion.getINSTANCE();
        if (arResourcesManager != null) {
            arResourcesManager.setPlaneDetection(planeDetection);
        }
    }

    @ReactProp(name = "showLayoutBounds")
    public void setShowLayoutBounds(View view, boolean showLayoutBounds) {
        ArViewManager.showLayoutBounds = showLayoutBounds;
    }

    @Override
    public boolean needsCustomLayoutForChildren() {
        return true;
    }

}

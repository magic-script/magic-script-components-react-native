package com.reactlibrary;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.google.ar.sceneform.Scene;
import com.reactlibrary.scene.CustomArFragment;
import com.reactlibrary.scene.UiNodesManager;

import java.lang.ref.WeakReference;

/**
 * View manager that is responsible for creating the AR Fragment
 */
public class ArViewManager extends ViewGroupManager<FrameLayout> {

    private static final String REACT_CLASS = "RCTARView";
    private static WeakReference<AppCompatActivity> activityRef;

    static void initActivity(final AppCompatActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    public static WeakReference<AppCompatActivity> getActivityRef() {
        return activityRef;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected FrameLayout createViewInstance(final ThemedReactContext reactContext) {
        // view that contains AR fragment
        Log.d("ArViewManager", "createViewInstance");
        FrameLayout mContainer = new FrameLayout(reactContext);
        CustomArFragment fragment = new CustomArFragment(); // new ArFragment();
        AppCompatActivity activity = activityRef.get();
        if (activity != null) {
            activity.getSupportFragmentManager().beginTransaction().add(fragment, "arFragment").commitNow();
            addView(mContainer, fragment.getView(), 0); // same as mCointainer.addView
            Scene scene = fragment.getArSceneView().getScene();
            UiNodesManager.registerScene(scene);
        }

        return mContainer;
    }

    @Override
    public boolean needsCustomLayoutForChildren() {
        // TODO check if this is required
        return true;
    }

}

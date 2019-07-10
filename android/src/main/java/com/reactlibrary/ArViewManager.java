package com.reactlibrary;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
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
    private static WeakReference<FrameLayout> containerRef = new WeakReference<>(null);

    static void initActivity(final AppCompatActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    public static WeakReference<AppCompatActivity> getActivityRef() {
        return activityRef;
    }

    public static void addViewToContainer(View view) {
        FrameLayout container = containerRef.get();
        if (container != null) {
            container.addView(view);
            container.requestLayout();
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected FrameLayout createViewInstance(final ThemedReactContext reactContext) {
        // view that contains AR fragment
        Log.d("ArViewManager", "createViewInstance");
        FrameLayout mContainer = new DynamicContainer(reactContext);
        containerRef = new WeakReference<>(mContainer);
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

    // for tests
    @ReactProp(name = "text")
    public void setText(FrameLayout view, @Nullable String text) {
        // view.setText(text);
    }

}

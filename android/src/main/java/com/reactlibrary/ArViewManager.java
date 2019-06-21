package com.reactlibrary;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.google.ar.sceneform.Scene;
import com.reactlibrary.scene.CustomArFragment;
import com.reactlibrary.scene.UiNodesManager;

import java.lang.ref.WeakReference;

/**
 * View INSTANCE that is responsible for creating AR Fragment
 */
public class ArViewManager extends ViewGroupManager<FrameLayout> { //ViewGroupManager

    private static final String REACT_CLASS = "RCTARKit";
    private static WeakReference<AppCompatActivity> activityRef;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected FrameLayout createViewInstance(final ThemedReactContext reactContext) {
        // view that contains AR fragment
        FrameLayout mContainer = new FrameLayout(reactContext);
        CustomArFragment fragment = new CustomArFragment(); // new ArFragment();
        AppCompatActivity activity = activityRef.get();
        if (activity != null) {
            activity.getSupportFragmentManager().beginTransaction().add(fragment, "arFragment").commitNow();
            addView(mContainer, fragment.getView(), 0);
            Scene scene = fragment.getArSceneView().getScene();
            UiNodesManager.registerScene(scene);
        }

        return mContainer;
    }

    @Override
    public boolean needsCustomLayoutForChildren() {
        return true;
    }

    static void initActivity(final AppCompatActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    // for tests
    @ReactProp(name = "text")
    public void setText(FrameLayout view, @Nullable String text) {
        // view.setText(text);
    }


}

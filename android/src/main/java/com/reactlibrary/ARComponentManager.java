/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactlibrary;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.ar.sceneform.Node;
import com.reactlibrary.scene.UiNodesManager;
import com.reactlibrary.scene.nodes.GroupNode;
import com.reactlibrary.scene.nodes.LineNode;
import com.reactlibrary.scene.nodes.ModelNode;
import com.reactlibrary.scene.nodes.UiButtonNode;
import com.reactlibrary.scene.nodes.UiImageNode;
import com.reactlibrary.scene.nodes.UiProgressBarNode;
import com.reactlibrary.scene.nodes.UiScrollBarNode;
import com.reactlibrary.scene.nodes.UiScrollViewNode;
import com.reactlibrary.scene.nodes.UiSpinnerNode;
import com.reactlibrary.scene.nodes.UiTextEditNode;
import com.reactlibrary.scene.nodes.UiTextNode;
import com.reactlibrary.scene.nodes.UiToggleNode;
import com.reactlibrary.scene.nodes.base.TransformNode;
import com.reactlibrary.scene.nodes.base.UiNode;
import com.reactlibrary.scene.nodes.layouts.UiGridLayout;
import com.reactlibrary.scene.nodes.layouts.UiLinearLayout;
import com.reactlibrary.scene.nodes.video.MediaPlayerPool;
import com.reactlibrary.scene.nodes.video.VideoNode;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

import kotlin.Unit;

/**
 * Android module that is responsible for "parsing" JS tags in order to generate AR Nodes
 * Based on: https://facebook.github.io/react-native/docs/native-modules-android
 * <p>
 * Node creation methods are called from
 * react-native-magic-script/components/platform/platform-factory.js
 */
@SuppressWarnings("unused")
public class ARComponentManager extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final String COMPONENT_NAME = "ARComponentManager";

    // Supported events names
    private static final String EVENT_CLICK = "onClick";
    private static final String EVENT_PRESS = "onPress";
    private static final String EVENT_TEXT_CHANGED = "onTextChanged";
    private static final String EVENT_TOGGLE_CHANGED = "onToggleChanged";

    // Supported events arguments
    private static final String EVENT_ARG_NODE_ID = "nodeId";
    private static final String EVENT_ARG_TEXT = "text";
    private static final String EVENT_ARG_TOGGLE_ACTIVE = "On";

    // All code inside react method must be called from main thread
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ReactApplicationContext context;

    public ARComponentManager(ReactApplicationContext reactContext) {
        super(reactContext);
        // here activity is null yet (so we use initAR method)
        this.context = reactContext;
        context.addLifecycleEventListener(this);
    }

    @NotNull
    @Override
    public String getName() {
        return COMPONENT_NAME;
    }

    @Override
    public Map<String, Object> getConstants() {
        return Collections.emptyMap();
    }

    /**
     * Creates node that is a parent for other nodes (<view>)
     *
     * @param props  properties (e.g. localPosition)
     * @param nodeId id of the node
     */
    @ReactMethod
    public void createGroupNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new GroupNode(props), nodeId));
    }

    /**
     * Creates a button
     *
     * @param props  properties (e.g. localPosition)
     * @param nodeId id of the node
     */
    @ReactMethod
    public void createButtonNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiButtonNode(props, context), nodeId));
    }

    @ReactMethod
    public void createImageNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiImageNode(props, context), nodeId));
    }

    @ReactMethod
    public void createTextNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiTextNode(props, context), nodeId));
    }

    @ReactMethod
    public void createTextEditNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiTextEditNode(props, context), nodeId));
    }

    @ReactMethod
    public void createModelNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new ModelNode(props, context), nodeId));
    }

    @ReactMethod
    public void createVideoNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new VideoNode(props, context), nodeId));
    }

    @ReactMethod
    public void createScrollBarNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiScrollBarNode(props, context), nodeId));
    }

    @ReactMethod
    public void createScrollViewNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiScrollViewNode(props, context), nodeId));
    }

    @ReactMethod
    public void createSpinnerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiSpinnerNode(props, context), nodeId));
    }

    @ReactMethod
    public void createToggleNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiToggleNode(props, context), nodeId));
    }

    @ReactMethod
    public void createProgressBarNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiProgressBarNode(props, context), nodeId));
    }

    @ReactMethod
    public void createLineNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new LineNode(props, context), nodeId));
    }

    @ReactMethod
    public void createGridLayoutNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiGridLayout(props), nodeId));
    }

    @ReactMethod
    public void createLinearLayoutNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiLinearLayout(props), nodeId));
    }

    @ReactMethod
    public void addChildNode(final String nodeId, final String parentId) {
        mainHandler.post(() -> UiNodesManager.addNodeToParent(nodeId, parentId));
    }

    @ReactMethod
    public void addChildNodeToContainer(final String nodeId) {
        mainHandler.post(() -> UiNodesManager.addNodeToRoot(nodeId));
    }

    @ReactMethod
    public void removeChildNode(final String nodeId, final String parentId) {
        mainHandler.post(() -> UiNodesManager.removeNode(nodeId));
    }

    @ReactMethod
    public void removeChildNodeFromRoot(final String nodeId) {
        mainHandler.post(() -> UiNodesManager.removeNode(nodeId));
    }

    @ReactMethod
    public void updateNode(final String nodeId, final ReadableMap properties) {
        mainHandler.post(() -> UiNodesManager.updateNode(nodeId, properties));
    }

    @ReactMethod
    public void clearScene() {
        mainHandler.post(() -> UiNodesManager.clear());
    }

    @ReactMethod
    public void addOnPressEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiNode) {
                ((UiNode) node).setClickListener(() -> {
                    WritableMap pressParams = Arguments.createMap();
                    pressParams.putString(EVENT_ARG_NODE_ID, nodeId);

                    // must use separate map
                    WritableMap clickParams = Arguments.createMap();
                    clickParams.putString(EVENT_ARG_NODE_ID, nodeId);

                    sendEvent(EVENT_PRESS, pressParams);
                    sendEvent(EVENT_CLICK, clickParams);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void removeOnPressEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiNode) {
                ((UiNode) node).setClickListener(null);
            }
        });
    }

    @ReactMethod
    public void addOnTextChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiTextEditNode) {
                ((UiTextEditNode) node).setTextChangedListener(text -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putString(EVENT_ARG_TEXT, text);

                    sendEvent(EVENT_TEXT_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void addOnToggleChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> {
            final Node node = UiNodesManager.findNodeWithId(nodeId);
            if (node instanceof UiToggleNode) {
                ((UiToggleNode) node).setToggleChangedListener(isOn -> {
                    WritableMap params = Arguments.createMap();
                    params.putString(EVENT_ARG_NODE_ID, nodeId);
                    params.putBoolean(EVENT_ARG_TOGGLE_ACTIVE, isOn);

                    sendEvent(EVENT_TOGGLE_CHANGED, params);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    @ReactMethod
    public void updateLayout() {
        // unused on Android
    }

    private void addNode(TransformNode node, String nodeId) {
        node.build();
        UiNodesManager.registerNode(node, nodeId);
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        MediaPlayerPool.INSTANCE.destroy();
    }
}

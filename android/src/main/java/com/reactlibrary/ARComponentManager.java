package com.reactlibrary;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.ar.sceneform.Node;
import com.reactlibrary.scene.UiNodesManager;
import com.reactlibrary.scene.nodes.GroupNode;
import com.reactlibrary.scene.nodes.ModelNode;
import com.reactlibrary.scene.nodes.UiButtonNode;
import com.reactlibrary.scene.nodes.UiImageNode;
import com.reactlibrary.scene.nodes.UiSpinnerNode;
import com.reactlibrary.scene.nodes.UiTextNode;
import com.reactlibrary.scene.nodes.base.TransformNode;
import com.reactlibrary.scene.nodes.base.UiNode;

import java.util.Collections;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Android module that is responisble for "parsing" JS tags to generate AR Nodes
 * Based on: https://facebook.github.io/react-native/docs/native-modules-android
 * <p>
 * Node creation methods are called from
 * react-native-magic-script/components/platform/platform-factory.js
 */
public class ARComponentManager extends ReactContextBaseJavaModule {

    private static final String LOG_TAG = "ARComponentManager";

    // All code inside react method must be called from main thread
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private ReactApplicationContext context;

    public ARComponentManager(ReactApplicationContext reactContext) {
        super(reactContext);
        // here activity is null yet (so we use initAR method)
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return "ARComponentManager";
    }

    @Override
    public Map<String, Object> getConstants() {
        return Collections.emptyMap();
    }

    /**
     * Must be called before adding AR View
     */
    @ReactMethod
    public void initAR() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                AppCompatActivity activity = (AppCompatActivity) getCurrentActivity();
                ArViewManager.initActivity(activity);
            }
        });
    }

    /**
     * Creates node that is a parent for other nodes
     * (it does not contain a view)
     *
     * @param props  properties (e.g. localPosition)
     * @param nodeId id of the node
     */
    @ReactMethod
    public void createGroupNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                TransformNode node = new GroupNode(props);
                node.build();
                UiNodesManager.registerNode(node, nodeId);
            }
        });
    }

    @ReactMethod
    public void createViewNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                TransformNode node = new GroupNode(props);
                node.build();
                UiNodesManager.registerNode(node, nodeId);
            }
        });
    }

    /**
     * Creates a button
     *
     * @param props  properties (e.g. localPosition)
     * @param nodeId id of the node
     */
    @ReactMethod
    public void createButtonNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNode node = new UiButtonNode(props, context);
                node.build();
                UiNodesManager.registerNode(node, nodeId);
            }
        });
    }

    @ReactMethod
    public void createImageNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNode node = new UiImageNode(props, context);
                node.build();
                UiNodesManager.registerNode(node, nodeId);
            }
        });
    }

    @ReactMethod
    public void createTextNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNode node = new UiTextNode(props, context);
                node.build();
                UiNodesManager.registerNode(node, nodeId);
            }
        });
    }

    @ReactMethod
    public void createModelNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO ( only stub currently)
                ModelNode node = new ModelNode(props, context);
                node.build();
                UiNodesManager.registerNode(node, nodeId);
            }
        });
    }

    @ReactMethod
    public void createSpinnerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNode node = new UiSpinnerNode(props, context);
                node.build();
                UiNodesManager.registerNode(node, nodeId);
            }
        });
    }

    @ReactMethod
    public void addChildNode(final String nodeId, final String parentId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNodesManager.addNodeToParent(nodeId, parentId);
            }
        });
    }

    @ReactMethod
    public void addChildNodeToContainer(final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNodesManager.addNodeToRoot(nodeId);
            }
        });
    }

    @ReactMethod
    public void removeChildNode(final String nodeId, final String parentId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNodesManager.removeNode(nodeId);
            }
        });
    }

    @ReactMethod
    public void removeChildNodeFromRoot(final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNodesManager.removeNode(nodeId);
            }
        });
    }

    @ReactMethod
    public void updateNode(final String nodeId, final ReadableMap properties) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNodesManager.updateNode(nodeId, properties);
            }
        });
    }

    @ReactMethod
    public void clearScene() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNodesManager.clear();
            }
        });
    }

    @ReactMethod
    public void validateScene() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UiNodesManager.validateScene();
            }
        });
    }


    // TODO separate react method for onClick ?
    @ReactMethod
    public void addOnPressEventHandler(final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Node node = UiNodesManager.findNodeWithId(nodeId);
                if (node instanceof UiNode) {
                    ((UiNode) node).setClickListener(new Function0<Unit>() {
                        @Override
                        public Unit invoke() {
                            WritableMap pressParams = Arguments.createMap();
                            pressParams.putString("nodeId", nodeId);

                            // must use separte map
                            WritableMap clickParams = Arguments.createMap();
                            clickParams.putString("nodeId", nodeId);

                            sendEvent("onPress", pressParams);
                            sendEvent("onClick", clickParams);
                            return Unit.INSTANCE;
                        }
                    });
                }
            }
        });
    }

    @ReactMethod
    public void removeOnPressEventHandler(final String nodeId) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Node node = UiNodesManager.findNodeWithId(nodeId);
                if (node instanceof UiNode) {
                    ((UiNode) node).setClickListener(null);
                }
            }
        });
    }


    private void sendEvent(String eventName, @Nullable WritableMap params) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

}

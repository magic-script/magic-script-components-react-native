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

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.vr.sdk.audio.GvrAudioEngine;
import com.magicleap.magicscript.ar.CubeRenderableBuilder;
import com.magicleap.magicscript.ar.CubeRenderableBuilderImpl;
import com.magicleap.magicscript.ar.ModelRenderableLoader;
import com.magicleap.magicscript.ar.ModelRenderableLoaderImpl;
import com.magicleap.magicscript.ar.VideoRenderableLoader;
import com.magicleap.magicscript.ar.VideoRenderableLoaderImpl;
import com.magicleap.magicscript.ar.ViewRenderableLoader;
import com.magicleap.magicscript.ar.ViewRenderableLoaderImpl;
import com.magicleap.magicscript.font.FontProvider;
import com.magicleap.magicscript.font.providers.AndroidFontProvider;
import com.magicleap.magicscript.font.providers.FontProviderImpl;
import com.magicleap.magicscript.icons.DefaultIconsProvider;
import com.magicleap.magicscript.icons.ExternalIconsProvider;
import com.magicleap.magicscript.icons.IconsRepository;
import com.magicleap.magicscript.icons.IconsRepositoryImpl;
import com.magicleap.magicscript.icons.ToggleIconsProviderImpl;
import com.magicleap.magicscript.scene.NodesManager;
import com.magicleap.magicscript.scene.nodes.ContentNode;
import com.magicleap.magicscript.scene.nodes.DialogNode;
import com.magicleap.magicscript.scene.nodes.GroupNode;
import com.magicleap.magicscript.scene.nodes.LineNode;
import com.magicleap.magicscript.scene.nodes.ModelNode;
import com.magicleap.magicscript.scene.nodes.PanelNode;
import com.magicleap.magicscript.scene.nodes.UIWebViewNode;
import com.magicleap.magicscript.scene.nodes.UiButtonNode;
import com.magicleap.magicscript.scene.nodes.UiCircleConfirmationNode;
import com.magicleap.magicscript.scene.nodes.UiColorPickerNode;
import com.magicleap.magicscript.scene.nodes.UiDatePickerNode;
import com.magicleap.magicscript.scene.nodes.UiDropdownListItemNode;
import com.magicleap.magicscript.scene.nodes.UiDropdownListNode;
import com.magicleap.magicscript.scene.nodes.UiImageNode;
import com.magicleap.magicscript.scene.nodes.UiListViewItemNode;
import com.magicleap.magicscript.scene.nodes.UiListViewNode;
import com.magicleap.magicscript.scene.nodes.UiProgressBarNode;
import com.magicleap.magicscript.scene.nodes.UiScrollBarNode;
import com.magicleap.magicscript.scene.nodes.UiScrollViewNode;
import com.magicleap.magicscript.scene.nodes.UiSliderNode;
import com.magicleap.magicscript.scene.nodes.UiSpinnerNode;
import com.magicleap.magicscript.scene.nodes.UiTabNode;
import com.magicleap.magicscript.scene.nodes.UiTextEditNode;
import com.magicleap.magicscript.scene.nodes.UiTextNode;
import com.magicleap.magicscript.scene.nodes.UiTimePickerNode;
import com.magicleap.magicscript.scene.nodes.audio.AudioFileProvider;
import com.magicleap.magicscript.scene.nodes.audio.AudioNode;
import com.magicleap.magicscript.scene.nodes.audio.ExternalAudioEngine;
import com.magicleap.magicscript.scene.nodes.audio.GvrAudioEngineWrapper;
import com.magicleap.magicscript.scene.nodes.audio.UriAudioProvider;
import com.magicleap.magicscript.scene.nodes.audio.VrAudioEngine;
import com.magicleap.magicscript.scene.nodes.base.TransformNode;
import com.magicleap.magicscript.scene.nodes.layouts.PageViewNode;
import com.magicleap.magicscript.scene.nodes.layouts.UiGridLayout;
import com.magicleap.magicscript.scene.nodes.layouts.UiLinearLayout;
import com.magicleap.magicscript.scene.nodes.layouts.UiRectLayout;
import com.magicleap.magicscript.scene.nodes.layouts.manager.GridLayoutManager;
import com.magicleap.magicscript.scene.nodes.layouts.manager.HorizontalLinearLayoutManager;
import com.magicleap.magicscript.scene.nodes.layouts.manager.LinearLayoutManager;
import com.magicleap.magicscript.scene.nodes.layouts.manager.PageViewLayoutManager;
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager;
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams;
import com.magicleap.magicscript.scene.nodes.picker.NativeFilePickerNode;
import com.magicleap.magicscript.scene.nodes.toggle.LinearToggleViewManager;
import com.magicleap.magicscript.scene.nodes.toggle.ToggleGroupNode;
import com.magicleap.magicscript.scene.nodes.toggle.ToggleViewManager;
import com.magicleap.magicscript.scene.nodes.toggle.UiToggleNode;
import com.magicleap.magicscript.scene.nodes.video.MediaPlayerPool;
import com.magicleap.magicscript.scene.nodes.video.VideoNode;
import com.magicleap.magicscript.scene.nodes.video.VideoPlayer;
import com.magicleap.magicscript.scene.nodes.video.VideoPlayerImpl;
import com.magicleap.magicscript.scene.nodes.views.DialogProviderImpl;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * A React module that is responsible for "parsing" JS tags in order to generate AR Nodes
 * Based on: https://facebook.github.io/react-native/docs/native-modules-android
 * <p>
 * Node creation methods are called from
 * react-native-magic-script/components/platform/platform-factory.js
 */
@SuppressWarnings("unused")
public class ARComponentManager extends ReactContextBaseJavaModule implements LifecycleEventListener, ActivityEventListener {

    private static final String COMPONENT_NAME = "ARComponentManager";
    // All code inside react method must be called from main thread
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ReactApplicationContext context;
    private NodesManager nodesManager;
    private EventsManager eventsManager;

    // Renderable loaders
    private ViewRenderableLoader viewRenderableLoader;
    private ModelRenderableLoader modelRenderableLoader;
    private VideoRenderableLoader videoRenderableLoader;
    private CubeRenderableBuilder cubeRenderableBuilder;

    // Other resources providers
    private FontProvider fontProvider;
    private IconsRepository iconsRepo;

    private MediaPlayerPool mediaPlayerPool;
    private List<ActivityResultObserver> activityResultObservers = new ArrayList<>();
    private List<LifecycleEventListener> lifecycleEventListeners = new ArrayList<>();

    public ARComponentManager(ReactApplicationContext reactContext, NodesManager nodesManager, EventsManager eventsManager,
                              MediaPlayerPool mediaPlayerPool) {
        super(reactContext);
        this.context = reactContext;
        this.nodesManager = nodesManager;
        this.eventsManager = eventsManager;
        this.mediaPlayerPool = mediaPlayerPool;

        this.viewRenderableLoader = new ViewRenderableLoaderImpl(context);
        this.modelRenderableLoader = new ModelRenderableLoaderImpl(context);
        this.videoRenderableLoader = new VideoRenderableLoaderImpl(context);
        this.cubeRenderableBuilder = new CubeRenderableBuilderImpl(context);

        AndroidFontProvider androidFontProvider = new AndroidFontProvider();
        this.fontProvider = new FontProviderImpl(context, androidFontProvider);

        DefaultIconsProvider defaultIconsProvider = new DefaultIconsProvider(context);
        ExternalIconsProvider externalIconsProvider = new ExternalIconsProvider(context);
        this.iconsRepo = new IconsRepositoryImpl(defaultIconsProvider, externalIconsProvider);

        context.addLifecycleEventListener(this);
        context.addActivityEventListener(this);
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
        mainHandler.post(() -> {
            UiButtonNode node = new UiButtonNode(props, context, viewRenderableLoader, fontProvider, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createImageNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiImageNode node = new UiImageNode(props, context, viewRenderableLoader, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createTextNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiTextNode node = new UiTextNode(props, context, viewRenderableLoader, fontProvider);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createTextEditNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiTextEditNode node = new UiTextEditNode(props, context, viewRenderableLoader, fontProvider);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createModelNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new ModelNode(props, context, modelRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createVideoNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            VideoPlayer videoPlayer = new VideoPlayerImpl(context);
            addNode(new VideoNode(props, context, videoPlayer, videoRenderableLoader), nodeId);
        });
    }

    @ReactMethod
    public void createScrollBarNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiScrollBarNode(props), nodeId));
    }

    @ReactMethod
    public void createScrollViewNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiScrollViewNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createSliderNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiSliderNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createSpinnerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiSpinnerNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createCircleConfirmationNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiCircleConfirmationNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createToggleNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            ToggleViewManager manager = new LinearToggleViewManager(fontProvider, new ToggleIconsProviderImpl());
            UiToggleNode node = new UiToggleNode(props, context, viewRenderableLoader, manager);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createToggleGroupNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new ToggleGroupNode(props), nodeId));
    }

    @ReactMethod
    public void createProgressBarNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiProgressBarNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createLineNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new LineNode(props, cubeRenderableBuilder), nodeId));
    }

    @ReactMethod
    public void createGridLayoutNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            GridLayoutManager layoutManager = new GridLayoutManager();
            addNode(new UiGridLayout(props, layoutManager), nodeId);
        });
    }

    @ReactMethod
    public void createLinearLayoutNode(final ReadableMap props, final String nodeId) {
        VerticalLinearLayoutManager verticalManager = new VerticalLinearLayoutManager<LayoutParams>();
        HorizontalLinearLayoutManager horizontalManager = new HorizontalLinearLayoutManager<LayoutParams>();
        LinearLayoutManager manager = new LinearLayoutManager(verticalManager, horizontalManager);
        mainHandler.post(() -> addNode(new UiLinearLayout(props, manager), nodeId));
    }

    @ReactMethod
    public void createDropdownListNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiDropdownListNode node = new UiDropdownListNode(props, context, viewRenderableLoader, fontProvider, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createDropdownListItemNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiDropdownListItemNode node = new UiDropdownListItemNode(props, context, viewRenderableLoader, fontProvider);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createColorPickerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiColorPickerNode node = new UiColorPickerNode(props, context, viewRenderableLoader, fontProvider, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createWebViewNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UIWebViewNode node = new UIWebViewNode(props, context, viewRenderableLoader);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createRectLayoutNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            VerticalLinearLayoutManager<LayoutParams> layoutManager = new VerticalLinearLayoutManager<>();
            addNode(new UiRectLayout(props, layoutManager), nodeId);
        });
    }

    @ReactMethod
    public void createListViewItemNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiListViewItemNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createListViewNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new UiListViewNode(props, context, viewRenderableLoader), nodeId));
    }

    @ReactMethod
    public void createDatePickerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiDatePickerNode datePickerNode = new UiDatePickerNode(props, context, viewRenderableLoader, new DialogProviderImpl());
            addNode(datePickerNode, nodeId);
        });
    }

    @ReactMethod
    public void createTimePickerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiTimePickerNode datePickerNode = new UiTimePickerNode(props, context, viewRenderableLoader, new DialogProviderImpl());
            addNode(datePickerNode, nodeId);
        });
    }

    @ReactMethod
    public void createDialogNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            DialogNode node = new DialogNode(props, ArViewManager.getActivityRef().get(), iconsRepo, new DialogProviderImpl());
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createTabNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            UiTabNode node = new UiTabNode(props, context, viewRenderableLoader, fontProvider, iconsRepo);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createPanelNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new PanelNode(props), nodeId));
    }

    @ReactMethod
    public void createContentNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new ContentNode(props), nodeId));
    }

    @ReactMethod
    public void createPageViewNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> addNode(new PageViewNode(props, new PageViewLayoutManager()), nodeId));
    }

    @ReactMethod
    public void createAudioNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            GvrAudioEngine gvrAudioEngine = new GvrAudioEngine(context, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);
            ExternalAudioEngine externalAudioEngine = new GvrAudioEngineWrapper(gvrAudioEngine);
            VrAudioEngine audioEngine = new VrAudioEngine(Executors.newSingleThreadExecutor(), externalAudioEngine);
            AudioFileProvider audioFileProvider = new UriAudioProvider(context);
            AudioNode node = new AudioNode(props, context, audioEngine, audioFileProvider);
            addNode(node, nodeId);
        });
    }

    @ReactMethod
    public void createFilePickerNode(final ReadableMap props, final String nodeId) {
        mainHandler.post(() -> {
            NativeFilePickerNode filePicker = new NativeFilePickerNode(props, context, viewRenderableLoader, fontProvider, iconsRepo);
            activityResultObservers.add(filePicker);
            addNode(filePicker, nodeId);
        });
    }

    @ReactMethod
    public void addChildNode(final String nodeId, final String parentId) {
        mainHandler.post(() -> nodesManager.addNodeToParent(nodeId, parentId));
    }

    @ReactMethod
    public void addChildNodeToContainer(final String nodeId) {
        mainHandler.post(() -> nodesManager.addNodeToRoot(nodeId));
    }

    @ReactMethod
    public void removeChildNode(final String nodeId, final String parentId) {
        mainHandler.post(() -> nodesManager.removeNode(nodeId));
    }

    @ReactMethod
    public void removeChildNodeFromRoot(final String nodeId) {
        mainHandler.post(() -> nodesManager.removeNode(nodeId));
    }

    @ReactMethod
    public void updateNode(final String nodeId, final ReadableMap properties) {
        mainHandler.post(() -> nodesManager.updateNode(nodeId, properties));
    }

    @ReactMethod
    public void clearScene() {
        mainHandler.post(() -> nodesManager.clear());
    }

    // region Events

    // activate = click
    @ReactMethod
    public void addOnActivateEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnActivateEventHandler(nodeId));
    }

    // touch down
    @ReactMethod
    public void addOnPressEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnPressEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnLongPressEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnLongPressEventHandler(nodeId));
    }

    // touch up
    @ReactMethod
    public void addOnReleaseEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnReleaseEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnFocusGainedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnFocusGainedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnFocusLostEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnFocusLostEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnUpdateEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnUpdateEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnDeleteEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnDeleteEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnEnabledEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnEnabledEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnDisabledEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnDisabledEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnTextChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnTextChangedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnToggleChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnToggleChangedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnVideoPreparedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnVideoPreparedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnSliderChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnSliderChangedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnSelectionChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnSelectionChangedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnColorConfirmedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnColorConfirmedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnColorCanceledEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnColorCanceledEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnColorChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnColorChangedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnDateChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnDateChangedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnDateConfirmedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnDateConfirmedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnScrollChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnScrollChangedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnTimeChangedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnTimeChangedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnTimeConfirmedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnTimeConfirmedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnDialogConfirmedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnDialogConfirmedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnDialogCanceledEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnDialogCanceledEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnDialogTimeExpiredEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnDialogTimeExpiredEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnConfirmationCompletedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnConfirmationCompletedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnConfirmationUpdatedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnConfirmationUpdatedEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnConfirmationCanceledEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnConfirmationCanceledEventHandler(nodeId));
    }

    @ReactMethod
    public void addOnFileSelectedEventHandler(final String nodeId) {
        mainHandler.post(() -> eventsManager.addOnFileSelectedEventHandler(nodeId));
    }

    // endregion

    @ReactMethod
    public void updateLayout() {
        // unused on Android
    }

    private void addNode(TransformNode node, String nodeId) {
        node.build();
        nodesManager.registerNode(node, nodeId);
    }

    @Override
    public void onHostResume() {
        if (nodesManager instanceof LifecycleEventListener) {
            ((LifecycleEventListener) nodesManager).onHostResume();
        }
    }

    @Override
    public void onHostPause() {
        if (nodesManager instanceof LifecycleEventListener) {
            ((LifecycleEventListener) nodesManager).onHostPause();
        }
    }

    @Override
    public void onHostDestroy() {
        if (nodesManager instanceof LifecycleEventListener) {
            ((LifecycleEventListener) nodesManager).onHostDestroy();
        }
        mediaPlayerPool.destroy();
    }

    @Override
    public void onActivityResult(final Activity activity, final int requestCode, final int resultCode, final Intent data) {
        activityResultObservers.forEach(activityResultObserver -> activityResultObserver.onActivityResult(requestCode, resultCode, data));
    }

    @Override
    public void onNewIntent(final Intent intent) {

    }
}

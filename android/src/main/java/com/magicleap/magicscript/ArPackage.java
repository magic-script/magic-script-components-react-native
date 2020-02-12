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

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.magicleap.magicscript.scene.NodesManager;
import com.magicleap.magicscript.scene.UiNodesManager;
import com.magicleap.magicscript.scene.nodes.video.GlobalMediaPlayerPool;
import com.magicleap.magicscript.scene.nodes.video.MediaPlayerPool;

import com.magicleap.magicscript.plane.ARPlaneDetector;
import com.magicleap.magicscript.plane.ARPlaneDetectorBridge;
import com.magicleap.magicscript.plane.ARPlaneDetectorEvents;
import com.magicleap.magicscript.plane.ARPlaneDetectorEventsManager;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ArPackage implements ReactPackage {

    @NotNull
    @Override
    public List<NativeModule> createNativeModules(@NotNull ReactApplicationContext reactContext) {
        NodesManager nodesManager = UiNodesManager.Companion.getINSTANCE();
        EventsManager eventsManager = new ReactEventsManager(new ReactEventsEmitter(reactContext), nodesManager);
        MediaPlayerPool mediaPlayerPool = GlobalMediaPlayerPool.INSTANCE;
        ARComponentManager arComponentManager = new ARComponentManager(reactContext, nodesManager, eventsManager, mediaPlayerPool);
        ARPlaneDetectorEventsManager arPlaneEventsManager = new ARPlaneDetectorEvents(reactContext);
        ARPlaneDetector arPlaneDetector = new ARPlaneDetector(reactContext, arPlaneEventsManager, ARPlaneDetectorBridge.Companion.getINSTANCE());
        return Arrays.<NativeModule>asList(arComponentManager, arPlaneDetector);
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<ViewManager> createViewManagers(@NotNull ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(new ArViewManager());
    }

}

//
//  RCTARView.m
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 12/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

#import "RCTARView.h"
#import "RNMagicScript-Swift.h"

@interface RCTARView() <UIGestureRecognizerDelegate, ARSCNViewDelegate>

@property (nonatomic, strong) ARSession *session;
@property (nonatomic, strong) ARWorldTrackingConfiguration *configuration;

@end


@implementation RCTARView

- (instancetype)init {
    if ((self = [super init])) {
        self.arView = [self createARView];
        [self resume];
    }

    return self;
}

- (ARSCNView *)createARView {
    ARSCNView *view = [[ARSCNView alloc] init];
    view.autoenablesDefaultLighting = YES;
    view.rendersContinuously = YES;
    view.scene.rootNode.name = @"root";

#if TARGET_IPHONE_SIMULATOR
    // Allow for basic orbit gestures if we're running in the simulator
    view.allowsCameraControl = YES;
    view.defaultCameraController.interactionMode = SCNInteractionModeOrbitTurntable;
    view.defaultCameraController.maximumVerticalAngle = 45;
    view.defaultCameraController.inertiaEnabled = YES;
    [view.defaultCameraController translateInCameraSpaceByX:(float) 0.0 Y:(float) 0.0 Z:(float) 3.0];
#endif

    // Add gesture recognizer
    UITapGestureRecognizer *tapGestureRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapAction:)];
    tapGestureRecognizer.numberOfTapsRequired = 1;
    [view addGestureRecognizer:tapGestureRecognizer];

    // Resgister scene in nodes manager
    [UiNodesManager.instance registerScene:view];

    // Register ARSession in MLXrClientSession
    [XrClientSession registerARSession:view.session];
    view.delegate = self;

    // Add AR view as a child
    view.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:view];
    [view.topAnchor constraintEqualToAnchor:self.topAnchor].active = YES;
    [view.leadingAnchor constraintEqualToAnchor:self.leadingAnchor].active = YES;
    [view.trailingAnchor constraintEqualToAnchor:self.trailingAnchor].active = YES;
    [view.bottomAnchor constraintEqualToAnchor:self.bottomAnchor].active = YES;
    return view;
}

- (SCNNode*) renderer:(id<SCNSceneRenderer>)renderer nodeForAnchor:(ARAnchor *)anchor {
    UiNodesManager *nodesManager = UiNodesManager.instance;
    TransformNode *node = [nodesManager findNodeWithAnchorUuid:anchor.name];
    return node;
}

- (void)pause {
    [self.arView.session pause];
}

- (void)resume {
    if (self.configuration) {
        [self.arView.session runWithConfiguration:self.configuration];
    }
}

- (void)reset {
    if (self.configuration) {
        [self.arView.session runWithConfiguration:self.configuration options:ARSessionRunOptionRemoveExistingAnchors | ARSessionRunOptionResetTracking];
    }
}

- (BOOL)debug {
    return self.arView.showsStatistics;
}

- (void)setDebug:(BOOL)debug {
    if (debug) {
        self.arView.showsStatistics = YES;
        self.arView.debugOptions = ARSCNDebugOptionShowWorldOrigin | ARSCNDebugOptionShowFeaturePoints;
    } else {
        self.arView.showsStatistics = NO;
        self.arView.debugOptions = SCNDebugOptionNone;
    }
}

- (void)setRendersContinuously:(BOOL)rendersContinuously {
    self.arView.rendersContinuously = rendersContinuously;
}

- (BOOL)rendersContinuously {
    return self.arView.rendersContinuously;
}

#pragma mark - Lazy loads

- (ARWorldTrackingConfiguration *)configuration {
    if (!ARWorldTrackingConfiguration.isSupported) {
        return nil;
    }

    if (_configuration) {
        return _configuration;
    }

    _configuration = [ARWorldTrackingConfiguration new];
    // _configuration.planeDetection = ARPlaneDetectionHorizontal;
    const NSUInteger videoFormatCount = ARWorldTrackingConfiguration.supportedVideoFormats.count;
    if (videoFormatCount > 0) {
        _configuration.videoFormat = ARWorldTrackingConfiguration.supportedVideoFormats[videoFormatCount - 1];
    }
    _configuration.worldAlignment = 0;
    _configuration.autoFocusEnabled = false;
    _configuration.providesAudioData = false;
    _configuration.maximumNumberOfTrackedImages = 1;

    return _configuration;
}

- (void)handleTapAction: (UITapGestureRecognizer *)recognizer {
    // Take the screen space tap coordinates and pass them to the hitTest method on the ARSCNView instance
    const CGPoint tapPoint = [recognizer locationInView:self.arView];

    UiNodesManager *nodesManager = UiNodesManager.instance;
    NSDictionary<SCNHitTestOption, id> *options = @{
                                                    SCNHitTestBoundingBoxOnlyKey: @YES,
                                                    SCNHitTestIgnoreHiddenNodesKey: @YES,
                                                    // SCNHitTestOptionCategoryBitMask: @(nodesManager.componentNodeBitMask),
                                                    SCNHitTestRootNodeKey: nodesManager.rootNode,
                                                    };
    NSArray<SCNHitTestResult *> *results = [self.arView hitTest:tapPoint options:options];
    SCNHitTestResult *result = results.firstObject;
    [nodesManager handleNodeTap: result ? result.node : nil];
}

@end

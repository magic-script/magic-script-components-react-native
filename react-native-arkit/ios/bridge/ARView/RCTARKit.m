//
//  RCTARKit.m
//  RCTARKit
//
//  Created by HippoAR on 7/9/17.
//  Copyright © 2017 HippoAR. All rights reserved.
//

#import "RCTARKit.h"
#import "RCTConvert+ARKit.h"

@interface RCTARKit () <ARSCNViewDelegate, ARSessionDelegate, UIGestureRecognizerDelegate>

@property (nonatomic, strong) ARSession* session;
@property (nonatomic, strong) ARWorldTrackingConfiguration *configuration;

@end


void dispatch_once_on_main_thread(dispatch_once_t *predicate,
                                  dispatch_block_t block) {
    if ([NSThread isMainThread]) {
        dispatch_once(predicate, block);
    } else {
        if (DISPATCH_EXPECT(*predicate == 0L, NO)) {
            dispatch_sync(dispatch_get_main_queue(), ^{
                dispatch_once(predicate, block);
            });
        }
    }
}


@implementation RCTARKit
static RCTARKit *instance = nil;

+ (bool)isInitialized {
    return instance != nil;
}

+ (instancetype)sharedInstance {
    
    static dispatch_once_t onceToken;
    
    dispatch_once_on_main_thread(&onceToken, ^{
        if (instance == nil) {
            ARSCNView *arView = [[ARSCNView alloc] init];
            instance = [[self alloc] initWithARView:arView];
        }
    });
    
    return instance;
}

- (instancetype)initWithARView:(ARSCNView *)arView {
    if ((self = [super init])) {
        self.arView = arView;
        
        // delegates
//        arView.delegate = self;
//        arView.session.delegate = self;

        UITapGestureRecognizer *tapGestureRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapFrom:)];
        tapGestureRecognizer.numberOfTapsRequired = 1;
        [self.arView addGestureRecognizer:tapGestureRecognizer];

        [UiNodesManager.instance registerScene:arView.scene];
        
        // configuration(s)
        arView.autoenablesDefaultLighting = YES;
        arView.rendersContinuously = YES;
        arView.scene.rootNode.name = @"root";
        
    #if TARGET_IPHONE_SIMULATOR
        // allow for basic orbit gestures if we're running in the simulator
        arView.allowsCameraControl = YES;
        arView.defaultCameraController.interactionMode = SCNInteractionModeOrbitTurntable;
        arView.defaultCameraController.maximumVerticalAngle = 45;
        arView.defaultCameraController.inertiaEnabled = YES;
        [arView.defaultCameraController translateInCameraSpaceByX:(float) 0.0 Y:(float) 0.0 Z:(float) 3.0];
        
    #endif
        // start ARKit
        [self addSubview:arView];
        [self resume];
    }

    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.arView.frame = self.bounds;
}

- (void)pause {
    [self.session pause];
}

- (void)resume {
    [self.session runWithConfiguration:self.configuration];
}

- (void)session:(ARSession *)session didFailWithError:(NSError *)error {
}

- (void)reset {
    if (ARWorldTrackingConfiguration.isSupported) {
        [self.session runWithConfiguration:self.configuration options:ARSessionRunOptionRemoveExistingAnchors | ARSessionRunOptionResetTracking];
    }
}


#pragma mark - setter-getter

- (ARSession *)session {
    return self.arView.session;
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

-(ARWorldTrackingConfiguration *)configuration {
    if (_configuration) {
        return _configuration;
    }
    
    if (!ARWorldTrackingConfiguration.isSupported) {}
    
    _configuration = [ARWorldTrackingConfiguration new];
    _configuration.planeDetection = ARPlaneDetectionHorizontal;
    return _configuration;
}

- (void)handleTapFrom: (UITapGestureRecognizer *)recognizer {
    // Take the screen space tap coordinates and pass them to the hitTest method on the ARSCNView instance
    const CGPoint tapPoint = [recognizer locationInView:self.arView];
    UiNodesManager *nodesManager = UiNodesManager.instance;
    NSDictionary<SCNHitTestOption, id> *options = @{
                                                        SCNHitTestBoundingBoxOnlyKey: @YES,
                                                        SCNHitTestIgnoreHiddenNodesKey: @YES,
//                                                        SCNHitTestOptionCategoryBitMask: @(nodesManager.componentNodeBitMask),
                                                        SCNHitTestRootNodeKey: nodesManager.rootNode,
                                                    };
    NSArray<SCNHitTestResult *> *results = [self.arView hitTest:tapPoint options:options];
    SCNHitTestResult *result = results.firstObject;
    if (result != nil) {
        [nodesManager handleNodeTap: result.node];
    }
}



#pragma mark - ARSCNViewDelegate

- (void)renderer:(id<SCNSceneRenderer>)renderer updateAtTime:(NSTimeInterval)time {
}

- (void)renderer:(id <SCNSceneRenderer>)renderer didRenderScene:(SCNScene *)scene atTime:(NSTimeInterval)time {
}

- (void)renderer:(id <SCNSceneRenderer>)renderer willUpdateNode:(SCNNode *)node forAnchor:(ARAnchor *)anchor {
}

- (void)renderer:(id <SCNSceneRenderer>)renderer didAddNode:(SCNNode *)node forAnchor:(ARAnchor *)anchor {
}

- (void)renderer:(id <SCNSceneRenderer>)renderer didUpdateNode:(SCNNode *)node forAnchor:(ARAnchor *)anchor {
}

- (void)renderer:(id<SCNSceneRenderer>)renderer didRemoveNode:(SCNNode *)node forAnchor:(ARAnchor *)anchor {
}

@end

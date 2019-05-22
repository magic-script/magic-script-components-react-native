//
//  RNScene3dView.m
//  RNMagicScriptComponents
//
//  Created by Pawel Leszkiewicz on 20/05/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "RNScene3dView.h"
#import <SceneKit/SceneKit.h>

@interface RNScene3dView ()

@property(strong, nonatomic) SCNView *scnView;
@property(assign, nonatomic) BOOL showStatistics;

@end

@implementation RNScene3dView

- (instancetype)init {
    self = [super init];
    _scnView = [self createSceneView:0.f];
    return self;
}

- (void)setBackgroundColor:(UIColor *)backgroundColor {
    _scnView.backgroundColor = backgroundColor;
}

- (UIColor *)backgroundColor {
    return _scnView.backgroundColor;
}

- (void)setShowStatistics:(BOOL)showStatistics {
    _scnView.showsStatistics = showStatistics;
}

- (BOOL)showStatistics {
    return _scnView.showsStatistics;
}

- (void)addSubview:(UIView *)view {
    [super addSubview:view];
    NSLog(@"addSubview: %@", view);
}

- (SCNScene *)createScene {
    // create a new scene
    SCNScene *scene = [SCNScene new];

    // create and add a camera to the scene
    SCNNode *cameraNode = [SCNNode node];
    cameraNode.camera = [SCNCamera camera];
    [scene.rootNode addChildNode:cameraNode];

    const float boxSize = 1.f;
    SCNBox *box = [SCNBox boxWithWidth:boxSize height:boxSize length:boxSize chamferRadius:0.f];
    SCNNode *boxNode = [SCNNode nodeWithGeometry:box];
    [scene.rootNode addChildNode:boxNode];

    cameraNode.position = SCNVector3Make(0, 0, 15);

    return scene;
}

- (SCNView *)createSceneView:(CGFloat)inset {
    SCNView *sceneView = [[SCNView alloc] init];
    sceneView.scene = [self createScene];
    sceneView.allowsCameraControl = YES;
    sceneView.showsStatistics = YES;
    sceneView.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:sceneView];
    [sceneView.leftAnchor constraintEqualToAnchor:self.leftAnchor constant:inset].active = YES;
    [sceneView.topAnchor constraintEqualToAnchor:self.topAnchor constant:inset].active = YES;
    [sceneView.rightAnchor constraintEqualToAnchor:self.rightAnchor constant:-inset].active = YES;
    [sceneView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:-inset].active = YES;
    return sceneView;
}

@end

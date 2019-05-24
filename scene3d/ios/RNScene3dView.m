//
//  RNScene3dView.m
//  RNMagicScriptComponents
//
//  Created by Pawel Leszkiewicz on 20/05/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "RNScene3dView.h"
#import <SceneKit/SceneKit.h>
#import "RNScene3d-Swift.h"

@interface RNScene3dView ()

@property(strong, nonatomic) SCNView *sceneView;
@property(assign, nonatomic) BOOL showStatistics;

@end

@implementation RNScene3dView

- (instancetype)init {
    self = [super init];
    _sceneView = [self createSceneView:0.f];
    [self setupComponents];
    return self;
}

- (void)setBackgroundColor:(UIColor *)backgroundColor {
    _sceneView.backgroundColor = backgroundColor;
}

- (UIColor *)backgroundColor {
    return _sceneView.backgroundColor;
}

- (void)setShowStatistics:(BOOL)showStatistics {
    _sceneView.showsStatistics = showStatistics;
}

- (BOOL)showStatistics {
    return _sceneView.showsStatistics;
}

- (SCNView *)createSceneView:(CGFloat)inset {
    SCNView *scnView = [[SCNView alloc] init];
    scnView.scene = [self createScene];
    scnView.allowsCameraControl = YES;
    scnView.showsStatistics = YES;
    scnView.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:scnView];
    [scnView.leftAnchor constraintEqualToAnchor:self.leftAnchor constant:inset].active = YES;
    [scnView.topAnchor constraintEqualToAnchor:self.topAnchor constant:inset].active = YES;
    [scnView.rightAnchor constraintEqualToAnchor:self.rightAnchor constant:-inset].active = YES;
    [scnView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:-inset].active = YES;
    return scnView;
}

- (SCNScene *)createScene {
    // create a new scene
    SCNScene *scene = [SCNScene new];

    // create and add a camera to the scene
    SCNNode *cameraNode = [SCNNode node];
    cameraNode.camera = [SCNCamera camera];
    [scene.rootNode addChildNode:cameraNode];

    cameraNode.position = SCNVector3Make(0, 0, 15);

    return scene;
}

- (void)setupComponents {

    SCNScene *scene = self.sceneView.scene;

    SCNSphere *sphere = [SCNSphere sphereWithRadius:0.05f];
    sphere.firstMaterial.diffuse.contents = [UIColor yellowColor];
    SCNNode *sphereNode = [SCNNode nodeWithGeometry:sphere];
    sphereNode.position = SCNVector3Make(0, 0, 0.1f);
    [scene.rootNode addChildNode:sphereNode];

    // Text node
    RNTextNode *textNode = [RNTextNode new];
    textNode.text = @"abc";
    textNode.size = CGSizeMake(1.f, 1.f);
    [scene.rootNode addChildNode:textNode];

    // Image node
    RNImageNode *imageNode = [RNImageNode new];
    imageNode.size = CGSizeMake(2.f, 2.f);
    imageNode.image = [UIImage imageNamed: @"sample_image"];
    imageNode.position = SCNVector3Make(-2, 0, 0);
    [scene.rootNode addChildNode:imageNode];

    RNButtonNode *buttonNode = [RNButtonNode new];
    buttonNode.title = @"Button";
    buttonNode.size = CGSizeMake(2.f, 1.f);
    buttonNode.color = [UIColor yellowColor];
    buttonNode.position = SCNVector3Make(2, 0, 0);
    [scene.rootNode addChildNode:buttonNode];
    textNode.text = @"wxyz";
}

@end

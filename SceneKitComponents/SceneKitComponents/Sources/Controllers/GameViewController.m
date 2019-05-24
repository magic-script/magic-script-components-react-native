//
//  GameViewController.m
//  SceneKitComponents
//
//  Created by Pawel Leszkiewicz on 24/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

#import "GameViewController.h"
#import "SceneKitComponents-Swift.h"

@interface GameViewController ()
@property (weak, nonatomic) SCNView *sceneView;
@end


@implementation GameViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    // create a new scene
    SCNScene *scene = [SCNScene scene];

    // create and add a camera to the scene
    SCNNode *cameraNode = [SCNNode node];
    cameraNode.camera = [SCNCamera camera];
    [scene.rootNode addChildNode:cameraNode];
    
    // place the camera
    cameraNode.position = SCNVector3Make(0, 0, 15);
    
    // create and add a light to the scene
    SCNNode *lightNode = [SCNNode node];
    lightNode.light = [SCNLight light];
    lightNode.light.type = SCNLightTypeOmni;
    lightNode.position = SCNVector3Make(0, 10, 10);
    [scene.rootNode addChildNode:lightNode];
    
    // create and add an ambient light to the scene
    SCNNode *ambientLightNode = [SCNNode node];
    ambientLightNode.light = [SCNLight light];
    ambientLightNode.light.type = SCNLightTypeAmbient;
    ambientLightNode.light.color = [UIColor darkGrayColor];
    [scene.rootNode addChildNode:ambientLightNode];
    
    SCNView *scnView = [[SCNView alloc] init];
    scnView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:scnView];
    [scnView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor].active = YES;
    [scnView.topAnchor constraintEqualToAnchor:self.view.topAnchor].active = YES;
    [scnView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor].active = YES;
    [scnView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor].active = YES;
    self.sceneView = scnView;
    
    // set the scene to the view
    scnView.scene = scene;
    
    // allows the user to manipulate the camera
    scnView.allowsCameraControl = YES;
        
    // show statistics such as fps and timing information
    scnView.showsStatistics = YES;

    // configure the view
    scnView.backgroundColor = [UIColor colorWithWhite:55.f / 255.f alpha:1.f];

    [self setupComponents];
}

- (void) setupComponents {

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

- (BOOL)shouldAutorotate
{
    return YES;
}

- (BOOL)prefersStatusBarHidden {
    return YES;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations
{
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        return UIInterfaceOrientationMaskAllButUpsideDown;
    } else {
        return UIInterfaceOrientationMaskAll;
    }
}

@end

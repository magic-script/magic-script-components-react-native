//
//  RCTARKit.h
//  RCTARKit
//
//  Created by HippoAR on 7/9/17.
//  Copyright © 2017 HippoAR. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <SceneKit/SceneKit.h>
#import <ARKit/ARKit.h>

@interface RCTARKit : UIView

+ (instancetype)sharedInstance;
+ (bool)isInitialized;
- (instancetype)initWithARView:(ARSCNView *)arView;

@property (nonatomic, strong) ARSCNView *arView;

@property (nonatomic, assign) BOOL debug;
@property (nonatomic, assign) BOOL rendersContinuously;

- (void)pause;
- (void)resume;
- (void)reset;

@end



#if __has_include("RCTARKitARCL.h")
#import "RCTARKitARCL.h"
@compatibility_alias ARKit RCTARKitARCL;
#else
@compatibility_alias ARKit RCTARKit;
#endif

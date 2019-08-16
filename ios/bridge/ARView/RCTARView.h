//
//  RCTARView.h
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 12/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <SceneKit/SceneKit.h>
#import <ARKit/ARKit.h>

@interface RCTARView : UIView

+ (RCTARView*) current;
+ (void) setCurrent:(RCTARView*)val;

@property (nonatomic, strong) ARSCNView *arView;

@property (nonatomic, assign) BOOL debug;
@property (nonatomic, assign) BOOL rendersContinuously;

- (void)pause;
- (void)resume;
- (void)reset;

@end

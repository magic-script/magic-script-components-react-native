//
//  RNScene3dViewManager.m
//  RNMagicScriptComponents
//
//  Created by Pawel Leszkiewicz on 20/05/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "RNScene3dViewManager.h"
#import "RNScene3dView.h"
#import <SceneKit/SceneKit.h>

@implementation RNScene3dViewManager

RCT_EXPORT_MODULE(RNScene3dView)

RCT_EXPORT_VIEW_PROPERTY(showStatistics, BOOL)

- (UIView *)view {
    return [RNScene3dView new];
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

@end

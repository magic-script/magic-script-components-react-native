//
//  RNWorldManager.m
//  RNMagicScriptComponents
//
//  Created by Pawel Leszkiewicz on 20/05/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "RNWorldManager.h"
#import "RNWorldView.h"
#import <SceneKit/SceneKit.h>

@implementation RNWorldManager

RCT_EXPORT_MODULE(RNWorld)

RCT_EXPORT_VIEW_PROPERTY(showStatistics, BOOL)

- (UIView *)view {
    return [RNWorldView new];
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

@end

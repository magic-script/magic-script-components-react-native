//
//  RCTARViewManager.m
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 12/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

#import "RCTARViewManager.h"
#import "RCTARView.h"

@implementation RCTARViewManager

RCT_EXPORT_MODULE()

- (UIView *)view {
    return [UIView new];
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

@end

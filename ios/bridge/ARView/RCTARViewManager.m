//
//  RCTARViewManager.m
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 12/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

#import "RCTARViewManager.h"
#import "RCTARView.h"

@implementation RCTARViewManager

RCT_EXPORT_MODULE()

- (UIView *)view {
    return [RCTARView new];
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(debug, BOOL)
RCT_EXPORT_VIEW_PROPERTY(rendersContinuously, BOOL)

@end

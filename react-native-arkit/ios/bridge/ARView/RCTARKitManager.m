//
//  RCTARKitManager.m
//  RCTARKit
//
//  Created by HippoAR on 7/9/17.
//  Copyright © 2017 HippoAR. All rights reserved.
//

#import "RCTARKitManager.h"
#import "RCTARKit.h"

@implementation RCTARKitManager

RCT_EXPORT_MODULE()

- (UIView *)view {
    return [ARKit sharedInstance];
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

RCT_EXPORT_VIEW_PROPERTY(debug, BOOL)
RCT_EXPORT_VIEW_PROPERTY(rendersContinuously, BOOL)

RCT_EXPORT_METHOD(pause:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [[ARKit sharedInstance] pause];
    resolve(@{});
}

RCT_EXPORT_METHOD(resume:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [[ARKit sharedInstance] resume];
    resolve(@{});
}

RCT_EXPORT_METHOD(reset:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [[ARKit sharedInstance] reset];
    resolve(@{});
}

@end

//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

#import "ARPlaneDetector.h"
#import "ARPlaneDetectorEvents.h"
#import "RNMagicScript-Swift.h"

#import <Foundation/Foundation.h>

@interface ARPlaneDetector ()
@end

@implementation ARPlaneDetector

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(startDetecting:(NSDictionary *)configuration) {
    NSLog(@"startDetecting %@", configuration);
    [[PlaneDetector instance] enablePlaneDetectionWith:configuration];
}

RCT_EXPORT_METHOD(stopDetecting) {
    NSLog(@"stopDetecting");
    [[PlaneDetector instance] disablePlaneDetection];
}

RCT_EXPORT_METHOD(getAllPlanes:(NSDictionary *)configuration callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"getAllPlanes %@", configuration);
    NSDictionary *surfaces = [[PlaneDetector instance] getAllPlanesWith:configuration];
    callback(@[[NSNull null], surfaces]);
}

RCT_EXPORT_METHOD(reset) {
    NSLog(@"reset");
    [[PlaneDetector instance] reset];
}

RCT_EXPORT_METHOD(requestPlaneCast:(NSDictionary *)configuration callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"requestPlaneCast %@", configuration);
    callback(@[[NSNull null], @[]]);
}

RCT_EXPORT_METHOD(addOnPlaneDetectedEventHandler) {
    NSLog(@"addOnPlaneDetectedEventHandler");
    PlaneDetector.instance.onPlaneDetected = ^(PlaneDetector *sender, NSUUID *id, NSString *type, NSArray<NSArray<NSNumber *> *> *vertices, NSArray<NSNumber *> *center, NSArray<NSNumber *> *normal) {
        [[ARPlaneDetectorEvents instance] onPlaneDetectedEventReceived:sender id:id type:type vertices:vertices center:center normal:normal];
    };
}

RCT_EXPORT_METHOD(addOnPlaneUpdatedEventHandler) {
    NSLog(@"addOnPlaneUpdatedEventHandler");
    PlaneDetector.instance.onPlaneUpdated = ^(PlaneDetector *sender, NSUUID *id, NSString *type, NSArray<NSArray<NSNumber *> *> *vertices, NSArray<NSNumber *> *center, NSArray<NSNumber *> *normal) {
        [[ARPlaneDetectorEvents instance] onPlaneUpdatedEventReceived:sender id:id type:type vertices:vertices center:center normal:normal];
    };
}

RCT_EXPORT_METHOD(addOnPlaneRemovedEventHandler) {
    NSLog(@"addOnPlaneUpdatedEventHandler");
    PlaneDetector.instance.onPlaneRemoved = ^(PlaneDetector *sender, NSUUID *id, NSString *type, NSArray<NSArray<NSNumber *> *> *vertices, NSArray<NSNumber *> *center, NSArray<NSNumber *> *normal) {
        [[ARPlaneDetectorEvents instance] onPlaneRemovedEventReceived:sender id:id type:type vertices:vertices center:center normal:normal];
    };
}

RCT_EXPORT_METHOD(addOnPlaneTappedEventHandler) {
    NSLog(@"addOnPlaneTappedEventHandler");
    PlaneDetector.instance.onPlaneTapped = ^(PlaneDetector *sender, NSUUID *id, NSString *type, NSArray<NSArray<NSNumber *> *> *vertices, NSArray<NSNumber *> *center, NSArray<NSNumber *> *normal, NSArray<NSNumber *> *point) {
        [[ARPlaneDetectorEvents instance] onPlaneTappedEventReceived:sender id:id type:type vertices:vertices center:center normal:normal point:point];
    };
}

@end

//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
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

#import "ARPlaneDetectorEvents.h"
#import "RNMagicScript-Swift.h"

static ARPlaneDetectorEvents *_instance = nil;

@implementation ARPlaneDetectorEvents {
    bool hasListeners;
}

RCT_EXPORT_MODULE();

+ (instancetype)instance {
    return _instance;
}

- (void)setBridge:(RCTBridge *)bridge {
    [super setBridge:bridge];
    _instance = self;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

// Will be called when this module's first listener is added.
- (void)startObserving {
    NSLog(@"[ARPlaneDetectorEvents] startObserving");
    hasListeners = YES;
}

// Will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving {
    NSLog(@"[ARPlaneDetectorEvents] stopObserving");
    hasListeners = NO;
}

- (NSArray<NSString *> *)supportedEvents {
    return @[@"onPlaneDetected",
             @"onPlaneUpdated",
             @"onPlaneRemoved",
             @"onPlaneTapped"];
}

- (void)onEventWithName:(NSString *)name sender:(PlaneDetector *)sender body:(NSDictionary *)body {
    if (hasListeners) {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSMutableDictionary *fullBody = NULL;
            if (body != NULL) {
                fullBody = [NSMutableDictionary dictionaryWithDictionary:body];
            } else {
                fullBody = [@{} mutableCopy];
            }
            [self sendEventWithName:name body:fullBody];
        });
    }
}

- (void)onPlaneDetectedEventReceived:(PlaneDetector *)sender id:(NSUUID *)id type:(NSString *)type vertices:(NSArray<NSArray<NSNumber *> *> *)vertices center:(NSArray<NSNumber *> *)center normal:(NSArray<NSNumber *> *)normal {
    [self onEventWithName:@"onPlaneDetected" sender:sender body:
     @{
         @"id": [id UUIDString],
         @"type": type,
         @"vertices": vertices,
         @"center": center,
         @"normal": normal,
     }];
}

- (void)onPlaneUpdatedEventReceived:(PlaneDetector *)sender id:(NSUUID *)id type:(NSString *)type vertices:(NSArray<NSArray<NSNumber *> *> *)vertices center:(NSArray<NSNumber *> *)center normal:(NSArray<NSNumber *> *)normal {
    [self onEventWithName:@"onPlaneUpdated" sender:sender body:
     @{
         @"id": [id UUIDString],
         @"type": type,
         @"vertices": vertices,
         @"center": center,
         @"normal": normal,
     }];
}

- (void)onPlaneRemovedEventReceived:(PlaneDetector *)sender id:(NSUUID *)id type:(NSString *)type vertices:(NSArray<NSArray<NSNumber *> *> *)vertices center:(NSArray<NSNumber *> *)center normal:(NSArray<NSNumber *> *)normal {
    [self onEventWithName:@"onPlaneRemoved" sender:sender body:
     @{
         @"id": [id UUIDString],
         @"type": type,
         @"vertices": vertices,
         @"center": center,
         @"normal": normal,
     }];
}

- (void)onPlaneTappedEventReceived:(PlaneDetector *)sender id:(NSUUID *)id type:(NSString *)type vertices:(NSArray<NSArray<NSNumber *> *> *)vertices center:(NSArray<NSNumber *> *)center normal:(NSArray<NSNumber *> *)normal point:(NSArray<NSNumber *> *)point {
    [self onEventWithName:@"onPlaneTapped" sender:sender body:
     @{
         @"id": [id UUIDString],
         @"type": type,
         @"vertices": vertices,
         @"center": center,
         @"normal": normal,
         @"point": point
     }];
}

@end

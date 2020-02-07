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

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

NS_ASSUME_NONNULL_BEGIN

@class PlaneDetector;

@interface ARPlaneDetectorEvents : RCTEventEmitter <RCTBridgeModule>
+ (instancetype)instance;

- (void)onPlaneDetectedEventReceived:(PlaneDetector *)sender id:(NSUUID *)id type:(NSString *)type vertices:(NSArray<NSArray<NSNumber *> *> *)vertices center:(NSArray<NSNumber *> *)center normal:(NSArray<NSNumber *> *)normal;
- (void)onPlaneUpdatedEventReceived:(PlaneDetector *)sender id:(NSUUID *)id type:(NSString *)type vertices:(NSArray<NSArray<NSNumber *> *> *)vertices center:(NSArray<NSNumber *> *)center normal:(NSArray<NSNumber *> *)normal;
- (void)onPlaneRemovedEventReceived:(PlaneDetector *)sender id:(NSUUID *)id type:(NSString *)type vertices:(NSArray<NSArray<NSNumber *> *> *)vertices center:(NSArray<NSNumber *> *)center normal:(NSArray<NSNumber *> *)normal;
- (void)onPlaneTappedEventReceived:(PlaneDetector *)sender id:(NSUUID *)id type:(NSString *)type vertices:(NSArray<NSArray<NSNumber *> *> *)vertices center:(NSArray<NSNumber *> *)center normal:(NSArray<NSNumber *> *)normal point:(NSArray<NSNumber *> *)point;
@end

NS_ASSUME_NONNULL_END

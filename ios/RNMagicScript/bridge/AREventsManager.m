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

#import "AREventsManager.h"
#import "RNMagicScript-Swift.h"

static AREventsManager *_instance = nil;

@implementation AREventsManager {
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
    NSLog(@"[AREventsManager] startObserving");
    hasListeners = YES;
}

// Will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving {
    NSLog(@"[AREventsManager] stopObserving");
    hasListeners = NO;
}

- (NSArray<NSString *> *)supportedEvents {
    return @[
             @"onPress",
             @"onClick",
             @"onTextChanged",
             @"onToggleChanged",
             @"onVideoPrepared"
             ];
}

- (void)onPressEventReceived:(UiNode *)sender {
    if (hasListeners) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:@"onPress" body:@{ @"nodeId": sender.name }];
        });
    }
}

- (void)onClickEventReceived:(UiNode *)sender {
    if (hasListeners) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:@"onClick" body:@{ @"nodeId": sender.name }];
        });
    }
}

- (void)onTextChangedEventReceived:(UiNode *)sender text:(NSString *)text {
    if (hasListeners) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:@"onTextChanged" body:@{ @"nodeId": sender.name, @"text": text }];
        });
    }
}

- (void)onToggleChangedEventReceived:(UiNode *)sender value:(BOOL)value {
    if (hasListeners) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:@"onToggleChanged" body:@{ @"nodeId": sender.name, @"On": @(value) }];
        });
    }
}

- (void)onVideoPreparedEventReceived:(UiVideoNode *)sender videoURL:(NSString *)videoURL {
    if (hasListeners) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:@"onVideoPrepared" body:@{ @"nodeId": sender.name, @"videoURL": videoURL }];
        });
    }
}

@end

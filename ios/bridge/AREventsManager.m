//
//  AREventsManager.m
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 07/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
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
             @"onPress", @"onClick",
             @"onTextChanged",
             @"onToggleChanged"
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

- (void)onToggleChangedEventReceived:(UiNode *)sender value:(BOOL)value {
    if (hasListeners) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:@"onToggleChanged" body:@{ @"nodeId": sender.name, @"value": @(value) }];
        });
    }
}

@end

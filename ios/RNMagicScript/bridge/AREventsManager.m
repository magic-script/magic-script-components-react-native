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
        // UiNode
        @"onActivate",
        @"onClick",
        //@"onPress",
        //@"onLongPress",
        //@"onRelease",
        @"onEnabled",
        @"onDisabled",
        @"onFocusGained",
        @"onFocusLost",
        @"onUpdate",
        @"onDelete",
        // UiScrollViewNode
        @"onScrollChanged",
        // UiDropDownList
        @"onSelectionChanged",
        // UiSliderNode
        @"onSliderChanged",
        // UiTextEditNode
        @"onTextChanged",
        // UiToggleNode
        @"onToggleChanged",
        // UiVideoNode
        @"onVideoPrepared",
        // UiDatePickerNode
        @"onDateChanged",
        @"onDateConfirmed",
        // UiTimePickerNode
        @"onTimeChanged",
        @"onTimeConfirmed",
        // UiColorPickerNode
        @"onColorChanged",
        @"onColorConfirmed",
        @"onColorCanceled",
        // UiDialogNode
        @"onDialogConfirmed",
        @"onDialogCanceled",
        @"onDialogTimeExpired",
        // UiCircleConfirmationNode
        @"onConfirmationCompleted",
        @"onConfirmationUpdated",
        @"onConfirmationCanceled"
     ];
}

- (void)onEventWithName:(NSString *)name received:(UiNode *)sender body:(NSDictionary *)body {
    if (hasListeners) {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSMutableDictionary *fullBody = NULL;
            if (body != NULL) {
                fullBody = [NSMutableDictionary dictionaryWithDictionary:body];
            } else {
                fullBody = [@{} mutableCopy];
            }
            fullBody[@"nodeId"] = sender.name
            [self sendEventWithName:name body:fullBody];
        });
    }
}

- (void)onActivateEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onActivate" received:sender body:NULL];
}

- (void)onClickEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onClick" received:sender body:NULL];
}

- (void)onEnabledEventReceived:(UiNode *) {
    [self onEventWithName:@"onEnabled" received:sender body:NULL]
}

- (void)onDisabledEventReceived:(UiNode *) {
    [self onEventWithName:@"onDisabled" received:sender body:NULL]
}

- (void)onFocusGainedEventReceived:(UiNode *) {
    [self onEventWithName:@"onFocusGained" received:sender body:NULL]
}

- (void)onFocusLostEventReceived:(UiNode *) {
    [self onEventWithName:@"onFocusLost" received:sender body:NULL]
}

- (void)onUpdateEventReceived:(UiNode *) {
    [self onEventWithName:@"onUpdate" received:sender body:NULL]
}

- (void)onDeleteEventReceived:(UiNode *) {
    [self onEventWithName:@"onDelete" received:sender body:NULL]
}

- (void)onScrollChangedEventReceived:(UiNode *)sender value:(CGFloat)value {
    [self onEventWithName:@"onScrollChanged" received:sender body:@{ @"ScrollValue": @(value) }];
}

- (void)onTextChangedEventReceived:(UiNode *)sender text:(NSString *)text {
    [self onEventWithName:@"onTextChanged" received:sender body:@{ @"text": text }];
}

- (void)onToggleChangedEventReceived:(UiNode *)sender value:(BOOL)value {
    [self onEventWithName:@"onToggleChanged" received:sender body:@{ @"On": @(value) }];
}

- (void)onVideoPreparedEventReceived:(UiVideoNode *)sender videoURL:(NSString *)videoURL {
    [self onEventWithName:@"onVideoPrepared" received:sender body:@{ @"videoURL": videoURL }];
}

- (void)onSelectionChangedEventReceived:(UiDropdownListNode *)sender selectedItemsIndexes:(NSArray<NSNumber *> *)selectedItemsIndexes {
    [self onEventWithName:@"onSelectionChanged" received:sender body:@{ @"selectedItems": selectedItemsIndexes }];
}

- (void)onSliderChangedEventReceived:(UiSliderNode *)sender value:(CGFloat)value {
    [self onEventWithName:@"onSliderChanged" received:sender body:@{ @"Value": @(value) }];
}

- (void)onDateChangedEventReceived:(UiDatePickerNode *)sender value:(NSString *)value {
    [self onEventWithName:@"onDateChanged" received:sender body:@{ @"Value": @(value) }];
}

- (void)onDateConfirmedEventReceived:(UiDatePickerNode *)sender value:(NSString *)value {
    [self onEventWithName:@"onDateConfirmed" received:sender body:@{ @"Value": @(value) }];
}

- (void)onTimeChangedEventReceived:(UiTimePickerNode *)sender value:(NSString *)value {
    [self onEventWithName:@"onTimeChanged" received:sender body:@{ @"Value": @(value) }];
}

- (void)onTimeConfirmedEventReceived:(UiTimePickerNode *)sender value:(NSString *)value {
    [self onEventWithName:@"onTimeConfirmed" received:sender body:@{ @"Value": @(value) }];
}

- (void)onColorChangedEventReceived:(UiColorPickerNode *)sender value:(NSArray<NSNumber *> *)value {
    [self onEventWithName:@"onColorChanged" received:sender body:@{ @"Color": @(value) }];
}

- (void)onColorConfirmedEventReceived:(UiColorPickerNode *)sender value:(NSArray<NSNumber *> *)value {
    [self onEventWithName:@"onColorConfirmed" received:sender body:@{ @"Color": @(value) }];
}

- (void)onColorCanceledEventReceived:(UiColorPickerNode *)sender value:(NSArray<NSNumber *> *)value {
    [self onEventWithName:@"onColorCanceled" received:sender body:@{ @"Color": @(value) }];
}

- (void)onDialogConfirmedEventReceived:(UiDialogNode *)sender {
    [self onEventWithName:@"onDialogConfirmed" received:sender body:NULL];
}

- (void)onDialogCanceledEventReceived:(UiDialogNode *)sender {
    [self onEventWithName:@"onDialogCanceled" received:sender body:NULL];
}

- (void)onDialogTimeExpiredEventReceived:(UiDialogNode *)sender {
    [self onEventWithName:@"onDialogTimeExpired" received:sender body:NULL];
}

- (void)onConfirmationCompletedEventReceived:(UiCircleConfirmationNode *)sender {
    [self onEventWithName:@"onConfirmationCompleted" received:sender body:NULL];
}

- (void)onConfirmationUpdatedEventReceived:(UiCircleConfirmationNode *)sender value:(CGFloat)value {
    [self onEventWithName:@"onConfirmationUpdated" received:sender body:@{ @"Value": @(value) }];
}

- (void)onConfirmationCanceledEventReceived:(UiCircleConfirmationNode *)sender {
    [self onEventWithName:@"onConfirmationCanceled" received:sender body:NULL];
}

@end

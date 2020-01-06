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
        @"onConfirmationCanceled",
        // FilePicker
        @"onFileSelected"
     ];
}

- (void)onEventWithName:(NSString *)name sender:(TransformNode *)sender body:(NSDictionary *)body {
    if (hasListeners) {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSMutableDictionary *fullBody = NULL;
            if (body != NULL) {
                fullBody = [NSMutableDictionary dictionaryWithDictionary:body];
            } else {
                fullBody = [@{} mutableCopy];
            }
            fullBody[@"nodeId"] = sender.name;
            [self sendEventWithName:name body:fullBody];
        });
    }
}

- (void)onActivateEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onActivate" sender:sender body:NULL];
}

- (void)onClickEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onClick" sender:sender body:NULL];
}

- (void)onEnabledEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onEnabled" sender:sender body:NULL];
}

- (void)onDisabledEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onDisabled" sender:sender body:NULL];
}

- (void)onFocusGainedEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onFocusGained" sender:sender body:NULL];
}

- (void)onFocusLostEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onFocusLost" sender:sender body:NULL];
}

- (void)onUpdateEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onUpdate" sender:sender body:NULL];
}

- (void)onDeleteEventReceived:(UiNode *)sender {
    [self onEventWithName:@"onDelete" sender:sender body:NULL];
}

- (void)onScrollChangedEventReceived:(UiNode *)sender value:(CGFloat)value {
    [self onEventWithName:@"onScrollChanged" sender:sender body:@{ @"ScrollValue": @(value) }];
}

- (void)onTextChangedEventReceived:(UiNode *)sender text:(NSString *)text {
    [self onEventWithName:@"onTextChanged" sender:sender body:@{ @"text": text }];
}

- (void)onToggleChangedEventReceived:(UiNode *)sender value:(BOOL)value {
    [self onEventWithName:@"onToggleChanged" sender:sender body:@{ @"On": @(value) }];
}

- (void)onVideoPreparedEventReceived:(UiVideoNode *)sender videoURL:(NSString *)videoURL {
    [self onEventWithName:@"onVideoPrepared" sender:sender body:@{ @"videoURL": videoURL }];
}

- (void)onSelectionChangedEventReceived:(UiDropdownListNode *)sender selectedItemsIndexes:(NSArray<NSNumber *> *)selectedItemsIndexes {
    [self onEventWithName:@"onSelectionChanged" sender:sender body:@{ @"selectedItems": selectedItemsIndexes }];
}

- (void)onSliderChangedEventReceived:(UiSliderNode *)sender value:(CGFloat)value {
    [self onEventWithName:@"onSliderChanged" sender:sender body:@{ @"Value": @(value) }];
}

- (void)onDateChangedEventReceived:(UiDatePickerNode *)sender value:(NSString *)value {
    [self onEventWithName:@"onDateChanged" sender:sender body:@{ @"Value": value }];
}

- (void)onDateConfirmedEventReceived:(UiDatePickerNode *)sender value:(NSString *)value {
    [self onEventWithName:@"onDateConfirmed" sender:sender body:@{ @"Value": value }];
}

- (void)onTimeChangedEventReceived:(UiTimePickerNode *)sender value:(NSString *)value {
    [self onEventWithName:@"onTimeChanged" sender:sender body:@{ @"Value": value }];
}

- (void)onTimeConfirmedEventReceived:(UiTimePickerNode *)sender value:(NSString *)value {
    [self onEventWithName:@"onTimeConfirmed" sender:sender body:@{ @"Value": value }];
}

- (void)onColorChangedEventReceived:(UiColorPickerNode *)sender value:(NSArray<NSNumber *> *)value {
    [self onEventWithName:@"onColorChanged" sender:sender body:@{ @"Color": value }];
}

- (void)onColorConfirmedEventReceived:(UiColorPickerNode *)sender value:(NSArray<NSNumber *> *)value {
    [self onEventWithName:@"onColorConfirmed" sender:sender body:@{ @"Color": value }];
}

- (void)onColorCanceledEventReceived:(UiColorPickerNode *)sender value:(NSArray<NSNumber *> *)value {
    [self onEventWithName:@"onColorCanceled" sender:sender body:@{ @"Color": value }];
}

- (void)onDialogConfirmedEventReceived:(UiDialogNode *)sender {
    [self onEventWithName:@"onDialogConfirmed" sender:sender body:NULL];
}

- (void)onDialogCanceledEventReceived:(UiDialogNode *)sender {
    [self onEventWithName:@"onDialogCanceled" sender:sender body:NULL];
}

- (void)onDialogTimeExpiredEventReceived:(UiDialogNode *)sender {
    [self onEventWithName:@"onDialogTimeExpired" sender:sender body:NULL];
}

- (void)onConfirmationCompletedEventReceived:(UiCircleConfirmationNode *)sender {
    [self onEventWithName:@"onConfirmationCompleted" sender:sender body:NULL];
}

- (void)onConfirmationUpdatedEventReceived:(UiCircleConfirmationNode *)sender value:(CGFloat)value {
    const CGFloat angle = 2 * M_PI * value;
    [self onEventWithName:@"onConfirmationUpdated" sender:sender body:@{ @"Angle": @(angle) }];
}

- (void)onConfirmationCanceledEventReceived:(UiCircleConfirmationNode *)sender {
    [self onEventWithName:@"onConfirmationCanceled" sender:sender body:NULL];
}

@end

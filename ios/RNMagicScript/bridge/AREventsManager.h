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
#import <SceneKit/SceneKit.h>

@class UiNode;
@class UiVideoNode;
@class UiSliderNode;
@class UiDropdownListNode;
@class UiDropdownListItemNode;
@class UiDatePickerNode;
@class UiTimePickerNode;
@class UiColorPickerNode;
@class UiDialogNode;
@class UiCircleConfirmationNode;

@interface AREventsManager : RCTEventEmitter <RCTBridgeModule>

+ (instancetype)instance;
- (void)onActivateEventReceived:(UiNode *)sender;
- (void)onClickEventReceived:(UiNode *)sender;
- (void)onEnabledEventReceived:(UiNode *)sender;
- (void)onDisabledEventReceived:(UiNode *)sender;
- (void)onFocusGainedEventReceived:(UiNode *)sender;
- (void)onFocusLostEventReceived:(UiNode *)sender;
- (void)onUpdateEventReceived:(UiNode *)sender;
- (void)onDeleteEventReceived:(UiNode *)sender;
- (void)onScrollChangedEventReceived:(UiNode *)sender value:(CGFloat)value;
- (void)onTextChangedEventReceived:(UiNode *)sender text:(NSString *)text;
- (void)onToggleChangedEventReceived:(UiNode *)sender value:(BOOL)value;
- (void)onVideoPreparedEventReceived:(UiVideoNode *)sender videoURL:(NSString *)videoURL;
- (void)onSelectionChangedEventReceived:(UiDropdownListNode *)sender selectedItems:(NSArray<UiDropdownListItemNode *> *)selectedItems;
- (void)onSliderChangedEventReceived:(UiSliderNode *)sender value:(CGFloat)value;
- (void)onDateChangedEventReceived:(UiDatePickerNode *)sender value:(NSString *)value;
- (void)onDateConfirmedEventReceived:(UiDatePickerNode *)sender value:(NSString *)value;
- (void)onTimeChangedEventReceived:(UiTimePickerNode *)sender value:(NSString *)value;
- (void)onTimeConfirmedEventReceived:(UiTimePickerNode *)sender value:(NSString *)value;
- (void)onColorChangedEventReceived:(UiColorPickerNode *)sender value:(NSArray<NSNumber *> *)value;
- (void)onColorConfirmedEventReceived:(UiColorPickerNode *)sender value:(NSArray<NSNumber *> *)value;
- (void)onColorCanceledEventReceived:(UiColorPickerNode *)sender value:(NSArray<NSNumber *> *)value;
- (void)onDialogConfirmedEventReceived:(UiDialogNode *)sender;
- (void)onDialogCanceledEventReceived:(UiDialogNode *)sender;
- (void)onDialogTimeExpiredEventReceived:(UiDialogNode *)sender;
- (void)onConfirmationCompletedEventReceived:(UiCircleConfirmationNode *)sender;
- (void)onConfirmationUpdatedEventReceived:(UiCircleConfirmationNode *)sender value:(CGFloat)value;
- (void)onConfirmationCanceledEventReceived:(UiCircleConfirmationNode *)sender;
@end

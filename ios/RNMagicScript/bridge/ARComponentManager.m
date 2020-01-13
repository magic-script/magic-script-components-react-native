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

#import "ARComponentManager.h"
#import "AREventsManager.h"
#import "RCTConvert+Components.h"
#import "RNMagicScript-Swift.h"

void ARLog(NSString *format, ...) {
    static BOOL logEnabled = NO;
    if (logEnabled) {
        va_list ap;             // Type to hold information about variable arguments.
        va_start (ap, format);  // Initialize a variable argument list.
        NSLogv(format, ap);
        va_end (ap);            // End using variable argument list.
    }
}

@interface ARComponentManager ()
@end

@implementation ARComponentManager

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

- (void)registerNode:(TransformNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    [UiNodesManager.instance registerNode:node nodeId:nodeId];
    resolve(nil);
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(createAudioNode:(UiAudioNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createButtonNode:(UiButtonNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createCircleConfirmationNode:(UiCircleConfirmationNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createContentNode:(UiColorPickerNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createColorPickerNode:(UiColorPickerNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createDatePickerNode:(UiDatePickerNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createDialogNode:(UiDialogNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createDropdownListNode:(UiDropdownListNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createDropdownListItemNode:(UiDropdownListItemNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createGridLayoutNode:(UiGridLayoutNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createGroupNode:(UiGroupNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createImageNode:(UiImageNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createLineNode:(UiLineNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createLinearLayoutNode:(UiLinearLayoutNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createListViewNode:(UiListViewNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createListViewItemNode:(UiListViewItemNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createModelNode:(UiModelNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createPageViewNode:(UiPageViewNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createPanelNode:(UiPanelNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createProgressBarNode:(UiProgressBarNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createRectLayoutNode:(UiRectLayoutNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createScrollBarNode:(UiScrollBarNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createScrollViewNode:(UiScrollViewNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createSliderNode:(UiSliderNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createSpinnerNode:(UiSpinnerNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createTabNode:(UiTabNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createTextNode:(UiTextNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createTextEditNode:(UiTextEditNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createTimePickerNode:(UiTimePickerNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createToggleNode:(UiToggleNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createToggleGroupNode:(UiToggleGroupNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createVideoNode:(UiVideoNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(createWebViewNode:(UiWebViewNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self registerNode:node nodeId:nodeId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(addChildNode:(NSString *)nodeId toParentNode:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"addChildNode: %@ toParentNode: %@", nodeId, parentId);
    [UiNodesManager.instance addNode:nodeId toParent:parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addChildNodeToContainer:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"addChildNodeToContainer: %@", nodeId);
    [UiNodesManager.instance addNodeToRoot:nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(removeChildNode:(NSString *)nodeId fromParentNode:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"removeChildNode: %@ fromParentNode: %@", nodeId, parentId);
    [UiNodesManager.instance removeNode:nodeId fromParent:parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(removeChildNodeFromRoot:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"removeNodeFromRoot: %@", nodeId);
    [UiNodesManager.instance removeNodeFromRoot:nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(clearScene:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"clearScene");
    [UiNodesManager.instance clear];
    resolve(nil);
}

RCT_EXPORT_METHOD(updateNode:(NSString *)nodeId properties:(NSDictionary *)properties resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"updateNode: %@", nodeId);
    if ([UiNodesManager.instance updateNode:nodeId properties:properties]) {
        resolve(nil);
    } else {
        NSString *message = [NSString stringWithFormat:@"Could not update node \"%@\"", nodeId];
        reject(@"updateNodeError", message, nil);
    }
}

RCT_EXPORT_METHOD(updateLayout:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"updateLayout");
    [UiNodesManager.instance updateLayout];
    resolve(nil);
}

// MARK: - UiNode event handlers
RCT_EXPORT_METHOD(addOnActivateEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onActivate = ^(UiNode *sender) {
        [[AREventsManager instance] onActivateEventReceived:sender];
        [[AREventsManager instance] onClickEventReceived:sender];
    };
}

RCT_EXPORT_METHOD(removeOnActivateEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onActivate = NULL;
}

RCT_EXPORT_METHOD(addOnEnabledEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onEnabled = ^(UiNode *sender) {
        [[AREventsManager instance] onEnabledEventReceived:sender];
    };
}

RCT_EXPORT_METHOD(removeOnEnabledEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onEnabled = NULL;
}

RCT_EXPORT_METHOD(addOnDisabledEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onDisabled = ^(UiNode *sender) {
        [[AREventsManager instance] onDisabledEventReceived:sender];
    };
}

RCT_EXPORT_METHOD(removeOnDisabledEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onDisabled = NULL;
}

RCT_EXPORT_METHOD(addOnFocusGainedEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onFocusGained = ^(UiNode *sender) {
        [[AREventsManager instance] onFocusGainedEventReceived:sender];
    };
}

RCT_EXPORT_METHOD(removeOnFocusGainedEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onFocusGained = NULL;
}

RCT_EXPORT_METHOD(addOnFocusLostEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onFocusLost = ^(UiNode *sender) {
        [[AREventsManager instance] onFocusLostEventReceived:sender];
    };
}

RCT_EXPORT_METHOD(removeOnFocusLostEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onFocusLost = NULL;
}

RCT_EXPORT_METHOD(addOnUpdateEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onUpdate = ^(UiNode *sender) {
        [[AREventsManager instance] onUpdateEventReceived:sender];
    };
}

RCT_EXPORT_METHOD(removeOnUpdateEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onUpdate = NULL;
}

RCT_EXPORT_METHOD(addOnDeleteEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onDelete = ^(UiNode *sender) {
        [[AREventsManager instance] onDeleteEventReceived:sender];
    };
}

RCT_EXPORT_METHOD(removeOnDeleteEventHandler:(NSString *)nodeId) {
    UiNode *node = [UiNodesManager.instance findUiNodeWithId:nodeId];
    node.onDelete = NULL;
}

// MARK: - UiScrollViewNode event handlers
RCT_EXPORT_METHOD(addOnScrollChangedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiScrollViewNode class]]) {
        ((UiScrollViewNode *)node).onScrollChanged = ^(UiNode *sender, CGFloat value) {
            ARLog(@"scrollView changed: %@", @(value));
            [[AREventsManager instance] onScrollChangedEventReceived:sender value:value];
        };
    }
}

// MARK: - UiTextEditNode event handlers
RCT_EXPORT_METHOD(addOnTextChangedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiTextEditNode class]]) {
        ((UiTextEditNode *)node).onTextChanged = ^(UiNode *sender, NSString *text) {
            ARLog(@"textEdit changed: %@", text);
            [[AREventsManager instance] onTextChangedEventReceived:sender text:text];
        };
    }
}

// MARK: - UiToggleNode event handlers
RCT_EXPORT_METHOD(addOnToggleChangedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiToggleNode class]]) {
        ((UiToggleNode *)node).onChanged = ^(UiNode *sender, BOOL on) {
            ARLog(@"toggle onChanged: %@", on ? @"on" : @"off");
            [[AREventsManager instance] onToggleChangedEventReceived:sender value:on];
        };
    }
}

// MARK: - UiVideoNode event handlers
RCT_EXPORT_METHOD(addOnVideoPreparedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiVideoNode class]]) {
        ((UiVideoNode *)node).onVideoPrepared = ^(UiVideoNode *sender, NSString *videoURL) {
            ARLog(@"video onPrepared");
            [[AREventsManager instance] onVideoPreparedEventReceived:sender videoURL:videoURL];
        };
    }
}

// MARK: - UiDropdownListNode event handlers
RCT_EXPORT_METHOD(addOnSelectionChangedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiDropdownListNode class]]) {
        ((UiDropdownListNode *)node).onSelectionChanged = ^(UiDropdownListNode *sender, NSArray<UiDropdownListItemNode *> *selectedItems) {
            ARLog(@"DropdownList item selected");
            [[AREventsManager instance] onSelectionChangedEventReceived:sender selectedItems:selectedItems];
        };
    }
}

// MARK: - UiSliderNode event handlers
RCT_EXPORT_METHOD(addOnSliderChangedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiSliderNode class]]) {
        ((UiSliderNode *)node).onSliderChanged = ^(UiSliderNode *sender, CGFloat value) {
            ARLog(@"slider changed: %@", @(value));
            [[AREventsManager instance] onSliderChangedEventReceived:sender value:value];
        };
    }
}

// MARK: - UiDatePickerNode event handlers
RCT_EXPORT_METHOD(addOnDateChangedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiDatePickerNode class]]) {
        ((UiDatePickerNode *)node).onDateChanged = ^(UiDatePickerNode *sender, NSString *value) {
            ARLog(@"datePicker changed: %@", value);
            [[AREventsManager instance] onDateChangedEventReceived:sender value:value];
        };
    }
}

RCT_EXPORT_METHOD(addOnDateConfirmedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiDatePickerNode class]]) {
        ((UiDatePickerNode *)node).onDateConfirmed = ^(UiDatePickerNode *sender, NSString *value) {
            ARLog(@"datePicker confirmed: %@", value);
            [[AREventsManager instance] onDateConfirmedEventReceived:sender value:value];
        };
    }
}

// MARK: - UiTimePickerNode event handlers
RCT_EXPORT_METHOD(addOnTimeChangedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiTimePickerNode class]]) {
        ((UiTimePickerNode *)node).onTimeChanged = ^(UiTimePickerNode *sender, NSString *value) {
            ARLog(@"timePicker changed: %@", value);
            [[AREventsManager instance] onTimeChangedEventReceived:sender value:value];
        };
    }
}

RCT_EXPORT_METHOD(addOnTimeConfirmedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiTimePickerNode class]]) {
        ((UiTimePickerNode *)node).onTimeConfirmed = ^(UiTimePickerNode *sender, NSString *value) {
            ARLog(@"timePicker confirmed: %@", value);
            [[AREventsManager instance] onTimeConfirmedEventReceived:sender value:value];
        };
    }
}

// MARK: - UiColorPickerNode event handlers
RCT_EXPORT_METHOD(addOnColorChangedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiColorPickerNode class]]) {
        ((UiColorPickerNode *)node).onColorChanged = ^(UiColorPickerNode *sender, NSArray<NSNumber *> *value) {
            ARLog(@"colorPicker changed: %@", value);
            [[AREventsManager instance] onColorChangedEventReceived:sender value:value];
        };
    }
}

RCT_EXPORT_METHOD(addOnColorConfirmedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiColorPickerNode class]]) {
        ((UiColorPickerNode *)node).onColorConfirmed = ^(UiColorPickerNode *sender, NSArray<NSNumber *> *value) {
            ARLog(@"colorPicker confirmed: %@", value);
            [[AREventsManager instance] onColorConfirmedEventReceived:sender value:value];
        };
    }
}

RCT_EXPORT_METHOD(addOnColorCanceledEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiColorPickerNode class]]) {
        ((UiColorPickerNode *)node).onColorCanceled = ^(UiColorPickerNode *sender, NSArray<NSNumber *> *value) {
            ARLog(@"colorPicker canceled: %@");
            [[AREventsManager instance] onColorCanceledEventReceived:sender value:value];
        };
    }
}

// MARK: - UiDialogNode event handlers
RCT_EXPORT_METHOD(addOnDialogConfirmedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiDialogNode class]]) {
        ((UiDialogNode *)node).onDialogConfirmed = ^(UiDialogNode *sender) {
            ARLog(@"dialogNode confirmed: %@");
            [[AREventsManager instance] onDialogConfirmedEventReceived:sender];
        };
    }
}

RCT_EXPORT_METHOD(addOnDialogCanceledEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiDialogNode class]]) {
        ((UiDialogNode *)node).onDialogCanceled = ^(UiDialogNode *sender) {
            ARLog(@"dialogNode canceled: %@");
            [[AREventsManager instance] onDialogCanceledEventReceived:sender];
        };
    }
}

RCT_EXPORT_METHOD(addOnDialogTimeExpiredEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiDialogNode class]]) {
        ((UiDialogNode *)node).onDialogTimeExpired = ^(UiDialogNode *sender) {
            ARLog(@"dialogNode timeExpired: %@");
            [[AREventsManager instance] onDialogTimeExpiredEventReceived:sender];
        };
    }
}

// MARK: - UiCircleConfirmationNode event handlers
RCT_EXPORT_METHOD(addOnConfirmationCompletedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiCircleConfirmationNode class]]) {
        ((UiCircleConfirmationNode *)node).onConfirmationCompleted = ^(UiCircleConfirmationNode *sender) {
            ARLog(@"circleConfirmationNode completed: %@");
            [[AREventsManager instance] onConfirmationCompletedEventReceived:sender];
        };
    }
}

RCT_EXPORT_METHOD(addOnConfirmationUpdatedEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiCircleConfirmationNode class]]) {
        ((UiCircleConfirmationNode *)node).onConfirmationUpdated = ^(UiCircleConfirmationNode *sender, CGFloat value) {
            ARLog(@"circleConfirmationNode completed: %@");
            [[AREventsManager instance] onConfirmationUpdatedEventReceived:sender value:value];
        };
    }
}

RCT_EXPORT_METHOD(addOnConfirmationCanceledEventHandler:(NSString *)nodeId) {
    TransformNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiCircleConfirmationNode class]]) {
        ((UiCircleConfirmationNode *)node).onConfirmationCanceled = ^(UiCircleConfirmationNode *sender) {
            ARLog(@"circleConfirmationNode canceled: %@");
            [[AREventsManager instance] onConfirmationCanceledEventReceived:sender];
        };
    }
}

@end

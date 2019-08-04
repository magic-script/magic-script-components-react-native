//
//  ARComponentManager.m
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 29/05/2019.
//  Copyright © 2019 HippoAR. All rights reserved.
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

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(initAR:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    // Empty method, created for consistency with Android API.
    ARLog(@"initAR");
    resolve(nil);
}

RCT_EXPORT_METHOD(createButtonNode:(UiButtonNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createButtonNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createGridLayoutNode:(UiGridLayoutNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createGridLayoutNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createGroupNode:(UiGroupNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createGroupNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createImageNode:(UiImageNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createImageNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createLineNode:(UiLineNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createLineNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createModelNode:(UiModelNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createModelNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createProgressBarNode:(UiProgressBarNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createProgressBarNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createSliderNode:(UiSliderNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createSliderNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createSpinnerNode:(UiSpinnerNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createSpinnerNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createTextNode:(UiTextNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createTextNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createTextEditNode:(UiTextEditNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createTextEditNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createToggleNode:(UiToggleNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"createToggleNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addChildNode:(NSString *)nodeId toParentNode:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"addChildNode: %@ toParentNode: %@", nodeId, parentId);
    [UiNodesManager.instance addNode: nodeId toParent: parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addChildNodeToContainer:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"addChildNodeToContainer: %@", nodeId);
    [UiNodesManager.instance addNodeToRoot: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(removeChildNode:(NSString *)nodeId fromParentNode:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"removeChildNode: %@ fromParentNode: %@", nodeId, parentId);
    [UiNodesManager.instance removeNode: nodeId fromParent: parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(removeChildNodeFromRoot:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"removeNodeFromRoot: %@", nodeId);
    [UiNodesManager.instance removeNodeFromRoot: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(clearScene:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"clearScene");
    [UiNodesManager.instance clear];
    resolve(nil);
}

RCT_EXPORT_METHOD(validateScene:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    ARLog(@"validateScene");
    [UiNodesManager.instance validateScene];
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

RCT_EXPORT_METHOD(addOnPressEventHandler:(NSString *)nodeId) {
    ARLog(@"addOnPressEventHandler: %@", nodeId);
    SCNNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiButtonNode class]]) {
        UiButtonNode *button = (UiButtonNode *)node;
        button.onTap = ^(UiNode *sender) {
            ARLog(@"button onTap");
            [[AREventsManager instance] onPressEventReceived:sender];
            [[AREventsManager instance] onClickEventReceived:sender];
        };
    }
}

RCT_EXPORT_METHOD(removeOnPressEventHandler:(NSString *)nodeId) {
    ARLog(@"removeOnPressEventHandler: %@", nodeId);
    SCNNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiButtonNode class]]) {
        UiButtonNode *button = (UiButtonNode *)node;
        button.onTap = nil;
    }
}

RCT_EXPORT_METHOD(addOnToggleChangedEventHandler:(NSString *)nodeId) {
    ARLog(@"addOnToggleChangedEventHandler: %@", nodeId);
    SCNNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiToggleNode class]]) {
        UiToggleNode *toggle = (UiToggleNode *)node;
        toggle.onChanged = ^(UiNode *sender, BOOL on) {
            ARLog(@"toggle onChanged: %@", on ? @"on" : @"off");
            [[AREventsManager instance] onToggleChangedEventReceived:sender value:on];
        };
    }
}

@end

//
//  ARComponentManager.m
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 29/05/2019.
//  Copyright © 2019 HippoAR. All rights reserved.
//

#import "ARComponentManager.h"
#import "AREventsManager.h"
#import "RCTConvert+ARKit.h"
#import "RCTARKit-Swift.h"

static ARComponentManager *_instance = nil;

@interface ARComponentManager ()
@end


@implementation ARComponentManager

+ (instancetype)instance {
    return _instance;
}

- (id)init {
    if (self = [super init]) {
        _instance = self;
    }
    return self;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}


RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(createViewNode:(SCNNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"createViewNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createTextNode:(UiTextNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"createTextNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createGroupNode:(UiGroupNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"createGroupNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createButtonNode:(UiButtonNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"createButtonNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createImageNode:(UiImageNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"createImageNode: %@", nodeId);
    [UiNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addChildNode:(NSString *)nodeId toParentNode:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"addChildNode: %@ toParentNode: %@", nodeId, parentId);
    [UiNodesManager.instance addNode: nodeId toParent: parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addChildNodeToContainer:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"addChildNodeToContainer: %@", nodeId);
    [UiNodesManager.instance addNodeToRoot: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(removeChildNode:(NSString *)nodeId fromParentNode:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"removeChildNode: %@ fromParentNode: %@", nodeId, parentId);
    [UiNodesManager.instance removeNode: nodeId fromParent: parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(clearScene:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"clearScene");
    [UiNodesManager.instance clear];
    resolve(nil);
}

RCT_EXPORT_METHOD(validateScene:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"validateScene");
    [UiNodesManager.instance validateScene];
    resolve(nil);
}

RCT_EXPORT_METHOD(updateNode:(NSString *)nodeId properties:(NSDictionary *)properties resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"updateNode: %@", nodeId);
    if ([UiNodesManager.instance updateNode:nodeId properties:properties]) {
        resolve(nil);
    } else {
        NSString *message = [NSString stringWithFormat:@"Could not update node \"%@\"", nodeId];
        reject(@"updateNodeError", message, nil);
    }
}

RCT_EXPORT_METHOD(addOnPressEventHandler:(NSString *)nodeId) {
    NSLog(@"addOnPressEventHandler: %@", nodeId);
    SCNNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiButtonNode class]]) {
        NSLog(@"startListeningToOnPressEvents: found button");
        UiButtonNode *button = (UiButtonNode *)node;
        button.onTap = ^(SCNNode *sender) {
            NSLog(@"button onTap");
            [[AREventsManager instance] onPressEventReceived:sender];
        };
    }
}

RCT_EXPORT_METHOD(removeOnPressEventHandler:(NSString *)nodeId) {
    NSLog(@"removeOnPressEventHandler: %@", nodeId);
    SCNNode *node = [UiNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[UiButtonNode class]]) {
        UiButtonNode *button = (UiButtonNode *)node;
        button.onTap = nil;
    }
}

@end

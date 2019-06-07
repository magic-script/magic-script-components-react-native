//
//  ARComponentManager.m
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 29/05/2019.
//  Copyright © 2019 HippoAR. All rights reserved.
//

#import "ARComponentManager.h"
#import "AREventsManager.h"
#import "RCTARKitNodes.h"
#import "RCTConvert+ARKit.h"
#import "RCTARKit-Swift.h"

static ARComponentManager *_instance = nil;

@interface ARComponentManager ()

@property(nonatomic, strong) NSMutableDictionary *callbackByNodeId;
//@property(nonatomic, copy) RCTResponseSenderBlock onPressCallback;

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


RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(createViewNode:(SCNNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"createViewNode: %@", nodeId);

    [MLNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createTextNode:(MLTextNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"createTextNode: %@", nodeId);
    [MLNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createButtonNode:(MLButtonNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"createButtonNode: %@", nodeId);
    [MLNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(createImageNode:(MLImageNode *)node nodeId:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"createImageNode: %@", nodeId);
    [MLNodesManager.instance registerNode: node nodeId: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addChildNode:(NSString *)nodeId toParentNode:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"addChildNode: %@ toParentNode: %@", nodeId, parentId);
    [MLNodesManager.instance addNode: nodeId toParent: parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addChildNodeToContainer:(NSString *)nodeId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"addChildNodeToContainer: %@", nodeId);
    [MLNodesManager.instance addNodeToRoot: nodeId];
    resolve(nil);
}

RCT_EXPORT_METHOD(clearScene:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"clearScene");
    [MLNodesManager.instance clear];
    resolve(nil);
}

RCT_EXPORT_METHOD(validateScene:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"validateScene");
    [MLNodesManager.instance validateScene];
    resolve(nil);
}

//RCT_EXPORT_METHOD(listenToOnPressEvents:(RCTResponseSenderBlock)callback) {
//    NSLog(@"listenToOnPressEvents");
//    self.onPressCallback = callback;
//}

RCT_EXPORT_METHOD(addOnPressEventHandler:(NSString *)nodeId) {
    NSLog(@"addOnPressEventHandler: %@", nodeId);
    SCNNode *node = [MLNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[MLButtonNode class]]) {
        NSLog(@"startListeningToOnPressEvents: found button");
        MLButtonNode *button = (MLButtonNode *)node;
        button.onTap = ^(SCNNode *sender) {
            NSLog(@"button onTap");
//            weakSelf.onPressCallback(@[[NSNull null], sender.name]);
            [[AREventsManager instance] onPressEventReceived:sender];
        };
    }
}

RCT_EXPORT_METHOD(removeOnPressEventHandler:(NSString *)nodeId) {
    NSLog(@"removeOnPressEventHandler: %@", nodeId);
    SCNNode *node = [MLNodesManager.instance findNodeWithId:nodeId];
    if (node && [node isKindOfClass:[MLButtonNode class]]) {
        MLButtonNode *button = (MLButtonNode *)node;
        button.onTap = nil;
    }
}




RCT_EXPORT_METHOD(addView:(SCNNode *)viewNode node:(SCNNode *)node frame:(NSString *)frame parentId:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"addView: %@ [parentId: %@]", viewNode.name, parentId);
    [node addChildNode:viewNode];
    [[RCTARKitNodes sharedInstance] addNodeToScene:node inReferenceFrame:frame withParentId:parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addButton:(MLButtonNode *)buttonNode node:(SCNNode *)node frame:(NSString *)frame parentId:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"addButton: %@ [parentId: %@]", buttonNode.name, parentId);
    [node addChildNode:buttonNode];
    [[RCTARKitNodes sharedInstance] addNodeToScene:node inReferenceFrame:frame withParentId:parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addImage:(MLImageNode *)imageNode node:(SCNNode *)node frame:(NSString *)frame parentId:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"addImage: %@ [parentId: %@]", imageNode.name, parentId);
    [node addChildNode:imageNode];
    [[RCTARKitNodes sharedInstance] addNodeToScene:node inReferenceFrame:frame withParentId:parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addText:(MLTextNode *)textNode node:(SCNNode *)node frame:(NSString *)frame parentId:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    NSLog(@"addText: %@ [parentId: %@]", textNode.name, parentId);
    [node addChildNode:textNode];
    [[RCTARKitNodes sharedInstance] addNodeToScene:node inReferenceFrame:frame withParentId:parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(unmount:(NSString *)identifier) {
    NSLog(@"unmounting node: %@ ", identifier);
    [[RCTARKitNodes sharedInstance] removeNode:identifier];
}

RCT_EXPORT_METHOD(addButtonPressEvent:(NSString *)buttonId callback:(RCTResponseSenderBlock)callback)
{
    if (!self.callbackByNodeId) {
        self.callbackByNodeId = [@{} mutableCopy];
    }
    self.callbackByNodeId[buttonId] = callback;
//    callback(@[[NSNull null], events]);
}

@end

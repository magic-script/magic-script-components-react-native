//
//  ARComponentManager.m
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 29/05/2019.
//  Copyright © 2019 HippoAR. All rights reserved.
//

#import "ARComponentManager.h"
#import "RCTARKitNodes.h"
#import "RCTConvert+ARKit.h"
#import "RCTARKit-Swift.h"

@interface ARComponentManager ()
@property(strong, nonatomic) NSMutableDictionary *callbackByNodeId;
@end

@implementation ARComponentManager

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

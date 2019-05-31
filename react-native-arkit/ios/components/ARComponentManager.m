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

RCT_EXPORT_METHOD(addButton:(MLButtonNode *)buttonNode node:(SCNNode *)node frame:(NSString *)frame parentId:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    [node addChildNode:buttonNode];
    [[RCTARKitNodes sharedInstance] addNodeToScene:node inReferenceFrame:frame withParentId:parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addImage:(MLImageNode *)imageNode node:(SCNNode *)node frame:(NSString *)frame parentId:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    [node addChildNode:imageNode];
    [[RCTARKitNodes sharedInstance] addNodeToScene:node inReferenceFrame:frame withParentId:parentId];
    resolve(nil);
}

RCT_EXPORT_METHOD(addText:(MLTextNode *)textNode node:(SCNNode *)node frame:(NSString *)frame parentId:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    [node addChildNode:textNode];
    [[RCTARKitNodes sharedInstance] addNodeToScene:node inReferenceFrame:frame withParentId:parentId];
    resolve(nil);
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

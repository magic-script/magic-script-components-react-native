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

@implementation ARComponentManager

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(mount:(MLButtonNode *)buttonNode node:(SCNNode *)node frame:(NSString *)frame parentId:(NSString *)parentId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject ) {
    [node addChildNode:buttonNode];
    [[RCTARKitNodes sharedInstance] addNodeToScene:node inReferenceFrame:frame withParentId:parentId];
    resolve(nil);
}

@end

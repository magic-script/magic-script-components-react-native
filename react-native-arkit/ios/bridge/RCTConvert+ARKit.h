//
//  RCTConvert+ARKit.h
//  RCTARKit
//
//  Created by Zehao Li on 9/28/17.
//  Copyright © 2017 HippoAR. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <SceneKit/SceneKit.h>
#import <React/RCTConvert.h>
#import <ARKit/ARKit.h>
#import "RCTARKit-Swift.h"

@interface SCNTextNode : SCNNode
@end


@interface RCTConvert (ARKit)

+ (SCNMaterial *)SCNMaterial:(id)json;

+ (SCNVector3)SCNVector3:(id)json;
+ (SCNVector4)SCNVector4:(id)json;
+ (SCNNode *)SCNNode:(id)json;

+ (SCNTextNode *)SCNTextNode:(id)json;
+ (UiGroupNode *)UiGroupNode:(id)json;
+ (UiButtonNode *)UiButtonNode:(id)json;
+ (UiTextNode *)UiTextNode:(id)json;
+ (UiImageNode *)UiImageNode:(id)json;

+ (void)setNodeProperties:(SCNNode *)node properties:(id)json;
+ (void)setMaterialProperties:(SCNMaterial *)material properties:(id)json;
+ (void)setLightProperties:(SCNLight *)light properties:(id)json;

@end


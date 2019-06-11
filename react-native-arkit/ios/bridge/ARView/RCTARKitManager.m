//
//  RCTARKitManager.m
//  RCTARKit
//
//  Created by HippoAR on 7/9/17.
//  Copyright © 2017 HippoAR. All rights reserved.
//

#import "RCTARKitManager.h"
#import "RCTARKit.h"

@implementation RCTARKitManager

RCT_EXPORT_MODULE()

- (UIView *)view {
    return [ARKit sharedInstance];
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (NSDictionary *)constantsToExport
{
    NSMutableDictionary * arHitTestResultType =
    [NSMutableDictionary dictionaryWithDictionary:
     @{
       @"FeaturePoint": @(ARHitTestResultTypeFeaturePoint),
       @"EstimatedHorizontalPlane": @(ARHitTestResultTypeEstimatedHorizontalPlane),
       @"ExistingPlane": @(ARHitTestResultTypeExistingPlane),
       @"ExistingPlaneUsingExtent": @(ARHitTestResultTypeExistingPlaneUsingExtent),
       }];
    NSMutableDictionary * arAnchorAligment =
    [NSMutableDictionary
     dictionaryWithDictionary:@{
                                @"Horizontal": @(ARPlaneAnchorAlignmentHorizontal)
                                }];
    NSMutableDictionary * arPlaneDetection =
    [NSMutableDictionary
     dictionaryWithDictionary:@{
                                @"Horizontal": @(ARPlaneDetectionHorizontal),
                                @"None": @(ARPlaneDetectionNone),
                                }];
    #if __IPHONE_OS_VERSION_MAX_ALLOWED >= 110300
    if (@available(iOS 11.3, *)) {
        [arHitTestResultType
         addEntriesFromDictionary:@{
                                    @"ExistingPlaneUsingGeometry": @(ARHitTestResultTypeExistingPlaneUsingGeometry),
                                    @"EstimatedVerticalPlane": @(ARHitTestResultTypeEstimatedVerticalPlane)
                                    }];
        [arPlaneDetection
         addEntriesFromDictionary:@{
                                    @"Vertical": @(ARPlaneDetectionVertical),
                                     @"HorizontalVertical": @(ARPlaneDetectionHorizontal + ARPlaneDetectionVertical),
                                    }];
        [arAnchorAligment
         addEntriesFromDictionary:@{
                                    @"Vertical": @(ARPlaneAnchorAlignmentVertical)
                                    }];
    }
      #endif

    return @{
             @"ARHitTestResultType": arHitTestResultType,
             @"ARPlaneDetection": arPlaneDetection,
             @"ARPlaneAnchorAlignment": arAnchorAligment,
             @"LightingModel": @{
                     @"Constant": SCNLightingModelConstant,
                     @"Blinn": SCNLightingModelBlinn,
                     @"Lambert": SCNLightingModelLambert,
                     @"Phong": SCNLightingModelPhong,
                     @"PhysicallyBased": SCNLightingModelPhysicallyBased
                     },
             @"LightType": @{
                     @"Ambient": SCNLightTypeAmbient,
                     @"Directional": SCNLightTypeDirectional,
                     @"Omni": SCNLightTypeOmni,
                     @"Probe": SCNLightTypeProbe,
                     @"Spot": SCNLightTypeSpot,
                     @"IES": SCNLightTypeIES
                     },
             @"ShadowMode": @{
                     @"Forward": [@(SCNShadowModeForward) stringValue],
                     @"Deferred": [@(SCNShadowModeDeferred) stringValue],
                     @"ModeModulated": [@(SCNShadowModeModulated) stringValue],
                     },
             @"ColorMask": @{
                     @"All": [@(SCNColorMaskAll) stringValue],
                     @"None": [@(SCNColorMaskNone) stringValue],
                     @"Alpha": [@(SCNColorMaskAlpha) stringValue],
                     @"Blue": [@(SCNColorMaskBlue) stringValue],
                     @"Red": [@(SCNColorMaskRed) stringValue],
                     @"Green": [@(SCNColorMaskGreen) stringValue],
                     },

             @"ShaderModifierEntryPoint": @{
                     @"Geometry": SCNShaderModifierEntryPointGeometry,
                     @"Surface": SCNShaderModifierEntryPointSurface,
                     @"LighingModel": SCNShaderModifierEntryPointLightingModel,
                     @"Fragment": SCNShaderModifierEntryPointFragment
                     },
             @"BlendMode": @{
                     @"Alpha": [@(SCNBlendModeAlpha) stringValue],
                     @"Add": [@(SCNBlendModeAdd) stringValue],
                     @"Subtract": [@(SCNBlendModeSubtract) stringValue],
                     @"Multiply": [@(SCNBlendModeMultiply) stringValue],
                     @"Screen": [@(SCNBlendModeScreen) stringValue],
                     @"Replace": [@(SCNBlendModeReplace) stringValue],
                     },
             @"TransparencyMode": @{
                     @"Default": [@(SCNTransparencyModeAOne) stringValue],
                     @"RGBZero": [@(SCNTransparencyModeRGBZero) stringValue],
                     @"SingleLayer": [@(SCNTransparencyModeSingleLayer) stringValue],
                     @"DualLayer": [@(SCNTransparencyModeDualLayer) stringValue],
                     },
             @"ChamferMode": @{
                     @"Both": [@(SCNChamferModeBoth) stringValue],
                     @"Back": [@(SCNChamferModeBack) stringValue],
                     @"Front": [@(SCNChamferModeBack) stringValue],

                     },
             @"ARWorldAlignment": @{
                     @"Gravity": @(ARWorldAlignmentGravity),
                     @"GravityAndHeading": @(ARWorldAlignmentGravityAndHeading),
                     @"Camera": @(ARWorldAlignmentCamera),
                     },
             @"FillMode": @{
                     @"Fill": [@(SCNFillModeFill) stringValue],
                     @"Lines": [@(SCNFillModeLines) stringValue],
                     },
             @"WrapMode": @{
                     @"Clamp": [@(SCNWrapModeClamp) stringValue],
                     @"Repeat": [@(SCNWrapModeRepeat) stringValue],
                     @"Mirror": [@(SCNWrapModeMirror) stringValue],
                     },
             @"Constraint": @{
                     @"None": @"0",
                     @"BillboardAxisAll": [@(SCNBillboardAxisAll) stringValue],
                     @"BillboardAxisX": [@(SCNBillboardAxisX) stringValue],
                     @"BillboardAxisY": [@(SCNBillboardAxisY) stringValue],
                     @"BillboardAxisZ": [@(SCNBillboardAxisZ) stringValue],
                     }
             };
}

RCT_EXPORT_VIEW_PROPERTY(debug, BOOL)
//RCT_EXPORT_VIEW_PROPERTY(planeDetection, ARPlaneDetection)
//RCT_EXPORT_VIEW_PROPERTY(origin, NSDictionary *)
//RCT_EXPORT_VIEW_PROPERTY(lightEstimationEnabled, BOOL)
//RCT_EXPORT_VIEW_PROPERTY(autoenablesDefaultLighting, BOOL)
RCT_EXPORT_VIEW_PROPERTY(rendersContinuously, BOOL)
//RCT_EXPORT_VIEW_PROPERTY(worldAlignment, NSInteger)
//RCT_EXPORT_VIEW_PROPERTY(detectionImages, NSArray *)

//RCT_EXPORT_VIEW_PROPERTY(onPlaneDetected, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onPlaneUpdated, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onPlaneRemoved, RCTBubblingEventBlock)
//
//RCT_EXPORT_VIEW_PROPERTY(onAnchorDetected, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onAnchorUpdated, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onAnchorRemoved, RCTBubblingEventBlock)
//
//RCT_EXPORT_VIEW_PROPERTY(onTrackingState, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onFeaturesDetected, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onLightEstimation, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onTapOnPlaneUsingExtent, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onTapOnPlaneNoExtent, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onEvent, RCTBubblingEventBlock)
//RCT_EXPORT_VIEW_PROPERTY(onARKitError, RCTBubblingEventBlock)

RCT_EXPORT_METHOD(pause:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [[ARKit sharedInstance] pause];
    resolve(@{});
}

RCT_EXPORT_METHOD(resume:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [[ARKit sharedInstance] resume];
    resolve(@{});
}

RCT_EXPORT_METHOD(reset:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [[ARKit sharedInstance] reset];
    resolve(@{});
}

RCT_EXPORT_METHOD(isInitialized:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    resolve(@([ARKit isInitialized]));
}

RCT_EXPORT_METHOD(isMounted:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    if( [ARKit isInitialized]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            resolve(@([[ARKit sharedInstance] isMounted]));
        });
    } else {
        resolve(@(NO));
    }
}

RCT_EXPORT_METHOD(getCamera:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    resolve([[ARKit sharedInstance] readCamera]);
}

RCT_EXPORT_METHOD(getCameraPosition:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    resolve([[ARKit sharedInstance] readCameraPosition]);
}

@end

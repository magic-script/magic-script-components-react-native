//
//  MLXrClientSession.m
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(MLXrClientSession, NSObject)

RCT_EXTERN_METHOD(connect:(NSString *)address deviceId:(NSString *)deviceId token:(NSString *)token resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(setUpdateInterval:(NSTimeInterval)interval resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(getAllAnchors:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(getAnchorByPcfId:(NSString *)pcfId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(getLocalizationStatus:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

@end

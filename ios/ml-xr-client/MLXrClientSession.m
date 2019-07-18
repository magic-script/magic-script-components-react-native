//
//  MLXrClientSession.m
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(MLXrClientSession, NSObject)

RCT_EXTERN_METHOD(connect:(NSString *)address deviceId:(NSString *)deviceId token:(NSString *)token callback:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(setUpdateInterval:(NSTimeInterval)updateInterval callback:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(getAllAnchors:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(getLocalizationStatus:(RCTResponseSenderBlock)callback)

@end

//
//  ARComponentManager.h
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 29/05/2019.
//  Copyright © 2019 HippoAR. All rights reserved.
//

#import <React/RCTBridgeModule.h>

@interface ARComponentManager : NSObject <RCTBridgeModule>

+ (instancetype)instance;

@end

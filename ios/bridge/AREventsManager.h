//
//  AREventsManager.h
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 07/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <SceneKit/SceneKit.h>

@class UiNode;

@interface AREventsManager : RCTEventEmitter <RCTBridgeModule>

+ (instancetype)instance;
- (void)onPressEventReceived:(UiNode *)sender;
- (void)onClickEventReceived:(UiNode *)sender;
- (void)onToggleChangedEventReceived:(UiNode *)sender value:(BOOL)value;

@end

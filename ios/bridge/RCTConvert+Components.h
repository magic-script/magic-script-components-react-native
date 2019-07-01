//
//  RCTConvert+Components.h
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 15/06/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import <React/RCTConvert.h>
#import "RNMagicScript-Swift.h"

@interface RCTConvert (Components)

+ (UiButtonNode *)UiButtonNode:(id)json;
+ (UiGroupNode *)UiGroupNode:(id)json;
+ (UiImageNode *)UiImageNode:(id)json;
+ (UiModelNode *)UiModelNode:(id)json;
+ (UiSpinnerNode *)UiSpinnerNode:(id)json;
+ (UiTextNode *)UiTextNode:(id)json;

@end

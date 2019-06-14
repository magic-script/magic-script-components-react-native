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

+ (UiGroupNode *)UiGroupNode:(id)json;
+ (UiButtonNode *)UiButtonNode:(id)json;
+ (UiTextNode *)UiTextNode:(id)json;
+ (UiImageNode *)UiImageNode:(id)json;

@end

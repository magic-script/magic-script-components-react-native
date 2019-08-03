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
+ (UiGridLayoutNode *)UiGridLayoutNode:(id)json;
+ (UiGroupNode *)UiGroupNode:(id)json;
+ (UiImageNode *)UiImageNode:(id)json;
+ (UiLineNode *)UiLineNode:(id)json;
+ (UiModelNode *)UiModelNode:(id)json;
+ (UiProgressBarNode *)UiProgressBarNode:(id)json;
+ (UiSliderNode *)UiSliderNode:(id)json;
+ (UiSpinnerNode *)UiSpinnerNode:(id)json;
+ (UiTextNode *)UiTextNode:(id)json;
+ (UiTextEditNode *)UiTextEditNode:(id)json;
+ (UiToggleNode *)UiToggleNode:(id)json;

@end

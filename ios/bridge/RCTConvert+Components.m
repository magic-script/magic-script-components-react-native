//
//  RCTConvert+Components.m
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 15/06/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "RCTConvert+Components.h"

@implementation RCTConvert (AR)

+ (UiButtonNode *)UiButtonNode:(id)json {
    return [[UiButtonNode alloc] initWithProps:json];
}

+ (UiGridLayoutNode *)UiGridLayoutNode:(id)json {
    return [[UiGridLayoutNode alloc] initWithProps:json];
}

+ (UiGroupNode *)UiGroupNode:(id)json {
    return [[UiGroupNode alloc] initWithProps:json];
}

+ (UiImageNode *)UiImageNode:(id)json {
    return [[UiImageNode alloc] initWithProps:json];
}

+ (UiLineNode *)UiLineNode:(id)json {
    return [[UiLineNode alloc] initWithProps:json];
}

+ (UiModelNode *)UiModelNode:(id)json {
    return [[UiModelNode alloc] initWithProps:json];
}

+ (UiProgressBarNode *)UiProgressBarNode:(id)json {
    return [[UiProgressBarNode alloc] initWithProps:json];
}

+ (UiSliderNode *)UiSliderNode:(id)json {
    return [[UiSliderNode alloc] initWithProps:json];
}

+ (UiSpinnerNode *)UiSpinnerNode:(id)json {
    return [[UiSpinnerNode alloc] initWithProps:json];
}

+ (UiTextNode *)UiTextNode:(id)json {
    return [[UiTextNode alloc] initWithProps:json];
}

+ (UiTextEditNode *)UiTextEditNode:(id)json {
    return [[UiTextEditNode alloc] initWithProps:json];
}

+ (UiToggleNode *)UiToggleNode:(id)json {
    return [[UiToggleNode alloc] initWithProps:json];
}

@end


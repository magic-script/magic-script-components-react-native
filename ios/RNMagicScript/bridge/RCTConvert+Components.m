//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

#import "RNMagicScript-Swift.h"
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

+ (UiScrollBarNode *)UiScrollBarNode:(id)json {
    return [[UiScrollBarNode alloc] initWithProps:json];
}

+ (UiScrollViewNode *)UiScrollViewNode:(id)json {
    return [[UiScrollViewNode alloc] initWithProps:json];
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

+ (UiVideoNode *)UiVideoNode:(id)json {
    return [[UiVideoNode alloc] initWithProps:json];
}

+ (UiDropdownListNode *)UiDropdownListNode:(id)json {
    return [[UiDropdownListNode alloc] initWithProps:json];
}

+ (UiDropdownListItemNode *)UiDropdownListItemNode:(id)json {
    return [[UiDropdownListItemNode alloc] initWithProps:json];
}

@end


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

#import <React/RCTConvert.h>

@class UiAudioNode;
@class UiButtonNode;
@class UiGridLayoutNode;
@class UiGroupNode;
@class UiImageNode;
@class UiLinearLayoutNode;
@class UiLineNode;
@class UiModelNode;
@class UiProgressBarNode;
@class UiRectLayoutNode;
@class UiScrollBarNode;
@class UiScrollViewNode;
@class UiSliderNode;
@class UiSpinnerNode;
@class UiTextNode;
@class UiTextEditNode;
@class UiToggleNode;
@class UiVideoNode;
@class UiDropdownListNode;
@class UiDropdownListItemNode;
@class UiDatePickerNode;
@class UiTimePickerNode;
@class UiColorPickerNode;
@class UiListViewNode;
@class UiListViewItemNode;

@interface RCTConvert (Components)
+ (UiAudioNode *)UiAudioNode:(id)json;
+ (UiButtonNode *)UiButtonNode:(id)json;
+ (UiGridLayoutNode *)UiGridLayoutNode:(id)json;
+ (UiGroupNode *)UiGroupNode:(id)json;
+ (UiImageNode *)UiImageNode:(id)json;
+ (UiLinearLayoutNode *)UiLinearLayoutNode:(id)json;
+ (UiLineNode *)UiLineNode:(id)json;
+ (UiModelNode *)UiModelNode:(id)json;
+ (UiProgressBarNode *)UiProgressBarNode:(id)json;
+ (UiRectLayoutNode *)UiRectLayoutNode:(id)json;
+ (UiScrollBarNode *)UiScrollBarNode:(id)json;
+ (UiScrollViewNode *)UiScrollViewNode:(id)json;
+ (UiSliderNode *)UiSliderNode:(id)json;
+ (UiSpinnerNode *)UiSpinnerNode:(id)json;
+ (UiTextNode *)UiTextNode:(id)json;
+ (UiTextEditNode *)UiTextEditNode:(id)json;
+ (UiToggleNode *)UiToggleNode:(id)json;
+ (UiVideoNode *)UiVideoNode:(id)json;
+ (UiDropdownListNode *)UiDropdownListNode:(id)json;
+ (UiDropdownListItemNode *)UiDropdownListItemNode:(id)json;
+ (UiDatePickerNode *)UiDatePickerNode:(id)json;
+ (UiTimePickerNode *)UiTimePickerNode:(id)json;
+ (UiColorPickerNode *)UiColorPickerNode:(id)json;
+ (UiListViewNode *)UiListViewNode:(id)json;
+ (UiListViewItemNode *)UiListViewItemNode:(id)json;
@end
